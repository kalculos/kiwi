# Kiwi

简单好用强大的 Java 工具，请配合静态导入食用。

Dive into [Kiwi](core/src/main/java/org/inlambda/kiwi/Kiwi.java) to see some features.

# Features

## runAny and fromAny

`runAny` 帮助你运行任意 Java 代码而不用去写没有必要的 try...catch。

```java
Kiwi.runAny(()->{
        Files.writeString(Path.of("output.txt"),"Hello, world!");
        }).orElseThrow();
```

`fromAny` 类似于 `runAny`，可以运行任意 Java 代码并且返回 `Result`。

## Future, Promise and Result

我们还提供了比 JDK 更好的 `Future<R,E>` 和 `Promise<R,E>` 实现，只需要几行代码继承 `AbstractPromise` 就可以无缝接入项目中。

```java
fetchOrder()
        .sync() // returns Result<T,E>
        // or
        .onComplete/onSuccess/onFailure

// and
        promise.success(result);
```

你也可以使用 `Result<T,E>`(implements Future) 帮助你处理异常。

```java
public Result<HttpResponse, HttpException> handle(HttpRequest req){
        // ... some business logics
        return Result.ok(HttpResponse.of(200,"OK"));
        // or
        return Result.fail();
        }

        connectDatabase().and(handle(xx))
        .fail(e->e.printStackTrace())
        .success(response->..do something)
```

## Collection

Kiwi 提供了~~你四处寻觅的~~` Pair<A,B>` 和 `Triple<A,B,C>`

```java
Kiwi.pairOf(...)
        Kiwi.tripleOf(...)
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

*尚未稳定，暂时移除*

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

也可以帮你检查空参数

```java

// source
@NoNullExcepted
public Connection createConnection(String jdbcUrl,@Nullable Context context){
        //....
        }

// de-compiled class:
public Connection createConnection(String jdbcUrl,@Nullable Context context){
        Objects.requireNonNull(jdbcUrl,"jdbcUrl cannot be null")
        //....
        }
```

也可以把事情推后再做。

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