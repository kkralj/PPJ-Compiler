import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
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

    public Automat() {

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
