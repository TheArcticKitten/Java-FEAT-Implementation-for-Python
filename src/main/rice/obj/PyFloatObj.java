package main.rice.obj;

/**
 * A representation of Python objects of type float.
 */
public class PyFloatObj extends APyObj {

    /**
     * The value of this PyFloatObj.
     */
    private final Float value;

    /**
     * Constructor for a PyFloatObj; initializes its value to the input.
     *
     * @param value the value of this PyFloatObj
     */
    public PyFloatObj(Float value) {
        this.value = value;
    }

    /**
     * @return the underlying (Java Float) representation of this PyFloatObj
     */
    @Override
    public Float getValue() {
        return this.value;
    }

    /**
     * Builds and returns a string representation of this object that mirrors the Python
     * string representation.
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        return this.value.toString();
    }
}