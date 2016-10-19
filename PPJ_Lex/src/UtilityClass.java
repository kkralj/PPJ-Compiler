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
		// rastavljaje regularnog izraza na podizraze
		List<String> izbori = rastaviizraz(izraz);

		int lijevo_stanje = novo_stanje(automat);
		int desno_stanje = novo_stanje(automat);

		// ako ima vise izbora obradimo podizbore inace obradimo cijeli izraz
		if (izbori.size() != 0) {
			for (String izbor : izbori) {
				pretvori(izbor, automat);
				dodajEpsilonPrijelaz(automat, lijevo_stanje, automat.lijevo_stanje);
				dodajEpsilonPrijelaz(automat, automat.desno_stanje, desno_stanje);
			}
		} else {
			boolean prefiksirano = false;
			int zadnje_stanje = lijevo_stanje;
			int duljinaIzraza = izraz.length();
			// izraz u polje znakova
			char[] tmpIzraz = izraz.toCharArray();

			for (int i = 0; i < duljinaIzraza; i++) {
				int a, b;
				// slucaj 1
				if (prefiksirano == true) {
					prefiksirano = false;
					char prijelazniZnak;
					if (tmpIzraz[i] == 't') {
						prijelazniZnak = '\t';
					} else if (tmpIzraz[i] == 'n') {
						prijelazniZnak = '\n';
					} else if (tmpIzraz[i] == '_') {
						prijelazniZnak = ' ';
					} else {
						prijelazniZnak = tmpIzraz[i];
					}

					a = novo_stanje(automat);
					b = novo_stanje(automat);
					dodajPrijelaz(automat, a, b, prijelazniZnak);
				} else {
					// slucaj 2
					if (tmpIzraz[i] == '\\') {
						prefiksirano = true;
						continue;
					}
					if (tmpIzraz[i] != '(') {
						// slucaj 2a
						a = novo_stanje(automat);
						b = novo_stanje(automat);
						if (tmpIzraz[i] == '$') {
							dodajEpsilonPrijelaz(automat, a, b);
						} else {
							dodajPrijelaz(automat, a, b, tmpIzraz[i]);
						}
					} else {
						// slučaj 2b
						int j = i;
						int brojOtvorenihZagrada = 0;
						// trazim odgovarajucu zatvorenu zagradu
						do {
							j++;
							if (tmpIzraz[j] == ')' && brojOtvorenihZagrada == 0) {
								break;
							}
							if (tmpIzraz[j] == '(') {
								brojOtvorenihZagrada++;
							} else if (tmpIzraz[j] == ')') {
								brojOtvorenihZagrada--;
							}
						} while (true);
						pretvori(new String(tmpIzraz, i + 1, j - i - 1), automat);
						a = automat.lijevo_stanje;
						b = automat.desno_stanje;
						i = j;
					}
				}

				// provjera ponavljanja(Kleenov operator)
				if (i + 1 < duljinaIzraza && tmpIzraz[i + 1] == '*') {
					int x = a;
					int y = b;
					a = novo_stanje(automat);
					b = novo_stanje(automat);
					dodajEpsilonPrijelaz(automat, a, x);
					dodajEpsilonPrijelaz(automat, y, b);
					dodajEpsilonPrijelaz(automat, a, b);
					dodajEpsilonPrijelaz(automat, y, x);
					i++;
				}

				// povezivanje s ostatkom automata
				dodajEpsilonPrijelaz(automat, zadnje_stanje, a);
				zadnje_stanje = b;
			}
			dodajEpsilonPrijelaz(automat, zadnje_stanje, desno_stanje);
			automat.lijevo_stanje = lijevo_stanje;
			automat.desno_stanje = desno_stanje;
		}

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
	 * Pomoćna metoda koja rastavlja početni regularni izraz na podizraze.
	 * Niđo ju je također pisao valjda je dobro napisana.
	 * 
	 * @param regex Izraz kojeg rastavljamo.
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

		// for (String s : izbori) {
		// System.out.println(s);
		// }
		return izbori;
	}

	/**
	 * Pomoćna metoda koja vraća broj novog stanja.
	 * 
	 * @param automat Automat.
	 * @return Novo stanje.
	 */
	private static int novo_stanje(Automat automat) {
		automat.brojStanja = automat.brojStanja + 1;
		return automat.brojStanja - 1;
	}

	/**
	 * Pomoćna metoda koja dodaje u automat prijelaz.Ovdje je epsilon prijelaz
	 * pa je zu prijelazni znak jednak znaku $.
	 * @param automat Automat
	 * @param pocetno Trenutno stanje.
	 * @param sljedece Sljedece stanje.
	 */
	private static void dodajEpsilonPrijelaz(Automat automat, int pocetno, int sljedece) {
		automat.prijelazi.put(pocetno + " $", new Integer(sljedece).toString());
	}

	/**
	 * Pomoćna metoda koja dodaje u automat prijelaz.
	 * @param automat Automat
	 * @param pocetno Trenutno stanje.
	 * @param sljedece Sljedece stanje.
	 * @param znak Prijelazni znak.
	 */
	private static void dodajPrijelaz(Automat automat, int pocetno, int sljedece, char znak) {
		automat.prijelazi.put(pocetno + " " + znak, new Integer(sljedece).toString());
	}

}
