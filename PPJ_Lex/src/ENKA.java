import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ENKA {
	List<String> stanja;
	Map<String, List<String>> prijelazi;

	public static void main(String[] args) {
		new ENKA().pretvori(
				"0x((0|1|2|3|4|5|6|7|8|9)|a|b|c|d|e|f|A|B|C|D|E|F)((0|1|2|3|4|5|6|7|8|9)|a|b|c|d|e|f|A|B|C|D|E|F)*");
	}

	public ENKA(String regex) {

	}

	public ENKA() {
	}

	private boolean je_operator(String regex, int index) {
		int count = 0;

		while (index - 1 >= 0 && regex.charAt(index - 1) == '\\') {
			count++;
			index--;
		}

		return count % 2 == 0;
	}

	public void pretvori(String regex) {
		List<String> izbori = new ArrayList<>();
		int br_zagrada = 0;
		int start = 0;
		for (int i = 0; i < regex.length(); i++) {
			if (regex.charAt(i) == '(' && je_operator(regex, i)) {
				br_zagrada++;
			} else if (regex.charAt(i) == ')' && je_operator(regex, i)) {
				br_zagrada--;
			} else if (br_zagrada == 0 && regex.charAt(i) == '|' && je_operator(regex, i)) {
				izbori.add(regex.substring(start, i));
				start = i + 1;
			}
		}
		if (start > 0) {
			izbori.add(regex.substring(start, regex.length()));
		}

		for (String s : izbori) {
			System.out.println(s);
		}
	}
}
