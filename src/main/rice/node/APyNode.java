package main.rice.node;

import main.rice.obj.APyObj;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * An abstract class whose instances represent templates for creating Python objects. Each
 * APyNode is a generator for a type of APyObj. For instance, a PyIntNode generates
 * PyIntObjs. If this node represents a compound type, e.g. a list, it will hold
 * references to its child node(s).
 *
 * @param <ObjType> the type of Python object generated by this tree
 */
public abstract class APyNode<ObjType extends APyObj> {

    /**
     * The domain for exhaustive generation.
     */
    protected List<Number> exDomain;

    /**
     * The domain for random generation.
     */
    protected List<Number> ranDomain;

    /**
     * The RNG used for random generation.
     */
    protected Random rand = new Random();

    /**
     * @return the left child node
     */
    public APyNode<?> getLeftChild() {
        return null;
    }

    /**
     * @return the right child node
     */
    public APyNode<?> getRightChild() {
        return null;
    }

    /**
     * Sets the exhaustive domain to the input list of numbers.
     *
     * @param domain the exhaustive domain
     */
    public void setExDomain(List<Number> domain) {
        this.exDomain = domain;
    }

    /**
     * @return the exhaustive domain
     */
    public List<Number> getExDomain() {
        return this.exDomain;
    }

    /**
     * Sets the random domain to the input list of numbers.
     *
     * @param domain the random domain
     */
    public void setRanDomain(List<Number> domain) {
        this.ranDomain = domain;
    }

    /**
     * @return the random domain
     */
    public List<Number> getRanDomain() {
        return this.ranDomain;
    }

    /**
     * Generates all valid PyObjs of type ObjType within the exhaustive domain.
     *
     * @return a set of PyObjs of type ObjType comprising the exhaustive domain
     */
    public abstract Set<ObjType> genExVals();

    /**
     * Generates a single valid PyObj of type ObjType within the random domain.
     *
     * @return a single PyObj of type ObjType selected from the random domain
     */
    public abstract ObjType genRandVal();

    /**
     * Finds and returns the maximum value in the exhaustive domain.
     *
     * @return the maximum value in the exhaustive domain
     */
    protected int exDomainMax() {
        var maxLength = 0;
        for (Number length : this.exDomain) {
            if (length.intValue() > maxLength) {
                // Found new max
                maxLength = length.intValue();
            }
        }
        return maxLength;
    }

    /**
     * Makes a random choice from the random domain.
     *
     * @return a random element from the random domain
     */
    protected Number ranDomainChoice() {
        assert (this.ranDomain.size() > 0);
        int choice = this.rand.nextInt(this.ranDomain.size());
        return this.ranDomain.get(choice);
    }
}