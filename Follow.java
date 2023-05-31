import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class Follow {
    private LinkedHashMap<Character, String> followSets = new LinkedHashMap<Character, String>();

    public LinkedHashMap<Character, String> followSets(LinkedHashMap<Character, String> rules, LinkedHashMap<Character, String> firstSets) {


        // Initialization of Follow Sets //
        for (char key : rules.keySet()) {
            if (followSets.isEmpty() && followSets.size() == 0)
                followSets.put(key, "$");
            else
                followSets.put(key, "");
        }

        for (char key : rules.keySet()) {
            for (char key2 : rules.keySet()) {
                String tempSets="";
                String temp = rules.get(key2);
                for (int i = 0; i < temp.length(); i++){
                    boolean found= false;
                    if (key == temp.charAt(i)) {
                        if( key == temp.charAt(temp.length() -1) ){
                            tempSets+=followSets.get(key2);
                        }
                        else if( temp.charAt(i+1)=='|'){
                            tempSets+=followSets.get(key2);
                            temp= temp.substring(i+2);
                            i=-1;
                        }
                        else {
                            boolean terminal=false;
                            for (char c1 : Compiler.grammarTerminal)
                                if (c1 == temp.charAt(i + 1) && temp.charAt(i + 1) != 'ε'){
                                    terminal=true;
                                    break;
                                }
                            if(terminal) {
                                tempSets += temp.charAt(i + 1);
                                break;
                            }
                            else {
                                String temp2 = firstSets.get(temp.charAt(i + 1));
                                for (int j2 = 0; j2 < temp2.length(); j2++) {
                                    if (temp2.charAt(j2) == 'ε'){
                                        found = true;
                                        if (temp.charAt(i+1)!=temp.charAt(temp.length()-1)) {
                                            temp=temp.substring(0,i+1)+temp.substring(i+2);
                                        }
                                        else {
                                            temp=temp.substring(0,i+1);
                                        }
                                        if('ε' == temp2.charAt(temp2.length() - 1)) {
                                            tempSets+=temp2.substring(0,j2-1);
                                        }
                                        else {
                                            tempSets+=temp2.substring(0,j2-1)+temp2.substring(j2+1);
                                        }
                                        i--;
                                        break;
                                    }
                                }
                                if (found == false){
                                    tempSets+=temp2;
                                    break;
                                }
                            }
                        }
                    }
                }
                if(tempSets!=""){
                    String followString = followSets.get(key)+tempSets;
                    char[] chars = followString.toCharArray();
                    LinkedHashSet<Character> charSet = new LinkedHashSet<Character>();
                    for (char c : chars) {
                        charSet.add(c);
                    }

                    StringBuilder sb = new StringBuilder();
                    for (Character character : charSet) {
                        sb.append(character);
                    }
                    followString=sb.toString();
                    StringBuilder result = new StringBuilder();

                    for(int i = 0 ; i < followString.length(); i++)
                    {
                        if(followString.charAt(i)!=','){
                            result = result.append(followString.charAt(i));
                            if(i == followString.length()-1)
                                break;
                            result = result.append(',');
                        }
                    }
                    followSets.put(key,result.toString());
                }

            }
        }
        return followSets;
    }
}

