import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Stack;

public class Parser {

    private String[][] parsingTable;
    private Stack<Character> stack = new Stack<>();
    private String input = "";
    private Character notRecognized;

    public boolean checkRules(LinkedHashMap<Character, String> inputGrammar) {
        boolean check = false;

        if (Compiler.rules != null)
            Compiler.rules.clear();

        for (char nonTerminal : inputGrammar.keySet()) {
            String productions = inputGrammar.get(nonTerminal);

            // Check Grammar Productions //
            for (int i = 0; i < productions.length(); i++) {
                char s = productions.charAt(i);

                boolean isNonTerminal = false;
                for (char nt : Compiler.grammarNonTerminal) {
                    if (s == nt) {
                        isNonTerminal = true;
                        break;
                    }
                }

                boolean isTerminal = false;
                for (char t : Compiler.grammarTerminal) {
                    if (s == t) {
                        isTerminal = true;
                        break;
                    }
                }

                if (isNonTerminal || isTerminal) {
                    check = true;
                } else {
                    check = false;
                    notRecognized = s;
                    break;
                }
            }

            if (!check) {
                notRecognized = nonTerminal;
                break;
            }

            Compiler.rules.put(nonTerminal, productions);
        }

        return check;
    }

    public Character getUnrecognizedChar() {
        return notRecognized;
    }

    public int constructTable(LinkedHashMap<Character, String> rules, LinkedHashMap<Character, String> firstSets, LinkedHashMap<Character, String> followSets) {

        // Initializing the parsing table
        ArrayList<Character> nonTerms = new ArrayList<>();
        ArrayList<Character> chars = new ArrayList<>();
        int countNonTerm = 0, countTerm = 0;
        for (char key : rules.keySet()) {
            nonTerms.add(key);
            countNonTerm++;
            for (int i = 0; i < rules.get(key).length(); i++) {
                for (int j = 0; j < Compiler.grammarTerminal.length; j++) {
                    if (rules.get(key).charAt(i) == Compiler.grammarTerminal[j] && rules.get(key).charAt(i) != 'ε' && rules.get(key).charAt(i) != '|') {
                        if (!(chars.indexOf(rules.get(key).charAt(i)) > 0)) {
                            chars.add(rules.get(key).charAt(i));
                            countTerm++;
                        }
                    }
                }
            }
        }
        chars.add('$');
        countTerm++;
        parsingTable = new String[countNonTerm + 1][countTerm + 1];
        parsingTable[0][0] = " ";
        for (int i = 1; i < parsingTable[0].length; i++) {
            parsingTable[0][i] = chars.get(i - 1).toString();
        }
        for (int i = 1; i < parsingTable.length; i++) {
            parsingTable[i][0] = nonTerms.get(i - 1).toString();
        }

        // Filling table
        for (char key : rules.keySet()) {
            String value = rules.get(key);
            String[] tempRules = value.split("\\|");
            int keepRow = 0;
            for (int i = 0; i < parsingTable.length; i++) {
                if (parsingTable[i][0].charAt(0) == key) {
                    keepRow = i;
                    break;
                }
            }
            for (int i = 0; i < tempRules.length; i++) {
                boolean isTerminal = false;
                for (int j = 0; j < Compiler.grammarTerminal.length; j++) {
                    if (tempRules[i].charAt(0) == Compiler.grammarTerminal[j]) {
                        isTerminal = true;
                        break;
                    }
                }
                if (isTerminal) {
                    if (tempRules[i].charAt(0) == 'ε') {
                        String getFollow = followSets.get(key);
                        String[] getFollowValues = getFollow.split("\\,");
                        for (int k = 0; k < getFollowValues.length; k++) {
                            for (int l = 1; l < parsingTable[0].length; l++) {
                                if (getFollowValues[k].charAt(0) == parsingTable[0][l].charAt(0)) {
                                    if (parsingTable[keepRow][l] == null) {
                                        parsingTable[keepRow][l] = key + "->" + tempRules[i];
                                        break;
                                    } else {
                                        System.out.println("This Grammar is not of type LL(1)");
                                        return 2;
                                    }
                                }
                            }
                        }
                        break;
                    } else {
                        for (int k = 1; k < parsingTable[0].length; k++) {
                            if (tempRules[i].charAt(0) == parsingTable[0][k].charAt(0)) {
                                if (parsingTable[keepRow][k] == null) {
                                    parsingTable[keepRow][k] = key + "->" + tempRules[i];
                                    break;
                                } else {
                                    System.out.println("This Grammar is not of type LL(1)");
                                    return 2;
                                }
                            }
                        }
                    }
                } else {
                    String getFirst = firstSets.get(tempRules[i].charAt(0));
                    String[] getFirstValues = getFirst.split("\\,");
                    for (int k = 0; k < getFirstValues.length; k++) {
                        for (int l = 1; l < parsingTable[0].length; l++) {
                            if (getFirstValues[k].charAt(0) == parsingTable[0][l].charAt(0)) {
                                if (parsingTable[keepRow][l] == null) {
                                    parsingTable[keepRow][l] = key + "->" + tempRules[i];
                                    break;
                                } else {
                                    System.out.println("This Grammar is not of type LL(1)");
                                    return 2;
                                }
                            }
                        }
                    }
                    if (i != tempRules.length) continue;
                    else break;
                }
            }
        }
        return 0;
    }

    public String[][] getParsingTable() {
        return parsingTable;
    }

    public void initializeString(LinkedHashMap<Character, String> rules, String inputString, String[][] parsingTable) {
        stack.push('$');
        System.out.println("Stack: " + stack);
        input = inputString + '$';
        System.out.println("Input: " + input);
        stack.push(Compiler.rules.keySet().toArray()[0].toString().charAt(0));
        System.out.println("Stack: " + stack);
    }

    public void autoAnalyzeString(String type, double timer) {
        switch (type) {
            case "Timer":
                while (true) {
                    analyzeString();
                    try {
                        Thread.sleep((long) (timer * 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            case "OneTime":
                while (analyzeString() == 0) {
                    continue;
                }
                break;
        }
    }

    public int analyzeString() {
        if (stack.empty()) {
            System.out.println("String not recognized");
            return 1;
        }
        char top = stack.peek();
        int keepRow = 0;
        String value = "";
        if (top == '$' && input.charAt(0) == '$') {
            System.out.println("The string is recognized");
            return 2;
        } else if (top == input.charAt(0)) {
            System.out.println("Symbol absorption: " + input.charAt(0));
            stack.pop();
            input = input.substring(1);
            System.out.println("Stack: " + stack);
            System.out.println("Input: " + input);
        } else {
            for (int j = 1; j < parsingTable.length; j++) {
                if (parsingTable[j][0].charAt(0) == top) {
                    keepRow = j;
                    break;
                }
            }
            for (int j = 1; j < parsingTable[0].length; j++) {
                if (parsingTable[0][j].charAt(0) == input.charAt(0)) {
                    value = parsingTable[keepRow][j];
                    if (value == null) {
                        System.out.println("String not recognized");
                        return 1;
                    }
                    value = value.substring(3);
                    System.out.println("Rule: " + parsingTable[keepRow][0] + "->" + value);
                    break;
                }
            }
            if (value != "") {
                if (value.charAt(0) != 'ε') {
                    stack.pop();
                    for (int j = value.length() - 1; j >= 0; j--) {
                        stack.push(value.charAt(j));
                    }
                } else {
                    System.out.println("Symbol removal: " + stack.peek());
                    stack.pop();
                }
            } else {
                System.out.println("String not recognized");
                return 1;
            }
        }
        return 0;
    }
}