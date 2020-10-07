package swati4star.createpdf.util.lambda;

/**
 * A custom Implementation of {@link java.util.function.Consumer}.
 * Used because API Level 24 is required to use the java variant.
 * @param <T> the type of the input to the operation
 */
public interface Consumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t);

}
