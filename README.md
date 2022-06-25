# Kiwi

Easy-to-use Java utilities.

Dive into [Kiwi](./src/main/java/org/inlambda/kiwi/Kiwi.java) to see some features.

# Example

```java
        runAny(()->Files.writeString(Path.of("test.txt"),"Hello World")).orElseThrow();
        byLazy(()->"Some operations");
        byLazy(t->"some Ops");
        lazyProxy(String.class,()->"Object that lazy to be created");
        immutableBean("Setters will be disabled");
        protectObject("I cant be substringed",Pattern.compile("^substring.*"));
        pairOf("a","b");
        tripleOf("a","b","c");
        accessClass(File.class).method("getAbsolutePath",MethodType.methodType(String.class)); // access method with trusted lookups
        accessClass(File.class).virtualField("path"); // access field with their setter or unsafe.
        Kiwi.toString("Invoke their toString or serialize them");

        string(11); // random string with length 11
        number(0,1000); // random number between 0 and 1000
        number(); // random number with 4 digits
        string(); // random string with length 16
        pick(List.of("some random objects")).orElseThrow(); // pick one of the objects in the list
        pickOrNull(List.of("There is nothing")); // pick one of the objects in the list or null

        var weakHashSet=new WeakHashSet<>(); // hashset implementation based on Weak one.
        weakHashSet.disableResizing();

        var positiveRange=IntRange.rangePositive();
        positiveRange.isInRange(11); // true
        positiveRange.random(); // get a random number in this range.
        todo(); // throw exception
```