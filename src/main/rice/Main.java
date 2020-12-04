package main.rice;

import main.rice.basegen.BaseSetGenerator;
import main.rice.concisegen.ConciseSetGenerator;
import main.rice.node.APyNode;
import main.rice.parse.ConfigFile;
import main.rice.parse.ConfigFileParser;
import main.rice.parse.InvalidConfigException;
import main.rice.test.TestCase;
import main.rice.test.TestResults;
import main.rice.test.Tester;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The main class is the entry point to the test case generator which ties together all
 * the different components we've created in the previous homeworks and generates the
 * concise test set and prints the resultant set to the console, along with a message
 * which explains what is being printed.
 */
public class Main {

    /**
     * The entry point for our testing program. Should expect three arguments in the
     * string array: a path to the config file, a path to the directory containing the
     * buggy implementations, and a path to the reference solution.
     * @param args String array of input arguments
     * @throws IOException
     * @throws InvalidConfigException
     */
    public static void main(String[] args) throws IOException, InvalidConfigException {

        /**
         * The first argument given is the path to the config file
         */
        String configPath = args[0];
        /**
         * The second argument given is the path to the buggy implementation
         */
        String buggyPath = args[1];
        /**
         * The third argument given is the path to the reference implementation
         */
        String refPath = args[2];

        Set<TestCase> retSet = generateTests(args);

        /*
        //utilize the helper method to generate the concise test set
        Set<TestCase> getTests = generateTests(args);

        //create a config file parser to parse the config file into an object
        ConfigFileParser parser = new ConfigFileParser();
        String cFileContents = parser.readFile(configPath);
        ConfigFile cFile = parser.parse(cFileContents);

        //create a tester to get our results for the concise test set.
        Tester mainTest = new
            Tester(cFile.getFuncName(), refPath, buggyPath, new ArrayList<>(getTests));

        //compute the expected results
        List<String> expResults = mainTest.computeExpectedResults();

        //print the expected results
        System.out.println("Printing expected results for the concise test set:");
        for(String curResult: expResults) {
            System.out.println(curResult);
        }*/
    }

    /**
     * Utilize the components that you built in homeworks 1-7 in order to perform
     * end-to-end test case generation, returning the concise test set.
     * @param args
     * @return A set of test cases which represent the concise test set
     * @throws IOException
     * @throws InvalidConfigException
     */
    public static Set<TestCase> generateTests(String[] args) throws IOException,
        InvalidConfigException {


        /**
         * The first argument given is the path to the config file
         */
        String configPath = args[0];
        /**
         * The second argument given is the path to the buggy implementation
         */
        String buggyPath = args[1];
        /**
         * The third argument given is the path to the reference implementation
         */
        String refPath = args[2];

        //create a config file parser to parse the config file into an object
        ConfigFileParser parser = new ConfigFileParser();
        String cFileContents = parser.readFile(configPath);
        ConfigFile cFile = parser.parse(cFileContents);

        //grab our information for generation from the config file
        String funcName = cFile.getFuncName();
        List<APyNode<?>> nodes = cFile.getNodes();
        int numRand = cFile.getNumRand();

        //generate the base test set using the BaseSetGenerator
        BaseSetGenerator testGen = new BaseSetGenerator(nodes, numRand);
        List<TestCase> baseSet = testGen.genBaseSet();

        //Create a tester to run our tests and find the concise set
        Tester oTest = new Tester(funcName, refPath, buggyPath, baseSet);
        oTest.computeExpectedResults();
        TestResults tResults = oTest.runTests();

        //Generate and return the concise set using the results and concise set generator
        ConciseSetGenerator cGen = new ConciseSetGenerator();
        Set<TestCase> cSet = cGen.setCover(tResults);

        return cSet;


    }
}
