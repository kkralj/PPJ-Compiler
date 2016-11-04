import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;

public class GSA {
    /**
     * types of characters
     */
    private static List<String> nonTerminal = new ArrayList<>();
    private static List<String> terminal = new ArrayList<>();
    private static List<String> synchronization = new ArrayList<>();
    /**
     * productions of grammar, for one nonTerminal character as key(left side of
     * production) is associated with several groups of characters(right side of
     * production) separated by character '|'.
     */
    private static Map<String, String> productions = new HashMap<>();

    private static List<String> emptyNonTerminal = new ArrayList<>();
    /**
     * Za ključ je ovdje stavljeno LR stavka dok je vrijednost pridružen nekakav
     * skup znakova za koje ne razumijem cemu sluze to ima veze s nekim živim
     * prefiksima i cim sve ne.
     */
    private static List<String> LRItems = new ArrayList<>();

    private static List<LRItem> items = new ArrayList<>();

    private static Map<String, String> transitions = new HashMap<>();

    public static void main(String[] args) {
        input(args);
        for (Entry<String, String> production : productions.entrySet()) {
            System.out.println(production);
        }

        computeEmptyNonTerminal();
        System.out.println("prazni nezavrsni znakovi");
        for (String entry : emptyNonTerminal) {
            System.out.println(entry);
        }
        computeStart();
        generateLRitems();
        generateENKA();

    }

    private static void generateENKA() {
        String firstLRItem = LRItems.get(0);
        LRItem item = new LRItem(firstLRItem);
        item.start.add("#");
        items.add(item);

    }

    private static void generateLRitems() {
        for (String nonTerminal : productions.keySet()) {
            String tmp[] = productions.get(nonTerminal).split("\\|");
            for (String entry : tmp) {
                if (entry.equals("$")) {
                    LRItems.add(nonTerminal + " -> *");
                } else {
                    String[] tmp1 = entry.split(" ");
                    List<String> list = new LinkedList<>(Arrays.asList(tmp1));
                    int n = list.size();
                    for (int i = 0; i <= n; i++) {
                        list.add(i, "*");
                        StringBuilder str = new StringBuilder();
                        for (String entry2 : list) {
                            str.append(" " + entry2);
                        }
                        LRItems.add(nonTerminal + " ->" + str);
                        list.remove(i);
                    }
                }
            }
        }
        for (String entry : LRItems) {
            System.out.println(entry);
        }
    }

    /**
     * Računa skupove ZAPOCINJE.
     */
    private static void computeStart() {
        List<String> allChars = new ArrayList<>();
        allChars.addAll(nonTerminal);
        allChars.addAll(terminal);
        int n = allChars.size();
        boolean[][] matrix = new boolean[n][n];
        // reflexivity
        for (int i = 0; i < n; i++) {
            matrix[i][i] = true;
        }
        // start directly with character
        for (String nonTerminal : productions.keySet()) {
            String tmp[] = productions.get(nonTerminal).split("\\|");
            for (String entry : tmp) {
                if (entry.equals("$")) {
                    continue;
                } else {
                    matrix[allChars.indexOf(nonTerminal)][allChars.indexOf(entry.split(" ")[0])] = true;
                }
            }
        }
        int x = nonTerminal.size();
        while (true) {
            int numberOfChanges = 0;
            for (int i = 0; i < x; i++) {
                for (int j = 0; j < x; j++) {
                    if (matrix[i][j]) {
                        for (int k = 0; k < n; k++) {
                            if (matrix[j][k]) {
                                if (matrix[i][k] == false) {
                                    matrix[i][k] = true;
                                    numberOfChanges++;
                                }
                            }
                        }
                    }
                }
            }
            if (numberOfChanges == 0) {
                break;
            }
        }

        System.out.println("\nZAPOCINJE za znakove");
        for (int i = 0; i < n; i++) {
            System.out.print(allChars.get(i) + ":");
            for (int j = x; j < n; j++) {
                if (matrix[i][j]) {
                    System.out.print(" " + allChars.get(j));
                }
            }
            System.out.println();
        }

    }

    /**
     * Method which compute empty non terminal characters.
     */
    private static void computeEmptyNonTerminal() {
        // as first we add left sides of epsilon productions
        for (String nonTerminal : productions.keySet()) {
            // Attention! .split("|") does not work.
            String tmp[] = productions.get(nonTerminal).split("\\|");
            for (String entry : tmp) {
                if (entry.equals("$")) {
                    emptyNonTerminal.add(nonTerminal);
                }
            }
        }
        /*
         * then we extend list of empty non terminal characters with non
		 * terminal characters which right side of productions already contains
		 * all empty non terminal characters
		 */

        while (true) {
            int size = emptyNonTerminal.size();
            for (String nonTerminal : productions.keySet()) {
                if (emptyNonTerminal.contains(nonTerminal)) {
                    continue;
                }
                boolean shouldBeAdded = false;
                // right sides
                // Attention! .split("|") does not work.
                String tmp[] = productions.get(nonTerminal).split("\\|");
                for (String entry : tmp) {
                    String[] tmp1 = entry.split(" ");
                    for (String entry1 : tmp1) {
                        boolean add = true;
                        if (!emptyNonTerminal.contains(entry1)) {
                            add = false;
                        }
                        if (add == true) {
                            shouldBeAdded = true;
                        }
                    }

                }
                if (shouldBeAdded) {
                    emptyNonTerminal.add(nonTerminal);
                }
            }
            if (size == emptyNonTerminal.size()) {
                break;
            }
        }

    }

    /**
     * Reads Syntax Analyzer definition.
     */
    private static void input(String[] args) {
        try (BufferedReader scanner = new BufferedReader(
                new InputStreamReader(args.length > 0 ? new FileInputStream(args[0]) : System.in))) {
            String line;

            // nonTerminal characters
            line = scanner.readLine().trim();
            line = line.substring(3);
            skipSplitAdd(line, nonTerminal);
            // it says in the instructions that should be added new start
            // nonTerminal
            // character, in this case it will be <%>.
            nonTerminal.add("<%>");

            // terminal characters
            line = scanner.readLine().trim();
            line = line.substring(3);
            skipSplitAdd(line, terminal);

            // synchronization characters
            line = scanner.readLine().trim();
            line = line.substring(5);
            skipSplitAdd(line, synchronization);

            line = scanner.readLine();
            // add start production as stated in the instruction
            productions.put("<%>", nonTerminal.get(0));
            while (line != null) {
                if (!line.startsWith(" ")) {
                    String key = line;
                    String value = "";
                    while (true) {
                        line = scanner.readLine();
                        if (line != null && line.startsWith(" ")) {
                            line = line.substring(1);
                            value += line + "|";
                        } else {
                            if (productions.containsKey(key)) {
                                productions.put(key, productions.get(key) + "|" + value);
                            } else {
                                productions.put(key, value.substring(0, value.length() - 1));
                            }
                            break;
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Splits given string by empty spaces and adds all to given list.
     *
     * @param s    String to split.
     * @param list List to add split strings to.
     */
    private static void skipSplitAdd(String s, List<String> list) {
        String tmp[] = s.split(" ");

        for (int i = 0; i < tmp.length; i++) {
            list.add(tmp[i]);

            // System.out.println(tmp[i]);
        }
    }

    private static class LRItem {
        public String item;
        public List<String> start = new ArrayList<>();

        public LRItem(String item) {
            super();
            this.item = item;
        }


    }
}
