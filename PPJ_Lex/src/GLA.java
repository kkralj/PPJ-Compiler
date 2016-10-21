import java.util.*;

/**
 * Generator for Lexical Analyzer.
 *
 * @author nikola
 */
public class GLA {

    /**
     * Regular definitions (name, regular expression)
     */
    private static Map<String, String> regularneDefinicije = new HashMap<>();

    /**
     * States of Lexical Analyzer.
     */
    private static List<String> stanjaLA = new ArrayList<>();

    /**
     * Names of lexical classes.
     */
    private static List<String> leksickeJedinke = new ArrayList<>();

    /**
     * Main method of the program.
     *
     * @param args
     *            Not used here.
     */
    public static void main(String[] args) {
        input();
    }

    /**
     * Reads Lexical Analyzer definition.
     */
    private static void input() {
        try (Scanner scanner = new Scanner(System.in)) {
            String linija = scanner.nextLine().trim();

            // regularne definicije
            while (linija.startsWith("{") && scanner.hasNextLine()) {
                String tmp[] = linija.split(" ");

                tmp[0] = tmp[0].substring(1, tmp[0].length() - 1);
                String naziv = tmp[0];
                String izraz = expandRegularDefinition(tmp[1]);

                regularneDefinicije.put(naziv, izraz);

                linija = scanner.nextLine();

                System.out.println(naziv + ", " + izraz);
            }

            // stanja
            while (!linija.startsWith("%X")) {
                linija = scanner.nextLine().trim();
            }

            skipSplitAdd(linija, stanjaLA);

            // leksicke jedinke
            while (!linija.startsWith("%L")) {
                linija = scanner.nextLine().trim();
            }

            skipSplitAdd(linija, leksickeJedinke);

            // pravila leksickog analizatora

            while (scanner.hasNextLine()) {
                while (!linija.startsWith("<")) {
                    linija = scanner.nextLine();
                }

                String tmp[] = linija.split(">", 2);

                String stanje = tmp[0].substring(1, tmp[0].length());
                String regDef = tmp[1];

                System.out.println(stanje + "<> " + regDef);

                scanner.nextLine(); // preskoci {

                linija = scanner.nextLine().trim();
                while (!linija.equals("}")) {
                    // radi nesto s naredbom

                    linija = scanner.nextLine().trim();
                }
            }

        }
    }

    /**
     * Expands all references in regular definitions.
     * <p>
     * <p>
     * For example: {reg1} 1|2|3 {reg2} {reg1}|4|5
     * <p>
     * {reg2} becomes (1|2|3)|4|5
     *
     * @param regDef
     *            regular definition to expand.
     * @return Returns regular definition with all references expanded.
     */
    private static String expandRegularDefinition(String regDef) {
        // nadji reference na regularne definicije
        int start = regDef.indexOf('{');

        while (start >= 0) {
            // provjeri je li { escapean
            if (!isEscaped(regDef, start)) {
                int end = regDef.indexOf('}', start);

                // provjeri je li } escapean
                while (isEscaped(regDef, end)) {
                    end = regDef.indexOf('}', end + 1);
                }

                String regRef = regDef.substring(start + 1, end);

                regDef = regDef.substring(0, start) + "(" + regularneDefinicije.get(regRef) + ")"
                        + regDef.substring(end + 1, regDef.length());
            }

            start = regDef.indexOf('{', start + 1);
        }

        return regDef;
    }

    /**
     * Checks if character at given position is escaped with \.
     * 
     * @param s
     *            String to check.
     * @param pos
     *            Position of character in the string.
     * @return True if character at provided position is escaped, false
     *         otherwise.
     */
    private static boolean isEscaped(String s, int pos) {
        if (pos < 0 || pos >= s.length()) {
            throw new IllegalArgumentException();
        }

        if (pos == 0) {
            return false;
        }

        int count = 0;
        pos--;

        while (pos >= 0 && s.charAt(pos) == '\\') {
            pos--;
            count++;
        }

        return count % 2 != 0;
    }

    /**
     * Splits given string by empty spaces and adds all but first to given list.
     *
     * @param s
     *            String to split.
     * @param list
     *            List to add split strings to.
     */
    private static void skipSplitAdd(String s, List<String> list) {
        String tmp[] = s.split(" ");

        for (int i = 1; i < tmp.length; i++) {
            list.add(tmp[i]);

            System.out.println(tmp[i]);
        }
    }

    // Mislim da je nepotrebno escapat to se radi u pretvorbi regularnog izraza
    // u automat
    /**
     * Escapes all occurences of '\' before a character.
     * <p>
     * For example '\|' is '|' after escape, also '\\' == '\'.
     *
     * @param s
     *            String to escape.
     * @return Returns escaped string.
     */
    // private static String escape(String s) {
    // StringBuilder sb = new StringBuilder();
    // char ss[] = s.toCharArray();
    //
    // for (int i = 0; i < ss.length; i++) {
    // // slučaj \\
    // if (ss[i] == '\\') {
    // if (ss[i + 1] == 'n') {
    // sb.append('\n');
    // } else if (ss[i + 1] == 't') {
    // sb.append('\t');
    // } else if (ss[i + 1] == '_') {
    // sb.append(' ');
    // } else {
    // sb.append(ss[i + 1]);
    // }
    //
    // i++;
    // } else {
    // sb.append(ss[i]);
    // }
    // }
    //
    // return sb.toString();
    // }

}
