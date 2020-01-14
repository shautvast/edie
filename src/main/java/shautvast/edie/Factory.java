package shautvast.edie;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 *
 */

@SuppressWarnings({"rawtypes","unchecked"})
public class Factory {

    private static final ConcurrentMap<Class<?>, Definition> definitions = new ConcurrentHashMap<>();

    /**
     * Creates a definition for the given type, using a Supplier that will create that type every time build() is called.
     *
     * @param type     Class that specifies what type must be created
     * @param supplier Lambda that creates the given type
     * @param <T>      Generic type of the created Definition
     * @return A Definition that can be called directly to create Type instances
     */
    public static <T> Definition<T> define(Class<T> type, Supplier<T> supplier) {
        Definition<T> definition = new Definition<>(supplier);
        definitions.put(type, definition);
        return definition;
    }

    /**
     * Creates a definition for the given type.
     * when using this method, the resulting definition is not complete yet. See Definition.withConstructor.
     *
     * @param type Class that specifies what type must be created
     * @param <T>  Generic type of the created Definition
     * @return A Definition that can be called directly to create Type instances
     */
    public static <T> Definition<T> define(Class<T> type) {
        Definition<T> definition = new Definition<>(type);
        definitions.put(type, definition);
        return definition;
    }

    /**
     * Builds the instance for the specified type, using an Adapter lambda that can alter specific attributes if needed.
     * It's logic is delegated to the Definition for the type.
     * <p>
     * Example:
     * Definition<Person> personDef = define(Person.class, () -> new Person("Sander", 48));
     * <p>
     * Person person = personDef.build(p -> {
     * p.setName("Harry");
     * return p;
     * });
     * <p>
     * The template name is 'Sander' but the person instance has a different name.
     *
     * @param type    Class to denote the type to create.
     * @param adapter Can be used to change the type after creation
     * @return an instance of the given type using template and adapter
     */
    public static <T> T build(Class<T> type, Adapter<T> adapter) {
        return getDefinition(type).build(adapter);
    }


    /**
     * Builds the instance for the specified type.
     * It's logic is delegated to the Definition for the type.
     * <p>
     * Example:
     * Definition<Person> personDef = define(Person.class, () -> new Person("Sander", 48));
     * <p>
     * Person person = personDef.build()
     * will always yields a new instance
     *
     * @param type Class to denote the type to create.
     * @return an instance of the given type using template and adapter
     */
    public static <T> T build(Class<T> type) {
        return getDefinition(type).build();
    }

    private static <T> Definition<T> getDefinition(Class<T> type) {
        Definition<T> definition = definitions.get(type);
        if (definition == null) {
            throw new IllegalStateException("definition not found for " + type);
        } else {
            return definition;
        }
    }

    static void checkTypes(Class<?>[] typesToConstruct) {
        if (Arrays.stream(typesToConstruct)
                .anyMatch(type -> !definitions.containsKey(type))) {
            throw new IllegalStateException("not all parameters bound to definition");
        }
    }
}