package tracer.util;

/**
 * The Copyable interface defines a method to get a copy of the object instance of any class that implements this
 * interface. The generic type should be the same type of the class. Preferably, the copy method should create a new
 * independent instance of the object that the method was called.
 *
 * @author Pedro Henrique
 */
public interface Copyable<T> {

    /**
     * Creates and returns a copy of this object.
     *
     * @return a copy of this object.
     */
    T copy();
}
