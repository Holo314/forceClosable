package org.holo.forceClosable.runtime;

/**
 * A marker interface over {@link AutoCloseable}.
 * <p>
 * Any class implementing this interface has all the contracts
 * {@link AutoCloseable} with the addition that it is compiled
 * time enforced to use Try-with-Resources(TwR).
 * <p>
 * The purpose of this class is to enforce the lifetime of classes to be bounded
 * by the caller lifetime on compiletime.
 * <p>
 * !! Implementation note !!
 * The original idea was to allow to return {@link ForcingClosable} instead of TwR as well,
 * and then force the caller to use try with resources:
 * <pre>
 * public class Test
 *         implements ForcingClosable {
 *     public void close() {}
 *
 *     public static void main(String[] args) {
 *         try (var z = delegation()) {}
 *     }
 *
 *     public static Test delegation() {
 *         return new Test();
 *     }
 * }
 * </pre>
 * But that doesn't work, here is a simple example of the unsoundness:
 * <pre>
 * public class Test
 *         implements ForcingClosable {
 *     public void close() {} // doesn't each here
 *
 *     public static void main(String[] args) {
 *         try (var z = d(true)) {}
 *     }
 *
 *     public static Test d(boolean v) {
 *         var x = new Test();
 *         if (v) throw new RuntimeException();
 *         return x;
 *     }
 * }
 * </pre>
 * <p>
 * Opt-out is possible by explicit down-casting to {@link AutoCloseable}.
 * <p>
 * <p>
 * Apart from {@link ForcingClosable}, it is possible to add more classes/interfaces to act
 * like {@link ForcingClosable} by specify them in using the compiler argument "-XepOpt:ForcingTry".
 * The value of the parameter is a list of canonical names of classes.
 */
public interface ForcingClosable
        extends AutoCloseable {}
