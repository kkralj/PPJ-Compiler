package analizator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.List;

public class LA {

    private List<String> lexerStates, lexerVariables;
    private List<LexerRule> lexerRules;

    private String source;

    public LA(final String sourceCode) {
        this.source = sourceCode;
        generateLexerRules();
        loadDefinitions();
    }

    @SuppressWarnings("unchecked")
    private void loadDefinitions() {

        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream("statesLA.obj"))) {
            this.lexerStates = (List<String>) reader.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream("lexicalVariables.obj"))) {
            this.lexerVariables = (List<String>) reader.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream("lexerRules.obj"))) {
            this.lexerRules = (List<LexerRule>) reader.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void generateLexerRules() {
        /*
         * aaaa aaa aaaaaaa aaaaaaaa aaaaaaaaaaa
         */
        // lexerRules.add(new LexerRule("aaaa", "initial", 1, "4a-rule"));
        // lexerRules.add(new LexerRule("aaa", "initial", 1, "3a-rule"));
        // lexerRules.add(new LexerRule("\\n", "initial", 1, "newline_rule"));

        /*
         * 3 - - ---0x12
         */
        // lexerRules.add(new LexerRule("0|1|2|3", "initial", 1, "number"));
        // lexerRules.add(new LexerRule("0x(1|2)*", "initial", 1, "hex"));
        // lexerRules.add(new LexerRule("\\n", "initial", 1, "newline_rule"));
        // lexerRules.add(new LexerRule("\\s", "initial", 1, "ignore_space"));
        // lexerRules.add(new LexerRule("-", "initial", 1, "mali_minus"));
        // lexerRules.add(new LexerRule("-(\\s)*-", "initial", 1,
        // "posebni_operator"));

        // greska_u_stanju
        // lexerRules.add(new LexerRule("xxx", "initial", 1, "3x-rule"));

        // vrati_se
        // lexerRules.add(new LexerRule("aa*", "initial", 1, "aa*"));
        // lexerRules.add(new LexerRule("bb(bb)*", "initial", 1, "bb(bb)*"));
        // lexerRules.add(new LexerRule("ccc(ccc)*", "initial", 1,
        // "ccc(ccc)*"));
        // lexerRules.add(new LexerRule("\\n", "initial", 1, "newline_rule"));

        // vrati_se_prioritet
        // lexerRules.add(new LexerRule("\\n", "initial", 1, "newline_rule"));
        // lexerRules.add(new LexerRule("aaa", "initial", 1, "aaa"));
        // lexerRules.add(new LexerRule("aa*", "initial", 1, "aa*"));

    }

    private String lexerState;
    private Integer currentLine = 1;
    private String bufferedString;

    public void generateTokens() {
        lexerState = lexerStates.get(0);

        bufferedString = "";

        for (int i = 0; i < source.length(); i++) {
            char current = source.charAt(i);
            bufferedString += current;

            boolean atLeastOneStateValid = hasAtLeastOneState(lexerState, bufferedString);

            if (atLeastOneStateValid) {
                continue; // hope to find even better solution so just continue

            } else if (bufferedString.length() <= 1 && !atLeastOneStateValid) {
                continue; // no valid states but too few characters to recover,
                          // read further

            } else { // no more matches exist, extract the best one and recover

                /* temporarily remove last char and return it later */
                bufferedString = bufferedString.substring(0, bufferedString.length() - 1);

                LexerRule rule = findBestRule(lexerState, bufferedString);

                /* error recovery */
                if (rule == null) {
                    // System.out.println("Error while processing ->" +
                    // bufferedString + "<-");

                    LexerRule prefixRule = bestPrefixRule(lexerState, bufferedString);

                    if (prefixRule == null) {
                        // System.err.println("Error char: ->" +
                        // bufferedString.charAt(0) + "<-");
                        bufferedString = bufferedString.substring(1);
                    } else {
                        completeActions(prefixRule);
                    }

                    i--; // start again
                    continue;

                } else {
                    completeActions(rule);
                }

                bufferedString += current; // removed character is added back
            }
        }
    }

    private void completeActions(LexerRule rule) {
        // lexerState, bufferedString, currentLine

        // System.out.println("Considering string: ->" + bufferedString + "<-");

        LexerRule.MatchState matchState = rule.getMatchState(bufferedString);

        // System.out.println("Match length: " + matchState.getMatchLength());
        // System.out.println("Match: ->" + bufferedString.substring(0,
        // matchState.getMatchLength()) + "<-");
        // System.out.println("Rule name: " + rule.getName());
        // System.out.println();

        boolean vratiSeActionUsed = false;
        String match = bufferedString.substring(0, matchState.getMatchLength());

        List<String> actions = rule.getActions();

        if (actions.contains("NOVI_REDAK")) {
            currentLine++;
        }

        for (String action : actions) {
            if (action.startsWith("VRATI_SE ")) {
                action = action.substring("VRATI_SE ".length());
                vratiSeActionUsed = true;
                match = bufferedString.substring(0, Integer.parseInt(action));
                bufferedString = bufferedString.substring(Integer.parseInt(action));

            }
        }

        for (String action : actions) {
            if (action.startsWith("UDJI_U_STANJE ")) {
                action = action.substring("UDJI_U_STANJE ".length());
                lexerState = action;
            }
        }

        for (String action : actions) {
            if (lexerVariables.contains(action)) {
                String output = action + " " + currentLine + " " + match;
                // if (output.equals("OP_PRIDRUZI 27 =")) {
                // System.out.print("");
                // }
                System.out.println(output);
            }
        }

        // for (String action : rule.getActions()) {
        // action = action.trim();
        // if (action.equals("NOVI_REDAK")) {
        // currentLine++;
        //
        // } else if (action.startsWith("UDJI_U_STANJE ")) {
        // action = action.substring("UDJI_U_STANJE ".length());
        // lexerState = action;
        //
        // } else if (action.startsWith("VRATI_SE ")) {
        // action = action.substring("VRATI_SE ".length());
        // vratiSeActionUsed = true;
        // match = bufferedString.substring(0, Integer.parseInt(action));
        // bufferedString = bufferedString.substring(Integer.parseInt(action));
        //
        // } else if (lexerVariables.contains(action)) {
        // String output = action + " " + currentLine + " " + match;
        // if (output.equals("OPERAND 2 3")) {
        // System.out.println("iduci");
        // }
        // System.out.println(output);
        //
        // }
        // }

        if (!vratiSeActionUsed) {
            bufferedString = bufferedString.substring(matchState.getMatchLength());
        }

    }

    private LexerRule bestPrefixRule(String currentState, String input) {
        int bestLen = 0;
        LexerRule bestRule = null;

        for (int i = 0; i < input.length(); i++) {
            for (LexerRule lexerRule : lexerRules) {
                if (lexerRule.getState().equals(currentState)) {
                    LexerRule.MatchState matchState = lexerRule.getMatchState(input.substring(0, i + 1));
                    int curLen = matchState.getMatchLength();
                    if (curLen > bestLen && matchState.isFullyMatched()) {
                        bestLen = curLen;
                        bestRule = lexerRule;
                    }
                }
            }
        }
        return bestRule;
    }

    private LexerRule findBestRule(String currentState, String bufferedString) {
        if (bufferedString.isEmpty()) {
            return null;
        }

        LexerRule bestRule = null;
        int bestLength = -1, bestPriority = -1;

        for (LexerRule lexerRule : lexerRules) {
            if (lexerRule.getState().equals(currentState)) {
                LexerRule.MatchState matchState = lexerRule.getMatchState(bufferedString);
                if (matchState.isFullyMatched()) {
                    int length = matchState.getMatchLength();
                    int priority = lexerRule.getPriority();

                    if (length > bestLength || (length == bestLength && priority > bestPriority)) {
                        bestLength = length;
                        bestPriority = priority;
                        bestRule = lexerRule;
                    }
                }
            }
        }

        return bestRule;
    }

    private boolean hasAtLeastOneState(String currentState, String bufferedString) {
        for (LexerRule lexerRule : lexerRules) {
            if (lexerRule.getRegexDefinition().startsWith("((0|1|2|3|4|5|6|")) {
                // System.out.println("tu");
            }
            if (lexerRule.getState().equals(currentState)) {
                LexerRule.MatchState matchState = lexerRule.getMatchState(bufferedString);
                if (matchState.getMatchLength() == bufferedString.length()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {

        StringBuilder sourceCode = new StringBuilder();
        BufferedReader bi;
        try {
            bi = new BufferedReader(new InputStreamReader(args.length > 0 ? new FileInputStream(args[0]) : System.in));

            String line;
            while ((line = bi.readLine()) != null) {
                sourceCode.append(line).append("\n");
            }

            LA lexicalAnalyzer = new LA(sourceCode.toString());

            lexicalAnalyzer.generateTokens();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
