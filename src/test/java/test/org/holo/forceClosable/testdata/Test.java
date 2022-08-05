package test.org.holo.forceClosable.testdata;

import org.holo.forceClosable.runtime.ForcingClosable;

import java.util.concurrent.atomic.AtomicReference;

public class Test
        implements ForcingClosable {
    public void close() {}
}

class Test0
        implements AutoCloseable {
    public void close() {}
}

class Toast {
    public void bar() {
        try (var x = new Test()) {}
        final var y = new Test(); // erroneous - Test implements ForcingClosable
        try (var x0 = new Test0()) {}
        final var y0 = new Test0(); // not erroneous
        try (var x1 = new Test1()) {}
        final var y1 = new Test1() {}; // erroneous - Test1$1 is specified in the compiler argument
        try (var x1 = new AtomicReference<>(new Test1()).get()) {}
    }
}