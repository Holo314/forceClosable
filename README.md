# forceClosable

This project is an [error-prone](https://errorprone.info/docs/installation) plugin to for the usage
of [Try-with-Resources (TwR)](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html).

The project adds to your runtime a single empty
interface [ForcingClosable](src/main/java/org/holo/forceClosable/runtime/ForcingClosable.java) that extends
AutoClosable.

When activating the compiler plugin, all usage of instances
of [ForcingClosable](src/main/java/org/holo/forceClosable/runtime/ForcingClosable.java) that are not directly in a TwR
clause will fail at compiletime:

```java
import java.util.concurrent.atomic.AtomicReference;

public class Test
        implements ForcingClosable {
    public void close() {}

    public static void main(String[] args) {
        try (var x0 = new Test()) {
            // Okay
        }
        var x1 = new Test(); // Fail, not TwR clause
        try (var x2 = new AtomicReference<>(new Test()).get()) {
            // Fail, the instance of Test is not directly in the clause
        }
    }
}
```

In addition, you can use the compiler parameter `-XepOpt:ForcingTry` to make existing classes to act similarly to how [ForcingClosable](src/main/java/org/holo/forceClosable/runtime/ForcingClosable.java) works. The value of the flag is comma seperated list of [canonical names](https://docs.oracle.com/javase/specs/jls/se11/html/jls-6.html#jls-6.7).