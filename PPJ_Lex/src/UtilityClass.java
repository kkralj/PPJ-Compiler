import java.util.ArrayList;
import java.util.List;

/**
 * Razred koji sadrži statičku metodu pretvori koja prima regularni izraz i
 * pretvara ga u automat.Doslovno sam pretipkao one pseudokodove iz uputa.
 * 
 * @author Mato Manovic
 * @version 1.0
 */
public class UtilityClass {
	/**
	 * Metoda koja pretvara regularni izraz u automat
	 * 
	 * @param izraz
	 *            Regularni izraz.
	 * @param automat
	 *            Objekt automat.
	 */
	public static void pretvori(String izraz, Automat automat) {
		

	}

	/**
	 * Pomoćna metoda koja provjerava da li je znak na zadanom indeksu operator
	 * tj. ako nije onda je prefiksiran. Valjda ju je Niđo dobro napisao :)
	 * 
	 * @param regex
	 *            Regularni izraz
	 * @param index
	 *            Indeks znaka
	 * @return Je li znak operator.
	 */
	private static boolean je_operator(String regex, int index) {
		int count = 0;

		while (index - 1 >= 0 && regex.charAt(index - 1) == '\\') {
			count++;
			index--;
		}

		return count % 2 == 0;
	}

	/**
	 * Pomoćna metoda koja rastavlja početni regularni izraz na podizraze. Niđo
	 * ju je također pisao valjda je dobro napisana.
	 * 
	 * @param regex
	 *            Izraz kojeg rastavljamo.
	 * @return Listu podizraza.
	 */
	private static List<String> rastaviizraz(String regex) {
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

//		 for (String s : izbori) {
//		 System.out.println(s);
//		 }
		return izbori;
	}

	/**
	 * Pomoćna metoda koja vraća broj novog stanja.
	 * 
	 * @param automat
	 *            Automat.
	 * @return Novo stanje.
	 */
	private static int novo_stanje(Automat automat) {
		automat.brojStanja = automat.brojStanja + 1;
		return automat.brojStanja - 1;
	}

	/**
	 * Pomoćna metoda koja dodaje u automat prijelaz.Ovdje je epsilon prijelaz
	 * pa je zu prijelazni znak jednak znaku $.
	 * 
	 * @param automat
	 *            Automat
	 * @param pocetno
	 *            Trenutno stanje.
	 * @param sljedece
	 *            Sljedece stanje.
	 */
	private static void dodajEpsilonPrijelaz(Automat automat, int pocetno, int sljedece) {
		String key = pocetno + ",$";
		if (!automat.prijelazi.containsKey(key)) {
			automat.prijelazi.put(key, new Integer(sljedece).toString());
		} else {
			String value = automat.prijelazi.get(key) + "," + new Integer(sljedece).toString();
			automat.prijelazi.put(key, value);
		}
	}

	/**
	 * Pomoćna metoda koja dodaje u automat prijelaz.
	 * 
	 * @param automat
	 *            Automat
	 * @param pocetno
	 *            Trenutno stanje.
	 * @param sljedece
	 *            Sljedece stanje.
	 * @param znak
	 *            Prijelazni znak.
	 */
	private static void dodajPrijelaz(Automat automat, int pocetno, int sljedece, char znak) {
		String key = pocetno + "," + znak;
		if (!automat.prijelazi.containsKey(key)) {
			automat.prijelazi.put(key, new Integer(sljedece).toString());
		} else {
			String value = automat.prijelazi.get(key) + "," + new Integer(sljedece).toString();
			automat.prijelazi.put(key, value);
		}
	}

}
