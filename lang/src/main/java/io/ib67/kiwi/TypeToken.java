/*
 * MIT License
 *
 * Copyright (c) 2025 Kalculos and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.ib67.kiwi;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.*;

/**
 * A TypeToken captures complete type signatures from where it can be represented. It is a representation
 * of a type with all its type parameters "resolved", which means you don't have the need to handle TypeVariables
 * like K,V from `Map K,V` since they are already identified from context.
 * Usage: {@snippet lang = java:
 *     // an example for List<E>
 *     var type = new TypeToken<List<? extends CharSequence>>() {
 *     }; // should notice that it's subclassing TT
 *     System.out.println(type); // prints List<? extends CharSequence>
 *     System.out.println(type.resolveReturnValue(List.class.getMethod("get", int.class))); // prints ? extends CharSequence
 *     // Sometimes you won't want to handle wildcard bounds, use reduceBounds.
 *     var newToken = TypeToken.reduceBounds(type, false);
 *     System.out.println(newToken); // prints List<ChatSequence>
 *     System.out.println(newToken.resolveReturnValue(List.class.getMethod("get", int.class))); // prints CharSequence.
 *     // please refer to javadoc for more usages.
 *}
 *
 * @param <C>
 * @author iceBear67
 */
@ApiStatus.AvailableSince("1.0.0")
public class TypeToken<C> {
    public enum WildcardKind {
        EXTENDS, SUPER
    }

    private static final int MASK_ARRAY = 1;
    private static final int MASK_WILDCARD_EXTENDS = 1 << 1;
    private static final int MASK_WILDCARD_SUPER = 1 << 2;
    private static final TypeToken<?>[] EMPTY = new TypeToken[0];
    private static final TypeToken<Object> OBJECT = new TypeToken<>(Object.class);
    private static final Map<Type, TypeToken<?>> CACHED = new WeakHashMap<>();
    private Class<?> baseTypeRaw;
    private TypeToken<?>[] typeParams;
    private int hashCode;
    private int flags = 0;

    /**
     * Copy constructor
     *
     * @param anotherToken tt to be copied
     */
    public TypeToken(TypeToken<C> anotherToken) {
        Objects.requireNonNull(anotherToken);
        this.baseTypeRaw = anotherToken.baseTypeRaw;
        this.typeParams = Arrays.copyOf(anotherToken.typeParams, anotherToken.typeParams.length);
        this.hashCode = anotherToken.hashCode();
        this.flags = anotherToken.flags;
    }

    private TypeToken(Type type) {
        resolveTypeToken(type);
        hashCode = hash(baseTypeRaw, typeParams, flags);
    }

    private TypeToken(Class<?> selfTypeRaw, TypeToken<?>... typeParams) {
        Objects.requireNonNull(selfTypeRaw);
        Objects.requireNonNull(typeParams);
        this.baseTypeRaw = selfTypeRaw;
        this.typeParams = typeParams;
        if (selfTypeRaw.isArray()) {
            this.flags |= MASK_ARRAY;
        }
        hashCode = hash(selfTypeRaw, typeParams, flags);
    }

    /**
     * This constructor is meant to be invoked when subclassing construction is used.
     */
    protected TypeToken() {
        this((Type) null);
    }

    /**
     * Construct a TypeToken by provided data without additional check. May lead to potential {@link ClassCastException}
     * For most situations, please use subclass construction instead.
     *
     * @param type             type
     * @param actualTypeParams params
     * @param <C>              raw type param
     * @return typeToken
     */
    public static <C> TypeToken<C> getParameterized(Class<C> type, Type... actualTypeParams) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(actualTypeParams);
        var subTokens = new TypeToken<?>[actualTypeParams.length];
        var params = type.getTypeParameters();
        if (params.length != actualTypeParams.length) {
            throw new IllegalArgumentException("Type parameters don't match");
        }
        synchronized (CACHED) {
            for (int i = 0; i < params.length; i++) {
                subTokens[i] = CACHED.computeIfAbsent(actualTypeParams[i], TypeToken::new);
            }
        }
        return new TypeToken<>(type, subTokens);
    }

    /**
     * Sink a TypeToken tree into a unique form by removing bounds found in the tree.
     * May be useful if you don't want to handle wildcard bounds.
     * Conversion rule for a given TT:
     * 1. If wildcard == SUPER,
     * if liftBySuper is false, it will be lifted into {@link TypeToken#OBJECT}
     * else it will be lifted into its superclass.
     * 2. If wildcard == EXTENDS, it will be lifted into its upper bound. (by default its object)
     * 3. If TT isn't wildcard, copy it and map {@link TypeToken#typeParams} with this method.
     *
     * @param type type to be reduced
     * @return a newly created reduced TypeToken.
     */
    @Contract("_, _ -> new")
    public static TypeToken<?> reduceBounds(TypeToken<?> type, boolean liftBySuper) {
        Objects.requireNonNull(type);
        return switch (type.getWildcardKind()) {
            case SUPER ->
                    liftBySuper ? TypeToken.reduceBounds(type.typeParams[0].resolveDirectParent(), liftBySuper) : OBJECT;
            case EXTENDS -> TypeToken.reduceBounds(type.typeParams[0], liftBySuper);
            case null -> {
                var copiedToken = new TypeToken<>(type);
                var params = copiedToken.typeParams;
                for (int i = 0; i < params.length; i++) {
                    params[i] = reduceBounds(params[i], liftBySuper);
                }
                copiedToken.hashCode = hash(copiedToken.baseTypeRaw, params, copiedToken.flags);
                yield copiedToken;
            }
        };
    }

    /**
     * Accepts a type then resolves it as a TypeToken.
     */
    @SuppressWarnings("unchecked")
    public static <C> TypeToken<C> resolve(Type type) {
        synchronized (CACHED) {
            return (TypeToken<C>) CACHED.computeIfAbsent(type, TypeToken::resolve0);
        }
    }

    /**
     * This method _creates_ TT from a type while {@link TypeToken#resolveTypeToken(Type)} resolves from
     * TT subclass itself.
     */
    @SuppressWarnings("unchecked")
    private static <C> TypeToken<C> resolve0(Type type) {
        return switch (type) {
            case Class<?> clazz -> {
                if (clazz == Object.class) yield (TypeToken<C>) OBJECT;
                var params = clazz.getTypeParameters();
                if (params.length == 0) yield new TypeToken<>(clazz, EMPTY);
                var subTokens = new TypeToken<?>[params.length];
                for (int i = 0; i < params.length; i++) {
                    var param = params[i];
                    subTokens[i] = TypeToken.resolve(param);
                }
                yield new TypeToken<>(clazz, subTokens);
            }
            case TypeVariable<?> typeVariable -> {
                var bounds = typeVariable.getBounds(); //todo upper and super bound?
                yield bounds.length == 0 ? (TypeToken<C>) OBJECT : TypeToken.resolve(bounds[0]);
            }
            case WildcardType wildcardType -> new TypeToken<>(wildcardType);
            case ParameterizedType parameterizedType -> {
                var self = parameterizedType.getRawType();
                if (self == Object.class) yield (TypeToken<C>) OBJECT;
                var actualParam = parameterizedType.getActualTypeArguments();
                var subTokens = new TypeToken<?>[actualParam.length];
                for (int i = 0; i < subTokens.length; i++) {
                    subTokens[i] = resolve(actualParam[i]);
                }
                yield new TypeToken<>((Class<?>) self, subTokens);
            }
            default -> throw new IllegalStateException("Unexpected value: " + type + ", type: " + type.getClass());
        };
    }

    /**
     * Initialization of the current typeToken.
     */
    private void resolveTypeToken(Type typeParam) {
        if (typeParam == null)
            typeParam = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        switch (typeParam) {
            case ParameterizedType parameterizedType -> {
                if (!(parameterizedType.getRawType() instanceof Class<?> clazz)) {
                    throw new IllegalStateException("Not a class, it is a " + parameterizedType.getRawType());
                }
                this.baseTypeRaw = clazz;
                var pms = parameterizedType.getActualTypeArguments();
                var tokens = new TypeToken<?>[pms.length];
                synchronized (CACHED) {
                    for (int i = 0; i < tokens.length; i++) {
                        tokens[i] = CACHED.computeIfAbsent(pms[i], TypeToken::new);
                    }
                }
                this.typeParams = tokens;
            }
            case Class<?> clazz -> {
                this.typeParams = EMPTY;
                baseTypeRaw = clazz;
                if (clazz.isArray()) flags |= MASK_ARRAY;
            }
            case WildcardType wildcardType -> {
                var upperBounds = wildcardType.getUpperBounds();
                var lowerBounds = wildcardType.getLowerBounds();
                baseTypeRaw = null;
                typeParams = new TypeToken[1];
                flags |= MASK_WILDCARD_EXTENDS;
                typeParams[0] = resolve(upperBounds[0]);
                if (upperBounds[0] == Object.class) {
                    if (lowerBounds.length != 0) {
                        typeParams[0] = resolve(lowerBounds[0]);
                        flags &= ~MASK_WILDCARD_EXTENDS;
                        flags |= MASK_WILDCARD_SUPER;
                    }
                }
            }
            case GenericArrayType arrayType -> {
                resolveTypeToken(arrayType.getGenericComponentType());
                flags |= MASK_ARRAY;
            }
            default -> throw new IllegalStateException("Unsupported: " + typeParam + " (" + typeParam.getClass() + ")");
        }
    }

    /**
     * Resolve the concrete type of field by its de-parameterized belonging class.
     *
     * @param field field
     * @return type of field in context
     */
    public TypeToken<?> resolveField(Field field) {
        return resolveType(field.getGenericType());
    }

    /**
     * Resolve the concrete type of return value by its de-parameterized belonging class.
     *
     * @param method method
     * @return type of the return value in context.
     */
    public TypeToken<?> resolveReturnValue(Method method) {
        return resolveType(method.getGenericReturnType());
    }

    /**
     * Resolve a type in the context of current TypeToken.
     * {@snippet lang = java:
     *         class A<T> {
     *             List<List<T>> fieldA;
     *         }
     *         var token = new TypeToken<A<Integer>>() {};
     *         var fieldType = token.resolveField(A.class.getDeclaredField("fieldA"));
     *         assertEquals("List<List<Integer>>", fieldType.toString());
     *}
     *
     * @param type typeVar or class to be resolved.
     * @return typeToken
     */
    public TypeToken<?> resolveType(Type type) {
        Objects.requireNonNull(type);
        if (type instanceof TypeVariable<?> typeVar) {
            var params = baseTypeRaw.getTypeParameters();
            for (int i = 0; i < params.length; i++) { //todo doc this order
                if (params[i] == typeVar) return typeParams[i];
            }
            throw new IllegalArgumentException("Unresolved TypeVariable or this TypeVar isn't belong to the type.");
        }
        //todo test
        if (type instanceof ParameterizedType parameterizedType) {
            var typeArgs = parameterizedType.getActualTypeArguments();
            var params = new TypeToken[typeArgs.length];
            for (int i = 0; i < typeArgs.length; i++) {
                params[i] = resolveType(typeArgs[i]);
            }
            return new TypeToken<>((Class<?>) parameterizedType.getRawType(), params);
        }
        if (type instanceof WildcardType wildcardType) {
            throw new IllegalArgumentException("Wildcard types are not supported for resolveType yet.");
        }
        return new TypeToken<>(type);
    }

    /**
     * Resolve a generic type whose type parameters can be inferred from current type.
     * {@snippet lang = java:
     *         class A<T1, T2> implements Z<T2> {}
     *         class B<T2> extends A<String, T2> {}
     *         class C extends B<Integer> {}
     *         var token = TypeToken.resolve(C.class);
     *         assertEquals("A<String,Integer>", token.inferType(A.class));
     *}
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public TypeToken<? super C> inferType(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        if (clazz == baseTypeRaw) return this;
        if (this.isWildcard())
            throw new IllegalArgumentException("Cannot flatten a wildcard type.");
        if (baseTypeRaw == null)
            throw new IllegalArgumentException("This TypeToken doesn't have a baseType. Is it a type variable?");
        if (!clazz.isAssignableFrom(baseTypeRaw))
            throw new IllegalArgumentException("The base type of this TypeToken is not assignable to " + clazz);
        //todo superclass/interfaces is null if selfTypeRaw is a interface since interfaces doesn't extend Object
        Deque<Type> path = new ArrayDeque<Type>(8);
        var success = findPathToSuper(path, clazz.isInterface(), baseTypeRaw, clazz);
        if (!success) {
            throw new IllegalArgumentException("Cannot find a path in hierarchy tree to " + clazz);
        }
        TypeToken<?> token = this;
        path = path.reversed();
        path.pop();
        for (Type type : path) {
            token = token.resolveGenericParent(type);
        }
        return (TypeToken<? super C>) token;
    }

    /**
     * This method supports only direct superclass/interfaces.
     * for most use-cases, see {@link TypeToken#inferType (Class)}
     */
    @SuppressWarnings("unchecked")
    private TypeToken<? super C> resolveGenericParent(Type type) {
        Objects.requireNonNull(type);
        return switch (type) {
            case ParameterizedType parameterizedType -> {
                var clazz = (Class<? super C>) parameterizedType.getRawType();
                var actualTypeArgs = parameterizedType.getActualTypeArguments();
                var tokenParams = new TypeToken[actualTypeArgs.length];
                for (int i = 0; i < actualTypeArgs.length; i++) {
                    var actualTypeArg = actualTypeArgs[i];
                    tokenParams[i] = resolveType(actualTypeArg);
                }
                yield new TypeToken<>(clazz, tokenParams);
            }
            case Class<?> clz -> TypeToken.resolve(clz);
            default -> throw new IllegalStateException("Unexpected value: " + baseTypeRaw.getGenericSuperclass());
        };
    }

    @Nullable
    public TypeToken<? super C> resolveDirectParent() {
        if (this.isWildcard())
            throw new IllegalArgumentException("Wildcard types are not supported for resolveType yet.");
        if (baseTypeRaw == Object.class) return null;
        if (this.baseTypeRaw.isInterface()) {
            throw new IllegalArgumentException("Interfaces don't have superclasses.");
        }
        return resolveGenericParent(baseTypeRaw.getGenericSuperclass());
    }

    /**
     * Utility method to find a path from `type` to `clazz` without involving unnecessary TT creation.
     * clazz may be assignable from type
     *
     * @param typeDeque     the path deque
     * @param findInterface also perform DFS on interfaces.
     * @param type          where we begin the search
     * @param clazz         target superclass
     * @return succeed or not
     */
    protected static boolean findPathToSuper(Deque<Type> typeDeque, boolean findInterface, Type type, Class<?> clazz) {
        if (clazz.getTypeParameters().length == 0) throw new IllegalArgumentException("clazz must be generic");
        typeDeque.push(type);
        Class<?> clz;
        if (type instanceof ParameterizedType parameterizedType) {
            clz = (Class<?>) parameterizedType.getRawType();
        } else {
            clz = (Class<?>) type;
        }
        if (clz == clazz) return true;
        var genericSuper = clz.getGenericSuperclass();
        if (genericSuper != null) { // for Object and interfaces.
            if (findPathToSuper(typeDeque, findInterface, clz.getGenericSuperclass(), clazz)) {
                return true;
            }
        }
        if (findInterface) {
            for (Type genericInterface : clz.getGenericInterfaces()) {
                if (findPathToSuper(typeDeque, findInterface, genericInterface, clazz)) {
                    return true;
                }
            }
        }
        typeDeque.pop();
        return false;
    }

    /**
     * @return an immutable list view of type params
     */
    public List<TypeToken<?>> getTypeParams() {
        return List.of(typeParams);
    }

    public boolean isArray() {
        return (flags & MASK_ARRAY) != 0;
    }

    public boolean isWildcard() {
        return getWildcardKind() != null;
    }

    /**
     * @return a {@link Class} corresponding to the type.
     */
    @Nullable
    public Class<?> getBaseTypeRaw() {
        return baseTypeRaw;
    }

    @Nullable
    public WildcardKind getWildcardKind() {
        if ((flags & MASK_WILDCARD_EXTENDS) != 0) {
            return WildcardKind.EXTENDS;
        }
        if ((flags & MASK_WILDCARD_SUPER) != 0) {
            return WildcardKind.SUPER;
        }
        return null;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        switch (getWildcardKind()) {
            case EXTENDS -> {
                sb.append('?');
                if (typeParams[0].getBaseTypeRaw() != Object.class) {
                    sb.append(" extends ").append(typeParams[0]);
                }
            }
            case SUPER -> sb.append("? super ").append(typeParams[0]);
            case null -> {
                sb.append(baseTypeRaw.getSimpleName());
                if (typeParams.length != 0) {
                    sb.append('<');
                    for (int i = 0; i < typeParams.length; i++) {
                        sb.append(typeParams[i].toString());
                        if (i != typeParams.length - 1) {
                            sb.append(", ");
                        }
                    }
                    sb.append('>');
                }
            }
        }
        // generic array kind
        if (!this.isWildcard() && !this.baseTypeRaw.isArray() && this.isArray())
            sb.append("[]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof TypeToken<?> tk) {
            return tk.baseTypeRaw == this.baseTypeRaw
                    && Arrays.equals(tk.typeParams, this.typeParams)
                    && flags == tk.flags;
        }
        return false;
    }

    private static int hash(Class<?> selfTypeRaw, TypeToken<?>[] typeParams, int flags) {
        var hashCode = 33;
        hashCode = 31 * hashCode + (selfTypeRaw == null ? 0 : selfTypeRaw.hashCode());
        hashCode = 31 * hashCode + (typeParams == null ? 0 : Arrays.hashCode(typeParams));
        hashCode = 31 * hashCode + flags;
        return hashCode;
    }
}