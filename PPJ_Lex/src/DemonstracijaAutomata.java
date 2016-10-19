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
		Scanner scanner= new Scanner(System.in);
		Automat automat= new Automat();
		UtilityClass.pretvori(scanner.nextLine(), automat);
		System.out.println(automat);
	}
}
