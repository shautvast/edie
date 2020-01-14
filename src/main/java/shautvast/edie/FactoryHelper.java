package shautvast.edie;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

public class FactoryHelper {

    /* unnamed counter */
    private static LongAdder globalCounter = new LongAdder();
    /* contains named counters */
    private static ConcurrentMap<String, LongAdder> namedCounters = new ConcurrentHashMap<>();
    /* util for random numbers*/
    private static Random random = new Random();

    /**
     * Values that need an index can be created with this helper method.
     * <p>
     * Example:
     * Factory.define(Person.class, () -> new Person("person[" + increment()+"]", 33));
     *
     * @return a number that increments on every invocation of the type supplier.
     */
    public static long increment() {
        return incrementAndGet(globalCounter);
    }

    /**
     * Creates an named incrementer that is called every time the Factory creates the object.
     * <p>
     * Example:
     * Factory.define(Person.class, () -> new Person("person[" + increment("personIndex")+"]", 33));
     *
     * @param name the name of the incrementer
     * @return a number that increments every time this method is called with the same name.
     */
    public static long increment(String name) {
        return incrementAndGet(namedCounters.computeIfAbsent(name, k -> new LongAdder()));
    }

    /**
     * Generate a random 64 bit integer number.
     * <p>
     * Example:
     * define(Person.class, () -> new Person("Sander", randomLong()));
     *
     * @return a random number on every invocation
     */
    public static long randomLong() {
        return random.nextLong();
    }

    /**
     * Generate a random 32 bit integer number.
     * <p>
     * Example:
     * define(Person.class, () -> new Person("Sander", randomInt()));
     *
     * @return a random number on every invocation
     */
    public static int randomInt() {
        return random.nextInt();
    }

    /**
     * Generate a random 32 bit integer number below an upper bound.
     * <p>
     * Example:
     * define(Person.class, () -> new Person("Sander", randomInt(100)));
     *
     * @param bound exlusive upper bound
     * @return a random number on every invocation
     */
    public static int randomInt(int bound) {
        return random.nextInt(bound);
    }

    /**
     * Generate a random 64 bit floating point number below an upper bound.
     *
     * @return a random number on every invocation
     */
    public static double randomDouble() {
        return random.nextDouble();
    }

    /**
     * Helper for the creation of factory types within a list.
     *
     * @param typesToConstruct Any types (should be known in Factory) that are to be elements in a List
     * @return A List of the same types. See
     */
    public static List<Class<?>> listOf(Class<?>... typesToConstruct) {
        Factory.checkTypes(typesToConstruct);
        return Arrays.asList(typesToConstruct);
    }

    private static long incrementAndGet(LongAdder longAdder) {
        long value = longAdder.longValue();
        longAdder.increment();
        return value;
    }
}
