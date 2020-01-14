package shautvast.edie;

/**
 * An Adapter lambda is used to alter instances after creation according to the template.
 * @param <T>
 */
public interface Adapter<T> {
    public void adapt(T input);
}
