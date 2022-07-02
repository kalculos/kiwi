# Kiwi

简单好用强大的 Java 工具，请配合静态导入食用。

Dive into [Kiwi](./src/main/java/org/inlambda/kiwi/Kiwi.java) to see some features.

# Features

## runAny and fromAny

`runAny` 帮助你运行任意 Java 代码而不用去写没有必要的 try...catch。

```java
Kiwi.runAny(()->{
        Files.writeString(Path.of("output.txt"),"Hello, world!");
        }).orElseThrow();
```

`fromAny` 类似于 `runAny`，可以运行任意 Java 代码并且返回 `Result`。

## Error Handling

Kiwi 提供 `Result<T,E>` 帮助你处理异常。

```java
public Result<HttpResponse, HttpException> handle(HttpRequest req){
        // ... some business logics
        return Result.ok(HttpResponse.of(200,"OK"));
        // or
        return Result.err();
        }

        connectDatabase().and(handle(xx))
        .fail(e->e.printStackTrace())
        .success(response->..do something)
```

与 Result/Optional 类似，但 `Option` 适用于跟 null 打交道。

```java
Option.of(somethingNullable)
        .map(s->s.toUpperCase())
        .orElse("default");

        Option.none()
        .If(e->xx,e->do xx)
        .Case(E->anotherValue,anotherValue->xx)
        .IfCast(Player.class,optionOfPlayer->xx)
        .IfNotNull(t->xx)
        ........
```

配合 JDK Pattern Matching 使用更佳，因为 Option 是分类型的。（Some 和 None）

## Collection

Kiwi 提供了~~你四处寻觅的~~` Pair<A,B>` 和 `Triple<A,B,C>`

```java
Kiwi.pairOf(...)
        Kiwi.tripleOf(...)
```

以及更快的 `Stack<E>`

```java
var stack=new LinkedOpenStack<>();
```

还有 `WeakHashMap` 对应的 `WeakHashSet`

```java
var set=new WeakHashSet<>();
        set.disableResizing(); // 取消扩容
```

以及常用的 `lazyBy`

```java
Function<K, V> a=Kiwi.byLazy(l->xx);
        Supplier<V> b=Kiwi.byLazy(()->xx);
```

... 和 `lazyProxy`

```java
var luckyPlayer=Kiwi.lazyProxy(Player.class,()->RandomHelper.pick(players))
```

## Magics

Kiwi 支持为类生成标准 JSON 格式的 `toString` 方法，极限效率，远超反射。

```java

@Jsonized // 指示为该类生成序列化为 JSON 的代码
@RequiredArgsConstructor
public final class MyResponse {
    private final boolean success;
    private final String message;
}

    var excepted = "{\"success\":true,\"message\":\"\\\"Hello, World\\\"\"}""; 
        new MyResponse(true,"Yes! \"Hello, world!\"").toString().equals(excepted);
```

Kiwi 支持你把事情推后再做。

```java
public XXService importantMethod(){
        return Kiwi.todo("optional comments"); // throws exception to interrupt this, better than returning a null. 
        }
```

## Reflection

Kiwi 提供了简单好用的反射工具。

```java
Kiwi.accessClass(XX.class)
        .virtualField("...")
        .get()/set(...) // 会寻找 Getter/Setter, 其次会使用 Unsafe 操作字段
```

---

...   
但 Kiwi 不能告诉你全部，你要自己上手体验 Kiwi。

# Contributing

Kiwi 一直在成长，有你的参与，它能变得更好。

你可以在 Issues/PR 提新功能的建议，这些建议经过评估后会被加入 Kiwi 中。  
此外，Kiwi 的目标是保持轻量而强大，因此我们总是避免无必要的添加。

<3