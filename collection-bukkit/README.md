# collection::bukkit

This project is aimed to provide some bukkit-specialized collections, which are excepted to be more performant and
faster.

Each "faster" collection has its own JMH benchmark. You can check or run them to see if it is faster than common
solutions.

## FastLoc2ObjMap

A simple map implementation utilizing `Long2ObjectOpenHashMap`  
You'd better use it directly instead of using a `Map` interface.

- `entrySet` and `keySet` is unsupported.
- `yaw` and `pitch` is ignored.

```java
Benchmark Mode Cnt Score Error Units
        k.bukkit.FastLocHashBench.bukkitHashCode thrpt 3 142457.895 ±  6561.469ops/s
        k.bukkit.FastLocHashBench.kiwiHashCode thrpt 3 154187.825 ± 49439.283ops/s

        k.collection.Loc2ObjMaps.commonHashMapRead thrpt 3 66207.903 ±  8220.051ops/s
        k.collection.Loc2ObjMaps.commonHashMapWrite thrpt 3 58502.088 ±  5821.166ops/s

        k.collection.Loc2ObjMaps.kiwiHashMapRead thrpt 3 72760.796 ± 70385.124ops/s
        k.collection.Loc2ObjMaps.kiwiHashMapWrite thrpt 3 78748.177 ± 54803.260ops/s
```