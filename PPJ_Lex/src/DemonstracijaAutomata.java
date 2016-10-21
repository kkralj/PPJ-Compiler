import java.io.IOException;
import java.util.Scanner;

/**
 * Klasa koja sadrzi main metodu gdje u standardni ulaz se upise regularni izraz
 * i zatim metoda ispise automat.
 *
 * @author Mato Manovic
 * @version 1.0
 */
public class DemonstracijaAutomata {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Automat automat = new Automat();
        UtilityClass.pretvori("ca*b", automat);
        //System.out.println(automat);
        System.out.println(automat.isValidInput("caaaaabbbbb"));
        System.out.println(automat.isValidInput("caaaaab"));
        System.out.println(automat.isValidInput("cb"));
        System.out.println(automat.isValidInput("cc"));
        System.out.println(automat.isValidInput("cd"));
        System.out.println(automat.isValidInput("c"));
        System.out.println(automat.isValidInput("caa"));
        System.out.println(automat.isValidInput("d"));
        System.out.println(automat.isValidInput(""));//ovo sam stavio isto da vraca
        //true mada pretpostavljam da nece biti praznih inputa
        
    }
}
