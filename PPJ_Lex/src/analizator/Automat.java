package analizator;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

/**
 * Razred koji definira automat.
 *
 * @author Mato Manovic
 * @version 1.0
 */
public class Automat implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public int lijevo_stanje;// pocetno stanje automata
    public int desno_stanje;// krajnje prihvatljivo stanje automata
    /**
     * Kao na utr-u pohranjujem prijlaze na nacin da je kljuc oblika
     * "trenutnostanje prijelazniznak" a vrijednost "novostanje". Mada ce ova
     * stanje biti integeri ovdje ce biti kao String.
     */
    public Map<String, String> prijelazi = new LinkedHashMap<>();

    private Set<String> stanja = new HashSet<>();

    /**
     * koliko je trenutno pohranjenih stanja
     */
    public int brojStanja;

    public Automat(String regex) {
        pretvori(regex);
        stanja.add(Integer.toString(lijevo_stanje));
    }

    public void reset() {
        stanja.clear();
        stanja.add(Integer.toString(lijevo_stanje));
    }

    public boolean next(String znak) {
        stanja = napraviPrijelaz(stanja, znak);

        stanja.addAll(epsilonOkolina(stanja));

        return stanja.contains(Integer.toString(desno_stanje));
    }

    public LexerRule.MatchState isValidInput(String input) {
        return izvrsiAutomat(input);
    }

    private LexerRule.MatchState izvrsiAutomat(String ulazniNiz) {
        TreeSet<String> pocetno = new TreeSet<>();
        pocetno.add(String.valueOf(lijevo_stanje));
        int povratak = 1;
        while (true) {
            pocetno = epsilonOkolina(pocetno);
            if (pocetno.size() == povratak) {
                break;
            }
            povratak = pocetno.size();
        }

        if (ulazniNiz.length() == 0) {
            return new LexerRule.MatchState(ulazniNiz.length(), pocetno.contains(String.valueOf(desno_stanje)));
        }

        String[] znakovi = ulazniNiz.split("");
        int cnt = 0;
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

            if (pocetno.size() == 0) {
                return new LexerRule.MatchState(cnt, false);
            }
            cnt++;

        }

        return new LexerRule.MatchState(ulazniNiz.length(), pocetno.contains(String.valueOf(desno_stanje)));
    }

    private TreeSet<String> napraviPrijelaz(Set<String> poc, String znak) {
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

    private TreeSet<String> epsilonOkolina(Set<String> pocetno) {
        TreeSet<String> okolina = new TreeSet<>();
        for (String entry : pocetno) {
            okolina.add(entry);
            String prijelaz = entry.concat(",$$");
            String novaStanja = prijelazi.get(prijelaz);

            if (novaStanja != null) {
                String[] fieldNova = novaStanja.split(",");
                okolina.addAll(Arrays.asList(fieldNova));
            }
        }
        return okolina;
    }

    // private static void ispis(TreeSet<String> stanja) {
    // int length = stanja.size();
    // if (stanja.size() == 0) {
    // System.out.print("#");
    //
    // }
    // for (String entry : stanja) {
    //
    // System.out.print(entry);
    // if (length > 1) {
    // System.out.print(",");
    //
    // }
    // length--;
    // }
    // }

    /**
     * Metoda koja pretvara regularni izraz u automat
     *
     * @param izraz   Regularni izraz.
     * @param automat Objekt automat.
     */
    private void pretvori(String izraz) {
        // rastavljaje regularnog izraza na podizraze
        List<String> izbori = rastaviizraz(izraz);

        int lijevo_stanje = brojStanja++;
        int desno_stanje = brojStanja++;

        // ako ima vise izbora obradimo podizbore inace obradimo cijeli izraz
        if (izbori.size() != 0) {
            for (String izbor : izbori) {
                pretvori(izbor);
                dodajEpsilonPrijelaz(lijevo_stanje, this.lijevo_stanje);
                dodajEpsilonPrijelaz(this.desno_stanje, desno_stanje);
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

                    a = brojStanja++;
                    b = brojStanja++;
                    dodajPrijelaz(a, b, prijelazniZnak);
                } else {
                    // slucaj 2
                    if (tmpIzraz[i] == '\\') {
                        prefiksirano = true;
                        continue;
                    }
                    if (tmpIzraz[i] != '(') {
                        // slucaj 2a
                        a = brojStanja++;
                        b = brojStanja++;
                        if (tmpIzraz[i] == '$') {
                            dodajEpsilonPrijelaz(a, b);
                        } else {
                            dodajPrijelaz(a, b, tmpIzraz[i]);
                        }
                    } else {
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
                        pretvori(new String(tmpIzraz, i + 1, j - i - 1));
                        a = this.lijevo_stanje;
                        b = this.desno_stanje;
                        i = j;
                    }
                }

                // provjera ponavljanja(Kleenov operator)
                if (i + 1 < duljinaIzraza && tmpIzraz[i + 1] == '*') {
                    int x = a;
                    int y = b;
                    a = brojStanja++;
                    b = brojStanja++;
                    dodajEpsilonPrijelaz(a, x);
                    dodajEpsilonPrijelaz(y, b);
                    dodajEpsilonPrijelaz(a, b);
                    dodajEpsilonPrijelaz(y, x);
                    i++;
                }

                // povezivanje s ostatkom automata
                dodajEpsilonPrijelaz(zadnje_stanje, a);
                zadnje_stanje = b;
            }
            dodajEpsilonPrijelaz(zadnje_stanje, desno_stanje);
        }
        this.lijevo_stanje = lijevo_stanje;
        this.desno_stanje = desno_stanje;

    }

    /**
     * Pomocna metoda koja provjerava da li je znak na zadanom indeksu operator
     * tj. ako nije onda je prefiksiran. Valjda ju je Nidjo dobro napisao :)
     *
     * @param regex Regularni izraz
     * @param index Indeks znaka
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
     * Pomocna metoda koja rastavlja pocetni regularni izraz na podizraze. Nidjo
     * ju je takodjer pisao valjda je dobro napisana.
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
     * Pomocna metoda koja dodaje u automat prijelaz.Ovdje je epsilon prijelaz
     * pa je zu prijelazni znak jednak znaku $$.
     *
     * @param automat  Automat
     * @param pocetno  Trenutno stanje.
     * @param sljedece Sljedece stanje.
     */
    private void dodajEpsilonPrijelaz(int pocetno, int sljedece) {
        String key = pocetno + ",$$";
        if (!prijelazi.containsKey(key)) {
            prijelazi.put(key, new Integer(sljedece).toString());
        } else {
            String value = prijelazi.get(key) + "," + new Integer(sljedece).toString();
            prijelazi.put(key, value);
        }
    }

    /**
     * Pomocna metoda koja dodaje u automat prijelaz.
     *
     * @param automat  Automat
     * @param pocetno  Trenutno stanje.
     * @param sljedece Sljedece stanje.
     * @param znak     Prijelazni znak.
     */
    private void dodajPrijelaz(int pocetno, int sljedece, char znak) {
        String key = pocetno + "," + znak;
        if (!prijelazi.containsKey(key)) {
            prijelazi.put(key, new Integer(sljedece).toString());
        } else {
            String value = prijelazi.get(key) + "," + new Integer(sljedece).toString();
            prijelazi.put(key, value);
        }
    }

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
