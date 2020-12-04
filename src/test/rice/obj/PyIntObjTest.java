package test.rice.obj;

import main.rice.obj.PyIntObj;
import org.junit.jupiter.api.*;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the PyIntObj class.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PyIntObjTest {

    /**
     * A PyIntObj representing the Python integer value 1.
     */
    private static PyIntObj posInt = new PyIntObj(1);

    /**
     * A PyIntObjs representing the Python integer value -1.
     */
    private static PyIntObj negInt = new PyIntObj(-1);

    /**
     * Two distinct PyIntObjs representing the Python integer value 123456789.
     */
    private static PyIntObj largeInt = new PyIntObj(123456789);
    private static PyIntObj largeInt2 = new PyIntObj(123456789);

    /**
     * A random integer (between 0 and Integer.MAX_VALUE), and a PyIntObj representing
     * that value.
     */
    private static int randVal = new Random().nextInt(Integer.MAX_VALUE);
    private static PyIntObj randInt = new PyIntObj(randVal);

    /**
     * Tests that getValue() returns the expected result for a PyIntObj constructed with a
     * positive integer value.
     */
    @Test
    @Tag("0.2")
    @Order(0)
    void testGetValuePositive() {
        assertEquals(1, posInt.getValue());
    }

    /**
     * Tests that getValue() returns the expected result for a PyIntObj constructed with a
     * negative integer value.
     */
    @Test
    @Tag("0.2")
    @Order(1)
    void testGetValueNegative() {
        assertEquals(-1, negInt.getValue());
    }

    /**
     * Tests that getValue() returns the expected result for a PyIntObj constructed with a
     * large positive integer value.
     */
    @Test
    @Tag("0.3")
    @Order(2)
    void testGetValueLarge() {
        assertEquals(123456789, largeInt.getValue());
    }

    /**
     * Tests that getValue() returns the expected result for a PyIntObj constructed with a
     * random integer value.
     */
    @Test
    @Tag("0.3")
    @Order(3)
    void testGetValueRandom() {
        assertEquals(randVal, randInt.getValue());
    }

    /**
     * Tests that toString() returns the expected result for a PyIntObj constructed with a
     * positive integer value.
     */
    @Test
    @Tag("0.2")
    @Order(4)
    void testToStringPositive() {
        assertEquals("1", posInt.toString());
    }

    /**
     * Tests that toString() returns the expected result for a PyIntObj constructed with a
     * negative integer value.
     */
    @Test
    @Tag("0.3")
    @Order(5)
    void testToStringNegative() {
        assertEquals("-1", negInt.toString());
    }

    /**
     * Tests that toString() returns the expected result for a PyIntObj constructed with a
     * large positive integer value.
     */
    @Test
    @Tag("0.5")
    @Order(6)
    void testToStringLarge() {
        assertEquals("123456789", largeInt.toString());
    }

    /**
     * Tests that two distinct PyIntObjs constructed with the same integer value are
     * considered equivalent by equals().
     */
    @Test
    @Tag("1.0")
    @Order(7)
    void testEqualsSimple() {
        assertEquals(largeInt, largeInt2);
    }

    /**
     * Tests that two distinct PyIntObjs constructed with different values are not
     * considered equivalent by equals().
     */
    @Test
    @Tag("2.0")
    @Order(8)
    void testNotEqual() {
        assertNotEquals(posInt, largeInt);
    }

    /**
     * Tests that two distinct PyIntObjs constructed with the same integer value return
     * the same value for hashCode().
     */
    @Test
    @Tag("1.0")
    @Order(9)
    void testHashCodeSimple() {
        assertEquals(largeInt.hashCode(), largeInt2.hashCode());
    }

    /**
     * Tests that two distinct PyIntObjs constructed with the different values return
     * different values for hashCode().
     */
    @Test
    @Tag("2.0")
    @Order(10)
    void testHashCodeNotEqual() {
        assertNotEquals(posInt.hashCode(), largeInt.hashCode());
    }
}