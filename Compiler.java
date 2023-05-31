import java.util.LinkedHashMap;

public class Compiler {
    static LinkedHashMap<Character, String> rules = new LinkedHashMap<Character, String>();
    static final char[] grammarNonTerminal = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    static final char[] grammarTerminal = "abcdefghijklmnopqrstuvwxyz->%:!|,#$()*+/ε.".toCharArray();

    static boolean deleteEpsilon = false;

    public static void main(String[] args) {
//        String fileName = "C:\\Users\\iRepair.al\\Desktop\\LL1Parser\\src\\grammar.txt"; // Replace with your file path

        LinkedHashMap<Character, String> inputGrammar = new LinkedHashMap<>();
        inputGrammar.put('S', "AB");
        inputGrammar.put('A', "aA|ε");
        inputGrammar.put('B', "bB|ε");
        String input = "aabb";

        First first = new First();
        Follow follow = new Follow();
        Parser parser = new Parser();

        // Check grammar rules
        boolean isValidGrammar = parser.checkRules(inputGrammar);
        if (!isValidGrammar) {
            Character unrecognizedChar = parser.getUnrecognizedChar();
            System.out.println("Invalid grammar rule. Unrecognized character: " + unrecognizedChar);
            return;
        }

        LinkedHashMap<Character, String> result = first.firstSets(inputGrammar);
        LinkedHashMap<Character, String> result2 = follow.followSets(inputGrammar,result);
        int result3 = parser.constructTable(inputGrammar,result,result2);
//        int result3 = parser.analyzeString();
        // Print the result
        for (char key : result.keySet()) {
            System.out.println("Key: " + key);
            System.out.println("First Set: " + result.get(key));
            System.out.println("------");
        }
        System.out.println("--------------------FOLLOW-----------------------------");
        for (char key : result2.keySet()) {
            System.out.println("Key: " + key);
            System.out.println("Follow Set: " + result2.get(key));
            System.out.println("------");
        }
        System.out.println("--------------------PARSER-----------------------------");
        int tableResult = parser.constructTable(rules, result, result2);
        if (tableResult != 0) {
            System.out.println("This Grammar is not of type LL(1)");
            return;
        }



        String[][] parsingTable = parser.getParsingTable();

// Iterate over rows and columns
        for (int i = 0; i < parsingTable.length; i++) {
            for (int j = 0; j < parsingTable[i].length; j++) {
                String value = parsingTable[i][j];
                System.out.print(value + "\t");
            }
            System.out.println();
        }

        parser.initializeString(rules, input, parsingTable);
        parser.autoAnalyzeString("OneTime",1.0);
//        first.findFirst(fileName);
    }
}


