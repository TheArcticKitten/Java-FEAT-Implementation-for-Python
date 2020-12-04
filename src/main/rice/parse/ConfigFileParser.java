package main.rice.parse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import main.rice.node.*;
import org.json.*;

/**
 * This class represents the config file parser which parses config files of the
 * format specified in the assignment.
 */
public class ConfigFileParser {

    /**
     * Reads and returns the contents of the file located at the input filepath
     * @param filepath String that holds the path to the config file
     * @return a string containing the contents of the file
     * @throws IOException if the file does not exist or cannot be read.
     */
    public String readFile(String filepath) throws IOException {

        File oFile = new File(filepath);
        String fileCont = Files.readString(Paths.get(filepath));

        return fileCont;
    }

    /**
     * Parses the input string which should be the contents of a JSON file formatted
     * according to the config file specifications. Builds a tree of Python nodes
     * for each parameter where each node's type, exhaustive domain, and random domain
     * are set up to reflect the contents of the config file. The nodes are then stored
     * in a list in the order that they were specified in the config file.
     *
     * @param contents String holding the contents of the config file
     * @return the list of nodes along with the parsed function name and number of random
     * tests to generate inside a config file object.
     * @throws InvalidConfigException In the case any part of the config file is missing
     * or malformed
     */
    public ConfigFile parse(String contents) throws InvalidConfigException {

        List<APyNode<?>> retNodes = new ArrayList<>();
        int numRan = -1;
        String funcName = "";
        boolean fileIsValid = true;
        JSONArray typeArr = new JSONArray();
        JSONArray exArr = new JSONArray();
        JSONArray randArr = new JSONArray();

        JSONObject jObj;
        try {
            jObj = new JSONObject(contents);
        }
        catch(Exception e) {
            throw new InvalidConfigException("Not valid JSON ");
        }
        Iterator keys = jObj.keys();

        ArrayList<String> keyList = new ArrayList<>();
        while(keys.hasNext()) {
             String curKey = (String)keys.next();
             keyList.add(curKey);
        }

        if(keyList.size() != 5) {
            fileIsValid = false;
            throw new InvalidConfigException(keyList.size() + " keys given. 5 Required");
        }

        for(String curKey: keyList) {
            switch(curKey) {
                case "fname":
                    if(jObj.get(curKey) instanceof String) {
                        funcName = (String)jObj.get(curKey);
                    }
                    else {
                        throw new InvalidConfigException("fName not a string");
                    }
                    /*if(funcName.equals("broken")) {
                        throw new InvalidConfigException("");
                    }*/
                    break;

                case "types":
                    String typeStr = jObj.get(curKey).toString();
                    try {
                        typeArr = jObj.getJSONArray(curKey);
                    }
                    catch(Exception e) {
                        throw new InvalidConfigException("Not valid JSON array");
                    }

                    break;

                case "exhaustive domain":
                    String exStr = jObj.get(curKey).toString();
                    if(isJsonArray(exStr)) {
                        exArr = jObj.getJSONArray(curKey);
                    }
                    else {
                        throw new InvalidConfigException("Not valid JSON array");
                    }

                    break;

                case "random domain":
                    String ranStr = jObj.get(curKey).toString();
                    if(isJsonArray(ranStr)) {
                        randArr = jObj.getJSONArray(curKey);
                    }
                    else {
                        throw new InvalidConfigException("Not valid JSON array");
                    }

                    break;

                case "num random":
                    if(jObj.get(curKey) instanceof Integer) {
                            String numStr = jObj.get(curKey).toString();
                            numRan = (int)jObj.get(curKey);
                            if(numRan < 0) {
                                fileIsValid = false;
                                throw new InvalidConfigException("numRan is negative");
                            }
                        }
                        else {
                            throw new InvalidConfigException("numRan not a valid integer");
                        }

                    break;

                default:
                    fileIsValid = false;
                    throw new InvalidConfigException("Invalid key");
            }
        }

        if(typeArr.length() != exArr.length() || typeArr.length() != randArr.length()
                    || randArr.length() != typeArr.length()) {
            throw new InvalidConfigException("Mismatched JSONArray lengths");
        }



        for(int i = 0; i < typeArr.length();i++) {
            String typeStr = ((String)typeArr.get(i)).replaceAll(" ", "");
            String exStr = (String)exArr.get(i);
            String randStr = (String)randArr.get(i);

            List<String> typeList = new ArrayList<>();
            List<String> exList = new ArrayList<>();
            List<String> randList = new ArrayList<>();

            int parenCounter = 0;
            for(char c : typeStr.toCharArray()) {
                if(c == '(') {
                    parenCounter += 1;
                }
            }


            String[] typeTokens = typeStr.split("\\(");
            String[] exTokens = exStr.split("\\(");
            String[] randTokens = randStr.split("\\(");

            int strTypeCounter = 0;

            for(String s : typeTokens) {
                if(s.equals("str")) {
                    strTypeCounter += 1;
                }

                int colonCounter = 0;
                for(char c : s.toCharArray()) {
                    if(c == ':') {
                        colonCounter += 1;
                    }
                }
                if(colonCounter > 1) {
                    throw new InvalidConfigException("too many colons");
                }
            }

            if(parenCounter > typeTokens.length - 1) {
                throw new InvalidConfigException("Too many parens");
            }

            for(String s : exTokens) {
                int colonCounter = 0;
                for(char c : s.toCharArray()) {
                    if(c == ':') {
                        colonCounter += 1;
                    }
                }
                if(colonCounter > 1) {
                    throw new InvalidConfigException("too many colons");
                }
            }


            for(String s : randTokens) {
                int colonCounter = 0;

                for(char c : s.toCharArray()) {
                    if(c == ':') {
                        colonCounter += 1;
                    }

                }
                if(colonCounter > 1) {
                    throw new InvalidConfigException("too many colons");
                }
            }


            int actTypes = typeTokens.length - strTypeCounter;
            if(actTypes != exTokens.length || actTypes != randTokens.length
                || randTokens.length != actTypes) {
                throw new InvalidConfigException("Mismatched JSONArray lengths");
            }

            for(String curToke : typeTokens) {
                curToke = curToke.replace(" ", "");
                typeList.add(curToke);
            }

            for(String curToke : exTokens) {
                curToke = curToke.replace(" ", "");
                exList.add(curToke);
            }

            for(String curToke : randTokens) {
                curToke = curToke.replace(" ", "");
                randList.add(curToke);
            }


            APyNode curNode = parseHelper(typeList, exList, randList);
            retNodes.add(curNode);
        }

        if(fileIsValid) {
            return new ConfigFile(funcName, retNodes, numRan);
        }
        else {
            throw new InvalidConfigException("File is invalid");
        }
    }

    private APyNode parseHelper(List<String> typeList, List<String> exList,
        List<String> randList) throws InvalidConfigException {
        String curType = typeList.get(0);
        curType = curType.replaceAll(" ", "");
        String curEx = exList.get(0);
        String curRand = randList.get(0);

        if( !curType.equals("bool") &&
            !curType.equals("str") &&
            !curType.equals("int") &&
            !curType.equals("float") &&
            !curType.equals("list") &&
            !curType.equals("dict") &&
            !curType.equals("set") &&
            !curType.equals("tuple")) {
            throw new InvalidConfigException("Invalid Type!");
        }


        if(curType.equals("bool")) {
            APyNode boolNode = new PyBoolNode();
            boolNode.setExDomain(parseDomain(curEx, curType));
            boolNode.setRanDomain(parseDomain(curRand, curType));
            return boolNode;
        }

        if(curType.equals("str")) {
            APyNode strNode = new PyStringNode(typeList.get(1));
            strNode.setExDomain(parseDomain(curEx, curType));
            strNode.setRanDomain(parseDomain(curRand, curType));
            return strNode;
        }

        if(curType.equals("int")) {
            APyNode intNode = new PyIntNode();
            intNode.setExDomain(parseDomain(curEx, curType));
            intNode.setRanDomain(parseDomain(curRand, curType));
            return intNode;
        }

        if(curType.equals("float")) {
            APyNode floatNode = new PyFloatNode();
            floatNode.setExDomain(parseDomain(curEx, curType));
            floatNode.setRanDomain(parseDomain(curRand, curType));
            return floatNode;
        }
        if(curType.equals("dict")) {
            boolean colonFlag = false;

            List<String> keys = new ArrayList<>();
            List<String> vals = new ArrayList<>();
            List<String> exKeys = new ArrayList<>();
            List<String> exVals = new ArrayList<>();
            List<String> randKeys = new ArrayList<>();
            List<String> randVals = new ArrayList<>();

            for(int i = 1; i < typeList.size(); i++) {
                String newCurType = typeList.get(i).replaceAll(" ", "");
                String newEx = exList.get(i);
                String newRan = randList.get(i);

                if (!colonFlag) {
                    if (newCurType.contains(":")) {
                        colonFlag = true;
                        String[] typeToke = newCurType.split(":");
                        String[] exToke = newEx.split(":");
                        String[] randToke = newRan.split(":");

                        keys.add(typeToke[0]);
                        vals.add(typeToke[1]);
                        exKeys.add(exToke[0]);
                        exVals.add(exToke[1]);
                        randKeys.add(randToke[0]);
                        randVals.add(randToke[1]);
                    } else {
                        keys.add(newCurType);
                        exKeys.add(newEx);
                        randKeys.add(newRan);
                    }
                } else {
                    vals.add(newCurType);
                    exVals.add(newEx);
                    randVals.add(newRan);
                }
            }
            APyNode newNode = new
                PyDictNode(parseHelper(keys, exKeys, randKeys),
                parseHelper(vals, exVals, randVals));
            newNode.setExDomain(parseDomain(curEx, curType));
            newNode.setRanDomain(parseDomain(curRand, curType));

            return newNode;
        }

        APyNode parameter = parseHelper(
            typeList.subList(1,typeList.size()),
            exList.subList(1, exList.size()),
            randList.subList(1, randList.size()));

        if(curType.equals("list")) {
            APyNode listNode = new PyListNode(parameter);
            listNode.setExDomain(parseDomain(curEx, curType));
            listNode.setRanDomain(parseDomain(curRand, curType));
            return listNode;
        }

        if(curType.equals("tuple")) {
            APyNode tupleNode = new PyTupleNode(parameter);
            tupleNode.setExDomain(parseDomain(curEx, curType));
            tupleNode.setRanDomain(parseDomain(curRand, curType));
            return tupleNode;
        }
        if(curType.equals("set")) {
            APyNode setNode = new PySetNode(parameter);
            setNode.setExDomain(parseDomain(curEx, curType));
            setNode.setRanDomain(parseDomain(curRand, curType));
            return setNode;
        }

        throw new InvalidConfigException("Invalid type/Structure mismatch");

    }

    private List<Number> parseDomain(String domStr, String type) throws
        InvalidConfigException {
        List<Number> numList = new ArrayList<>();
        type = type.replaceAll(" ", "");

        if(domStr.contains("~")) {
            String[] tokens = domStr.split("~");

            for(int i = 0; i < tokens.length;i++) {
                tokens[i] = tokens[i].replaceAll(" ", "");
            }

            if(tokens[0].contains(".") || tokens[1].contains(".")) {
                throw new InvalidConfigException("Non-integer domain value");
            }
            int tokeZero = Integer.parseInt(tokens[0]);
            int tokeOne = Integer.parseInt(tokens[1]);

            if(tokeZero > tokeOne) {
                throw new InvalidConfigException("First token larger than second");
            }

            if(type.equals("dict") || type.equals("bool") || type.equals("list")) {

                if(tokeZero < 0 || tokeOne < 0) {
                    throw new InvalidConfigException("Negative dict/bool domain");
                }

                if( type.equals("bool") && (tokeZero > 1 || tokeOne > 1) ) {
                    throw new InvalidConfigException("bool val greater than one");
                }

            }

            if(type.equals("float")) {
                long[] longArr = LongStream.rangeClosed(Long.parseLong(tokens[0]),
                    Long.parseLong(tokens[1])).toArray();
                for(long curLong : longArr) {
                    numList.add((float)curLong);
                }
            }
            else {

                if(tokens[0].contains(".") || tokens[1].contains(".")) {
                    throw new InvalidConfigException("Float given for integer domain");
                }
                int[] intArr = IntStream.rangeClosed(Integer.parseInt(tokens[0]),
                    Integer.parseInt(tokens[1])).toArray();

                for(int curInt : intArr) {
                    numList.add(curInt);
                }
            }
        }
        else {
            domStr = domStr.replaceAll("\\[", "");
            domStr = domStr.replaceAll(" ", "");
            domStr = domStr.replaceAll("]", "");
            String[] numArr = domStr.split(",");


            if(type.equals("bool") || type.equals("dict") || type.equals("list")) {
                for(String s : numArr) {
                    if(Integer.parseInt(s) < 0) {
                        throw new InvalidConfigException("Negative bool/dict/list domain");
                    }
                }
            }

            if(type.equals("float")) {
                for(String curNum : numArr) {
                    curNum = curNum.replaceAll(" ", "");
                    if(curNum.contains(".")) {
                        throw new InvalidConfigException("Float given for float domain");
                    }
                    long curLong = Long.parseLong(curNum);
                    numList.add(curLong);
                }
            }
            else {
                for(String curNum : numArr) {
                    curNum = curNum.replaceAll(" ", "");
                    if(curNum.contains(".")) {
                        throw new InvalidConfigException("Float given for int domain");
                    }
                    numList.add(Integer.parseInt(curNum));
                }
            }
        }

        HashSet<Number> noDuplicates = new HashSet<>(numList);

        return new ArrayList<>(noDuplicates);
    }

    /**
     * Helper method to check if a string is able to be parsed as an integer
     * @param intVal
     * @return True if the string can be parsed as an int, false otherwise.
     */
    protected boolean parseInt(String intVal) {
        try {
            Integer.parseInt(intVal.strip());
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    /**
     * Helper method to convert a JSONArray to an array of strings
     * @param arr A JSONArray
     * @return String[] version of the JSONArray
     */
    protected String[] toStringArray(JSONArray arr) {

        String[] retArr = new String[arr.length()];
        for(int i = 0; i < arr.length(); i++) {
            retArr[i] = (String)arr.get(i);
        }

        return retArr;
    }

    /**
     * Helper method to determine if a string is a valid JSONArray
     * @param arrVal
     * @return True if the input is a valid JSONArray string representation,
     * false otherwise
     */
    protected boolean isJsonArray(String arrVal) {
        if(!arrVal.startsWith("[") || !arrVal.endsWith("]")) {
            return false;
        }
        else {
            return true;
        }

    }
}
/*
 Checking that the file contains a valid JSON object
 Checking that each of the keys is an object of the correct type (e.g. "fname"'s value is a String, "types"'s value is a JSONArray of Strings, etc.)
 Checking that each type in "types" is valid
 Checking that the lower bound on a domain expressed as a range does not exceed the upper bound
 Checking that the bounds on a domain expressed as a range are integers
 Checking that the domain for an int is, in fact, comprised of integers
 Checking that the domain for a bool contains only values in {0, 1}
 Checking that the structures of both "exhaustive domain" and "random domain" parallel the structure "types"
 Checking for spurious or missing parentheses or colons
 Checking for and removing duplicate elements from a domain
 */