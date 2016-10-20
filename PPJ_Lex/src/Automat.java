import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Razred koji definira automat.
 *
 * @author Mato Manovic
 * @version 1.0
 */
<<<<<<< HEAD
public class Automat implements Serializable {
=======
public class Automat {
	public int lijevo_stanje;// pocetno stanje automata
	public int desno_stanje;// krajnje prihvatljivo stanje automata
	/**
	 * Kao na utr-u pohranjujem prijlaze na nacin da je kljuc oblika
	 * "trenutnostanje prijelazniznak" a vrijednost
	 * "skup novih stanja u koje automat prelazi.Stanja ce biti odvojena razmakom"
	 * . Mada ce ova stanje biti integeri ovdje ce biti kao String.
	 */
	public Map<String, String> prijelazi = new LinkedHashMap<>();
	/**
	 * koliko je trenutno pohranjenih stanja
	 */
	public int brojStanja;
>>>>>>> 070191ad45833cbb817704986d4487c99549f030

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

    public Automat() {

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
