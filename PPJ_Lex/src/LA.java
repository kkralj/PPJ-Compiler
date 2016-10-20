import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LA {

    private List<LexerRule> lexerRules = new ArrayList<>();
    private List<Automat> automationList = new ArrayList<>();

    private String source;

    public LA(final String sourceCode) {
        this.source = sourceCode;
        generateLexerRules();

    }

    private void generateLexerRules() {
         /*
            aaaa
            aaa
            aaaaaaa
            aaaaaaaa
            aaaaaaaaaaa
         */
//        lexerRules.add(new LexerRule("aaaa", "initial", 1, "4a-rule"));
//        lexerRules.add(new LexerRule("aaa", "initial", 1, "3a-rule"));
//        lexerRules.add(new LexerRule("\\n", "initial", 1, "newline_rule"));

        /*
            3 -  - ---0x12
         */
//        lexerRules.add(new LexerRule("0|1|2|3", "initial", 1, "number"));
//        lexerRules.add(new LexerRule("0x(1|2)*", "initial", 1, "hex"));
//        lexerRules.add(new LexerRule("\\n", "initial", 1, "newline_rule"));
//        lexerRules.add(new LexerRule("\\s", "initial", 1, "ignore_space"));
//        lexerRules.add(new LexerRule("-", "initial", 1, "mali_minus"));
//        lexerRules.add(new LexerRule("-(\\s)*-", "initial", 1, "posebni_operator"));


        // greska_u_stanju
        // lexerRules.add(new LexerRule("xxx", "initial", 1, "3x-rule"));

        // vrati_se
//        lexerRules.add(new LexerRule("aa*", "initial", 1, "aa*"));
//        lexerRules.add(new LexerRule("bb(bb)*", "initial", 1, "bb(bb)*"));
//        lexerRules.add(new LexerRule("ccc(ccc)*", "initial", 1, "ccc(ccc)*"));
//        lexerRules.add(new LexerRule("\\n", "initial", 1, "newline_rule"));

        // vrati_se_prioritet
//        lexerRules.add(new LexerRule("\\n", "initial", 1, "newline_rule"));
//        lexerRules.add(new LexerRule("aaa", "initial", 1, "aaa"));
//        lexerRules.add(new LexerRule("aa*", "initial", 1, "aa*"));

    }

    private String lexerState;
    private int currentLine = 0;

    public void generateTokens() {
        lexerState = "S_jedan";

        String bufferedString = "";

        for (int i = 0; i < source.length(); i++) {
            char current = source.charAt(i);
            bufferedString += current;

            boolean atLeastOneStateValid = hasAtLeastOneState(bufferedString);

            if (atLeastOneStateValid) {
                continue; // hope to find even better solution so just continue

            } else if (bufferedString.length() <= 1 && !atLeastOneStateValid) {
                continue; // no valid states but too few characters to recover, read further

            } else { // no more matches exist, extract the best one and recover

                /* temporarily remove last char and return it later */
                bufferedString = bufferedString.substring(0, bufferedString.length() - 1);

                LexerRule rule = findBestRule(bufferedString);

                /* error recovery */
                if (rule == null) {
                    System.out.println("Error while processing ->" + bufferedString + "<-");

                    LexerRule prefixRule = bestPrefixRule(bufferedString);

                    if (prefixRule == null) {
                        System.err.println("Error char: ->" + bufferedString.charAt(0) + "<-");
                        bufferedString = bufferedString.substring(1);
                    } else {
                        LexerRule.MatchState prefixMatchState = prefixRule.getMatchState(bufferedString);

                        System.out.println("Match length: " + prefixMatchState.getMatchLength());
                        System.out.println("Match: ->" + bufferedString.substring(0, prefixMatchState.getMatchLength()) + "<-");
                        System.out.println("Rule name: " + prefixRule.getName());
                        System.out.println();

                        lexerState = prefixRule.getName();
                        bufferedString = bufferedString.substring(prefixMatchState.getMatchLength());
                    }

                    i--; // start again
                    continue;

                } else {
                    System.out.println("Considering string: ->" + bufferedString + "<-");

                    LexerRule.MatchState matchState = rule.getMatchState(bufferedString);
                    System.out.println("Match length: " + matchState.getMatchLength());

                    System.out.println("Match: ->" + bufferedString.substring(0, matchState.getMatchLength()) + "<-");
                    System.out.println("Rule name: " + rule.getName());
                    System.out.println();

                    lexerState = rule.getName();
                    bufferedString = bufferedString.substring(matchState.getMatchLength());
                }


                bufferedString += current; // removed character is added back
            }
        }
    }

    private LexerRule bestPrefixRule(String input) {
        int bestLen = 0;
        LexerRule bestRule = null;

        for (int i = 0; i < input.length(); i++) {
            for (LexerRule lexerRule : lexerRules) {
                int curLen = lexerRule.getMatchState(input.substring(0, i + 1)).getMatchLength();
                if (curLen > bestLen) {
                    bestLen = curLen;
                    bestRule = lexerRule;
                }
            }
        }
        return bestRule;
    }

    private LexerRule findBestRule(String bufferedString) {
        if (bufferedString.isEmpty()) {
            return null;
        }

        LexerRule bestRule = null;
        int bestLength = -1, bestPriority = -1;

        for (LexerRule lexerRule : lexerRules) {
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

        return bestRule;
    }

    private boolean hasAtLeastOneState(String bufferedString) {
        for (LexerRule lexerRule : lexerRules) {
            if (lexerRule.getMatchState(bufferedString).getMatchLength() > 0) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        StringBuilder sourceCode = new StringBuilder();
        BufferedReader bi = new BufferedReader(
                new InputStreamReader(args.length > 0 ? new FileInputStream(args[0]) : System.in)
        );

        String line;
        while ((line = bi.readLine()) != null) {
            sourceCode.append(line).append("\n");
        }

        LA lexicalAnalyzer = new LA(sourceCode.toString());

        lexicalAnalyzer.generateTokens();

        // TODO: read serialized Automat objects here and store it in the list automationList

    }
}
