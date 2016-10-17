import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GLA {

	private static Map<String, String> regularneDefinicije = new HashMap<>();
	private static List<String> stanjaLA = new ArrayList<>();
	private static List<String> leksickeJedinke = new ArrayList<>();

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
				String izraz = escape(tmp[1]);

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

				String tmp[] = linija.split(">");

				String stanje = tmp[0].substring(1, tmp[0].length());
				String regDef = escape(tmp[1]);

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

	static void expandRegularDefinitions() {

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

	/**
	 * Escapes all occurences of '\' before a character.
	 * 
	 * For example '\|' is '|' after escape, also '\\' == '\'.
	 * 
	 * @param s
	 *            String to escape.
	 * @return Returns escaped string.
	 */
	private static String escape(String s) {
		StringBuilder sb = new StringBuilder();
		char ss[] = s.toCharArray();

		for (int i = 0; i < ss.length; i++) {
			// slučaj \\
			if (ss[i] == '\\') {
				if (ss[i + 1] == 'n') {
					sb.append('\n');
				} else if (ss[i + 1] == 't') {
					sb.append('\t');
				} else if (ss[i + 1] == '_') {
					sb.append(' ');
				} else {
					sb.append(ss[i + 1]);
				}

				i++;
			} else {
				sb.append(ss[i]);
			}
		}

		return sb.toString();
	}

}
