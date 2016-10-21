import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

/**
 * Razred koji definira automat.
 *
 * @author Mato Manovic
 * @version 1.0
 */
public class Automat implements Serializable {

    public int lijevo_stanje;// pocetno stanje automata
    public int desno_stanje;// krajnje prihvatljivo stanje automata
    /**
     * Kao na utr-u pohranjujem prijlaze na nacin da je kljuc oblika
     * "trenutnostanje prijelazniznak" a vrijednost "novostanje". Mada ce ova stanje
     * biti integeri ovdje ce biti kao String.
     */
    public Map<String, String> prijelazi = new LinkedHashMap<>();
    /**
     * koliko je trenutno pohranjenih stanja
     */
    public int brojStanja;

    public Automat(String izraz) {
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

    public boolean isValidInput(String input)  {
    	return izvrsiAutomat(input);
    }
    
    
    private boolean izvrsiAutomat(String ulazniNiz)  {
    	if(ulazniNiz.length()==0){
    		return true;
    	}
        TreeSet<String> pocetno = new TreeSet<>();
        pocetno.add(new Integer(lijevo_stanje).toString());
        int povratak = 1;
        while (true) {
            pocetno = epsilonOkolina(pocetno);
            if (pocetno.size() == povratak) {
                break;
            }
            povratak = pocetno.size();
        }

        String[] znakovi = ulazniNiz.split("");
        for (String entry : znakovi) {
            pocetno = napraviPrijelaz(pocetno, entry);

            int povratak2 = pocetno.size();
            while (true) {
                pocetno = epsilonOkolina(pocetno);
                if (pocetno.size() == povratak2) {
                    break;
                }
                povratak2 = pocetno.size();
            }

            if(pocetno.size()==0){
            	return false;
            }

        }
        return true;

    }

    private TreeSet<String> napraviPrijelaz(TreeSet<String> poc, String znak) {
        TreeSet<String> povratni = new TreeSet<>();
        for (String entry : poc) {
            String prijelaz = entry.concat("," + znak);

            String novaStanja = prijelazi.get(prijelaz);

            if (novaStanja != null) {
                String[] fieldNova = novaStanja.split(",");
                povratni.addAll(Arrays.asList(fieldNova));
            }
        }
        return povratni;
    }

    private  TreeSet<String> epsilonOkolina(TreeSet<String> pocetno) {
        TreeSet<String> okolina = new TreeSet<>();
        for (String entry : pocetno) {
            okolina.add(entry);
            String prijelaz = entry.concat(",$");
            String novaStanja = prijelazi.get(prijelaz);

            if (novaStanja != null) {
                String[] fieldNova = novaStanja.split(",");
                okolina.addAll(Arrays.asList(fieldNova));
            }
        }
        return okolina;
    }

//    private static void ispis(TreeSet<String> stanja)  {
//        int length = stanja.size();
//        if (stanja.size() == 0) {
//            System.out.print("#");
//
//        }
//        for (String entry : stanja) {
//
//            System.out.print(entry);
//            if (length > 1) {
//                System.out.print(",");
//
//            }
//            length--;
//        }
//    }

    
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("Pocetno stanje: " + lijevo_stanje + '\n');
        string.append("Konacno prihvatljivo: " + desno_stanje + "\n");
        for (Entry<String, String> prijelaz : prijelazi.entrySet()) {
            string.append(prijelaz.getKey() + "->" + prijelaz.getValue() + "\n");
        }
        return string.toString();
    }
}
