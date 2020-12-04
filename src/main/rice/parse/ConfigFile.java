package main.rice.parse;

import main.rice.node.APyNode;

import java.util.List;

/**
 * This class represents the contents of a single config file
 */
public class ConfigFile {

    /**
     * String to hold the name of the function under test
     */
    String funcName;

    /**
     * List of APyNodes which we use for generations of test cases for the function
     */
    List<APyNode<?>> nodes;

    /**
     * Number of random test cases to generate
     */
    int numRand;

    /**
     * Constructor for a ConfigFile object, which takes in three pieces of data.
     * @param funcName the name of the function under test
     * @param nodes a list of Python nodes to serve as generators for test cases
     *              for the function under test
     * @param numRand number of random test cases to be generated
     */
    public ConfigFile(String funcName, List<APyNode<?>> nodes, int numRand) {

        this.funcName = funcName;
        this.nodes = nodes;
        this.numRand = numRand;

    }

    /**
     * Getter method for the function name string
     * @return String holding the name of the function under test
     */
    public String getFuncName() {
        return this.funcName;
    }

    /**
     * Getter method to grab the list of nodes we use for test case generation
     * @return
     */
    public List<APyNode<?>> getNodes() {
        return this.nodes;
    }

    /**
     * Getter method for the number of random test cases to generate
     * @return
     */
    public int getNumRand() {
        return this.numRand;
    }
}
