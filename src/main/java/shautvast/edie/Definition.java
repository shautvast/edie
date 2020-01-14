package shautvast.edie;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Contains the specification for creating instances. They are either Supplier lambda or a Constructor instance.
 */
public class Definition<T> {

    /* contains the class instances for creating elements in a List */
    private final List<Class<?>> typesInList = new ArrayList<>();

    /* type of the object that will be created*/
    private Class<T> type;

    /* constructor class for creating instances*/
    private Constructor<T> constructor;

    /* lambda that can be used to create instances*/
    private Supplier<T> template;

    /*
     * Constructor that takes a type. The constructor member variable must be added later on.
     *
     * @param type Type of the instances this Definition will create.
     */
    Definition(Class<T> type) {
        this.type = type;
    }

    /*
     * Constructor that takes a Supplier lambda to create instances.
     *
     * @param supplier
     */
    Definition(Supplier<T> supplier) {
        this.template = supplier;
    }

    /**
     * Used to create the instance, once the definition is constructed.
     * Can be called directly, or via the Factory.build() method
     *
     * @return a new instance by calling the supplier or a constructor.
     */
    @SuppressWarnings("unchecked")
    public T build() {
        if (template != null) {
            return template.get();
        } else if (constructor != null) {
            if (!typesInList.isEmpty()) {
                return (T) newInstance(new Object[]{
                        typesInList.stream()
                                .map(Factory::build)
                                .collect(Collectors.toList())});
            } else {
                Object[] args = Arrays.stream(constructor.getParameterTypes())
                        .map(Factory::build)
                        .collect(Collectors.toList()).toArray(new Object[]{});
                return (T) newInstance(args);
            }
        } else {
            throw new IllegalStateException("Template and constructor cannot both be empty");
        }
    }

    /**
     * Build method that takes an adapter lambda for customizing the final object
     * Example:
     * Definition<Person> personDef = define(Person.class, () -> new Person("Sander", 48));
     * Person person = personDef.build(p -> {
     *             p.setName("Harry");
     *             return p;
     *         });
     * @param adapter
     * @return
     */
    public T build(Adapter<T> adapter) {
        T instance = template.get();
        adapter.adapt(instance);
        return instance;
    }

    private Object newInstance(Object[] args) {
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Example:
     * Definition<Employee> employeeDef = Factory.define(Employee.class).withConstructorArgs(Person.class);
     * Can be used given that Employee has a constructor with one parameterof type Person
     * This will use this constructor to create instances.
     *
     * @param parameterTypes varargs argument for the parameters of the constructor to look up.
     *
     * @return a Definition with the correct constructor
     */
    @SuppressWarnings("rawtypes")
    public Definition<T> withConstructorArgs(Class... parameterTypes) {
        try {
            this.constructor = type.getConstructor(parameterTypes);
            return this;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Definition<T> withConstructorArgs(List<Class<?>> typesToConstruct) {
        try {
            this.constructor = type.getConstructor(List.class);
            this.typesInList.addAll(typesToConstruct);
            return this;
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }
}
