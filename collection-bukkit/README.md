# collection::bukkit

This project is aimed to provide some bukkit-specialized collections, which are excepted to be more performant and
faster.

Each "faster" collection has its own JMH benchmark. You can check or run them to see if it is faster than common
solutions.

# Benchmark Environment

```
CPU: AMD Ryzen 7 4800U with Radeon Graphics (16) @ 1.800GHz
Memory: 15357MiB
OS: Arch Linux with latest GNU/Linux Kernel
JVM: JDK 17.0.2, OpenJDK 64-Bit Server VM, 17.0.2+8-LTS (zulu, not zulu prime)
```

## GsonPersistenceDataType

```java

@RequiredArgsConstructor
public class GsonPersistenceDataType<Z> implements PersistentDataType<String, Z> {
    @Getter
    private final Class<Z> complexType;
    private final Gson parser;
    // ...
}
```

## PlayerMap

```java
    static<T> PlayerMap<T> createWeakMap()
static<T> PlayerMap<T> createUUIDBasedMap()
static<T> PlayerMap<T> createPDCBasedMap(NamespacedKey key,PersistentDataType<?, T> type)
```

## FastLoc2ObjMap

A simple map implementation utilizing `Long2ObjectOpenHashMap`  
You'd better use it directly instead of using a `Map` interface.

- `entrySet` and `keySet` is unsupported.
- `yaw` and `pitch` is ignored.

```java
Benchmark                                     Mode  Cnt       Score       Error  Units
        k.bukkit.FastLocHashBench.bukkitHashCode     thrpt    3  140595.936 ± 33986.083  ops/s
        k.bukkit.FastLocHashBench.kiwiHashCode       thrpt    3  154351.619 ± 39776.544  ops/s
        k.collection.Loc2ObjMaps.commonHashMapRead   thrpt    3   62656.235 ± 39366.216  ops/s
        k.collection.Loc2ObjMaps.commonHashMapWrite  thrpt    3   57711.273 ± 13330.564  ops/s
        k.collection.Loc2ObjMaps.kiwiHashMapRead     thrpt    3   83885.242 ± 50416.401  ops/s
        k.collection.Loc2ObjMaps.kiwiHashMapWrite    thrpt    3   75764.393 ±  8659.842  ops/s


```