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

## FastLoc2ObjMap

A simple map implementation utilizing `Long2ObjectOpenHashMap`  
You'd better use it directly instead of using a `Map` interface.

- `entrySet` and `keySet` is unsupported.
- `yaw` and `pitch` is ignored.

```java
Benchmark Mode Cnt Score Error Units
        k.bukkit.FastLocHashBench.bukkitHashCode thrpt 3 147433.805 ± 30120.825ops/s
        k.bukkit.FastLocHashBench.kiwiHashCode thrpt 3 154101.057 ±  6928.009ops/s
        k.collection.Loc2ObjMaps.commonHashMapRead thrpt 3 54639.759 ± 13169.074ops/s
        k.collection.Loc2ObjMaps.commonHashMapWrite thrpt 3 55941.784 ± 48699.401ops/s
        k.collection.Loc2ObjMaps.kiwiHashMapRead thrpt 3 74833.467 ±  6228.279ops/s
        k.collection.Loc2ObjMaps.kiwiHashMapWrite thrpt 3 54215.283 ±  6905.586ops/s

```