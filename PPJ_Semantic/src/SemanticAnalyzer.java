import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class SemanticAnalyzer {

	private SyntaxTree syntaxTree;
	private Scope scope;
	// check if provided characters are ASCII compliant
	static CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();

	public SemanticAnalyzer(SyntaxTree syntaxTree) {
		this.syntaxTree = syntaxTree;
	}

	public void analyze() throws SemanticAnalyserException {
		this.scope = new Scope(null); // global context, no parent
		check(syntaxTree.getRoot());
	}

	private boolean check(Node node) throws SemanticAnalyserException {
		completeActions(node);

		return false;
	}

	/**
	 * Returns true if new context got assigned to the given Node.
	 */
	private boolean checkScope(Node root) { // TODO: ovo treba nadopuniti
		String rootLabel = root.getLabel();

		if (rootLabel.equals("<slozena_naredba>") || rootLabel.equals("<vanjska_deklaracija>")
				|| rootLabel.equals("<definicija_funkcije>")) {
			this.scope = new Scope(this.scope); // change current context to a
												// new one
			return true;
		}

		return false;
	}

	private void completeActions(Node node) throws SemanticAnalyserException {
		String nodeName = node.getLabel();

		switch (nodeName) {
		case "<primarni_izraz>": {
			primarni_izraz(node);
			break;
		}
		case "<postfiks_izraz>": {
			postfiks_izraz(node);
			break;
		}
		case "<lista_argumenata>": {
			lista_argumenata(node);
			break;
		}
		case "<unarni_izraz>": {
			unarni_izraz(node);
			break;
		}
		case "<unarni_operator>": {
			unarni_operator(node);
			break;
		}
		case "<cast_izraz>": {
			cast_izraz(node);
			break;
		}
		case "<ime_tipa>": {
			ime_tipa(node);
			break;
		}
		case "<specifikator_tipa>": {
			specifikator_tipa(node);
			break;
		}
		case "<multiplikativni_izraz>": {
			multiplikativni_izraz(node);
			break;
		}
		case "<aditivni_izraz>": {
			aditivni_izraz(node);
			break;
		}
		case "<odnosni_izraz>": {
			odnosni_izraz(node);
			break;
		}
		case "<jednakosni_izraz>": {
			jednakosni_izraz(node);
			break;
		}
		case "<bin_i_izraz>": {
			bin_i_izraz(node);
			break;
		}
		case "<bin_xili_izraz>": {
			bin_xili_izraz(node);
			break;
		}
		case "<bin_ili_izraz>": {
			bin_ili_izraz(node);
			break;
		}
		case "<log_i_izraz>": {
			log_i_izraz(node);
			break;
		}
		case "<log_ili_izraz>": {
			log_ili_izraz(node);
			break;
		}
		case "<izraz_pridruzivanja>": {
			izraz_pridruzivanja(node);
			break;
		}
		case "<izraz>": {
			izraz(node);
			break;
		}
		case "<slozena_naredba>": {
			slozena_naredba(node);
			break;
		}
		case "<lista_naredbi>": {
			lista_naredbi(node);
			break;
		}
		case "<naredba>": {
			naredba(node);
			break;
		}
		case "<izraz_naredba>": {
			izraz_naredba(node);
			break;
		}
		case "<naredba_grananja>": {
			naredba_grananja(node);
			break;
		}
		case "<naredba_petlje>": {
			naredba_petlje(node);
			break;
		}
		case "<naredba_skoka>": {
			naredba_skoka(node);
			break;
		}
		case "<prijevodna_jedinica>": {
			prijevodna_jedinica(node);
			break;
		}
		case "<vanjska_deklaracija>": {
			vanjska_deklaracija(node);
			break;
		}
		case "<definicija_funkcije>": {
			definicija_funkcije(node);
			break;
		}
		case "<lista_parametara>": {
			lista_parametara(node);
			break;
		}
		case "<deklaracija_parametra>": {
			deklaracija_parametra(node);
			break;
		}
		case "<lista_deklaracija>": {
			lista_deklaracija(node);
			break;
		}
		case "<deklaracija>": {
			deklaracija(node);
			break;
		}
		case "<lista_init_deklaratora>": {
			lista_init_deklaratora(node);
			break;
		}
		case "<init_deklarator>": {
			init_deklarator(node);
			break;
		}
		case "<izravni_deklarator>": {
			izravni_deklarator(node);
			break;
		}
		case "<inicijalizator>": {
			inicijalizator(node);
			break;
		}
		case "<lista_izraza_pridruzivanja>": {
			lista_izraza_pridruzivanja(node);
			break;
		}

		default:
			break;
		}
	}

	private void lista_deklaracija(Node root) {
	}

	private void deklaracija_parametra(Node root) {
	}

	private void deklaracija(Node root) {
	}

	private void lista_init_deklaratora(Node root) {
	}

	private void init_deklarator(Node root) {
		// <init_deklarator> ::= <izravni_deklarator>
		// | <izravni_deklarator> OP_PRIDRUZI <inicijalizator>

	}

	private void izravni_deklarator(Node root) {
	}

	private void inicijalizator(Node root) {
	}

	private void lista_izraza_pridruzivanja(Node root) {
	}

	private void lista_parametara(Node root) {
	}

	private void definicija_funkcije(Node root) {
		// <definicija_funkcije> ::= <ime_tipa> IDN L_ZAGRADA KR_VOID D_ZAGRADA
		// <slozena_naredba>
		// | <ime_tipa> IDN L_ZAGRADA <lista_parametara> D_ZAGRADA
		// <slozena_naredba>

		if (root.getChildren().get(3).getData().startsWith("KR_VOID")) {
			Node ime_tipa = root.getChildren().get(0);
			Node slozena_naredba = root.getChildren().get(5);

			// 1. provjeri (<ime_tipa>)
			// 2. <ime_tipa>.tip != const(T )
			// 3. ne postoji prije definirana funkcija imena IDN.ime
			// 4. ako postoji deklaracija imena IDN.ime u globalnom djelokrugu
			// onda je pripadni
			// tip te deklaracije funkcija(void → <ime_tipa>.tip)
			// 5. zabilježi definiciju i deklaraciju funkcije
			// 6. provjeri (<slozena_naredba>)

		} else {

		}
	}

	/**
	 * Nezavršni znak <naredba_skoka> generira continue, break i return naredbe.
	 */
	private void naredba_skoka(Node root) {
		// <naredba_skoka> ::= KR_CONTINUE TOCKAZAREZ
		// | KR_BREAK TOCKAZAREZ
		// | KR_RETURN TOCKAZAREZ
		// | KR_RETURN <izraz> TOCKAZAREZ

		if (root.getChildren().size() == 3 && root.getChildren().get(1).getData().equals("<izraz>")) {
			// 1. provjeri (<izraz>)
			// 2. naredba se nalazi unutar funkcije tipa funkcija(params → pov )
			// i vrijedi <izraz>.tip ∼ pov

		}

	}

	private void naredba_petlje(Node root) {
		// <naredba_petlje> ::= KR_WHILE L_ZAGRADA <izraz> D_ZAGRADA <naredba>
		// | KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> D_ZAGRADA
		// <naredba>
		// | KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> <izraz> D_ZAGRADA
		// <naredba>

		if (root.getChildren().size() == 5) {
			Node izraz = root.getChildren().get(2);
			if (!implicitInt(izraz.getType())) {
				throw new IllegalArgumentException("Greska u naredbi grananja.");
			}

		} else {
			Node izrazNaredba = root.getChildren().get(3);
			if (!implicitInt(izrazNaredba.getType())) {
				throw new IllegalArgumentException("Greska u naredbi grananja.");
			}
		}

	}

	private void vanjska_deklaracija(Node root) {
	}

	private void prijevodna_jedinica(Node root) {
		// do nothing
	}

	private void naredba_grananja(Node root) {
		// <naredba_grananja> ::= KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba>
		// | KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba> KR_ELSE <naredba>

		Node izraz = root.getChildren().get(2);
		if (!implicitInt(izraz.getType())) {
			throw new IllegalArgumentException("Greska u naredbi grananja.");
		}
	}

	private void izraz_naredba(Node root) {
		// <izraz_naredba> ::= TOCKAZAREZ
		// | <izraz> TOCKAZAREZ

		if (root.getChildren().size() == 1) {
			root.setType("int");

		} else {
			Node izraz = root.getChildren().get(0);
			root.setType(izraz.getType());
		}

	}

	private void naredba(Node root) {
		// <naredba> ::= <slozena_naredba>
		// | <izraz_naredba>
		// | <naredba_grananja>
		// | <naredba_petlje>
		// | <naredba_skoka>

		if (root.getChildren().get(0).getData().equals("<naredba_skoka>")) {
			Node child = root.getChildren().get(0);
			root.setType(child.getType());
			root.setLeftOK(child.isLeftOK());
		}
	}

	private void lista_naredbi(Node root) {
		// <lista_naredbi> ::= <naredba>
		// | <lista_naredbi> <naredba>

		// do nothing
	}

	/**
	 * Nezavršni znak <slozena_naredba> predstavlja blok naredbi koji opcionalno
	 * počinje lis- tom deklaracija. Svaki blok je odvojeni djelokrug, a
	 * nelokalnim imenima se pristupa u ugniježdujućem bloku (i potencijalno
	 * tako dalje sve do globalnog djelokruga)
	 */
	private void slozena_naredba(Node root) {
		// <slozena_naredba> ::= L_VIT_ZAGRADA <lista_naredbi> D_VIT_ZAGRADA
		// | L_VIT_ZAGRADA <lista_deklaracija> <lista_naredbi> D_VIT_ZAGRADA

		if (root.getChildren().size() == 3) {
			// do nothing
		} else {
			// do nothing
		}

	}

	private void log_ili_izraz(Node root) {
		// <log_ili_izraz> ::= <log_i_izraz>
		// | <log_ili_izraz> OP_ILI <log_i_izraz>

		if (root.getChildren().size() == 1) {
			Node logIizraz = root.getChildren().get(0);
			root.setType(logIizraz.getType());
			root.setLeftOK(logIizraz.isLeftOK());

		} else {
			Node logIliIzraz = root.getChildren().get(2);
			Node logIizraz = root.getChildren().get(0);

			if (implicitInt(logIizraz.getType()) && implicitInt(logIliIzraz.getType())) {
				root.setType("int");
				root.setLeftOK(false);

			} else {
				throw new IllegalArgumentException("Krivi log_ili_izraz");
			}
		}
	}

	private void log_i_izraz(Node root) {
		// <log_i_izraz> ::= <bin_ili_izraz>
		// | <log_i_izraz> OP_I <bin_ili_izraz>

		if (root.getChildren().size() == 1) {
			Node binIliIzraz = root.getChildren().get(0);
			root.setType(binIliIzraz.getType());
			root.setLeftOK(binIliIzraz.isLeftOK());

		} else {
			Node logIizraz = root.getChildren().get(0);
			Node binIliIzraz = root.getChildren().get(2);

			if (implicitInt(logIizraz.getType()) && implicitInt(binIliIzraz.getType())) {
				root.setType("int");
				root.setLeftOK(false);

			} else {
				throw new IllegalArgumentException("Krivi log_i_izraz");
			}
		}
	}

	private void bin_ili_izraz(Node root) {
		// <bin_ili_izraz> ::= <bin_xili_izraz>
		// | <bin_ili_izraz> OP_BIN_ILI <bin_xili_izraz>

		if (root.getChildren().size() == 1) {
			Node binXiliIzraz = root.getChildren().get(0);
			root.setType(binXiliIzraz.getType());
			root.setLeftOK(binXiliIzraz.isLeftOK());

		} else {
			Node binIliIzraz = root.getChildren().get(0);
			Node binXiliIzraz = root.getChildren().get(2);

			if (implicitInt(binIliIzraz.getType()) && implicitInt(binXiliIzraz.getType())) {
				root.setType("int");
				root.setLeftOK(false);

			} else {
				throw new IllegalArgumentException("Krivi bin_ili_izraz");
			}
		}
	}

	private void bin_xili_izraz(Node root) {
		// <bin_xili_izraz> ::= <bin_i_izraz>
		// | <bin_xili_izraz> OP_BIN_XILI <bin_i_izraz>

		if (root.getChildren().size() == 1) {
			Node binIizraz = root.getChildren().get(0);
			root.setType(binIizraz.getType());
			root.setLeftOK(binIizraz.isLeftOK());

		} else {
			Node binXiliIzraz = root.getChildren().get(0);
			Node binIizraz = root.getChildren().get(2);

			if (implicitInt(binXiliIzraz.getType()) && implicitInt(binIizraz.getType())) {
				root.setType("int");
				root.setLeftOK(false);

			} else {
				throw new IllegalArgumentException("Krivi bin_xili_izraz");
			}
		}
	}

	private void bin_i_izraz(Node root) {
		// <bin_i_izraz> ::= <jednakosni_izraz>
		// | <bin_i_izraz> OP_BIN_I <jednakosni_izraz>

		if (root.getChildren().size() == 1) {
			Node jednakosniIzraz = root.getChildren().get(0);
			root.setType(jednakosniIzraz.getType());
			root.setLeftOK(jednakosniIzraz.isLeftOK());

		} else {
			Node binIizraz = root.getChildren().get(0);
			Node jednakosniIzraz = root.getChildren().get(2);

			if (implicitInt(binIizraz.getType()) && implicitInt(jednakosniIzraz.getType())) {
				root.setType("int");
				root.setLeftOK(false);

			} else {
				throw new IllegalArgumentException("Krivi bin_i_izraz");
			}
		}

	}

	private void izraz_pridruzivanja(Node root) {
		// <izraz_pridruzivanja> ::= <log_ili_izraz>
		// | <postfiks_izraz> OP_PRIDRUZI <izraz_pridruzivanja>

		if (root.getChildren().size() == 1) {
			Node logIliIzraz = root.getChildren().get(0);
			root.setType(logIliIzraz.getType());
			root.setLeftOK(logIliIzraz.isLeftOK());

		} else {
			Node postfiksIzraz = root.getChildren().get(0);
			Node izrazPridruzivanja = root.getChildren().get(2);

		}
	}

	private void jednakosni_izraz(Node root) {
		// <jednakosni_izraz> ::= <odnosni_izraz>
		// | <jednakosni_izraz> OP_EQ <odnosni_izraz>
		// | <jednakosni_izraz> OP_NEQ <odnosni_izraz>

		if (root.getChildren().size() == 1) {
			Node child = root.getChildren().get(0);
			root.setType(child.getType());
			root.setLeftOK(child.isLeftOK());
		}
	}

	private void odnosni_izraz(Node root) {
		// <odnosni_izraz> ::= <aditivni_izraz>
		// | <odnosni_izraz> OP_LT <aditivni_izraz>
		// | <odnosni_izraz> OP_GT <aditivni_izraz>
		// | <odnosni_izraz> OP_LTE <aditivni_izraz>
		// | <odnosni_izraz> OP_GTE <aditivni_izraz>

		if (root.getChildren().size() == 1) {
			Node child = root.getChildren().get(0);
			root.setType(child.getType());
			root.setLeftOK(child.isLeftOK());
		}
	}

	private void multiplikativni_izraz(Node root) {
		// <multiplikativni_izraz> ::= <cast_izraz>
		// | <multiplikativni_izraz> OP_PUTA <cast_izraz>
		// | <multiplikativni_izraz> OP_DIJELI <cast_izraz>
		// | <multiplikativni_izraz> OP_MOD <cast_izraz>

		if (root.getChildren().size() == 1) {
			Node child = root.getChildren().get(0);
			root.setType(child.getType());
			root.setLeftOK(child.isLeftOK());

		} else { // ostale produkcije imaju iste akcije

			Node multiplikativniIzraz = root.getChildren().get(0);
			Node castIzraz = root.getChildren().get(2);

			if (!implicitInt(multiplikativniIzraz.getType()) || !implicitInt(castIzraz.getType())) {
				throw new IllegalArgumentException("Izraz mora biti implicitni int.");
			}

			root.setType("int");
			root.setLeftOK(false);

		}
	}

	private void specifikator_tipa(Node root) {
		String childName = root.getChildren().get(0).getData();

		if (childName.startsWith("KR_VOID")) {
			root.setType("void");

		} else if (childName.startsWith("KR_CHAR")) {
			root.setType("char");

		} else if (childName.startsWith("KR_INT")) {
			root.setType("int");

		} else {
			throw new IllegalArgumentException("Nepostojeci <specifikator tipa>");

		}

	}

	/**
	 * Nezavršni znak <ime_tipa> generira imena opcionalno const-kvalificiranih
	 * brojevnih ti- pova i ključnu riječ void. U ovim produkcijama će se
	 * izračunati izvedeno svojstvo tip koje se koristi u produkcijama gdje se
	 * <ime_tipa> pojavljuje s desne strane i dodatno će se onemogućiti tip
	 * const void (koji je sintaksno ispravan, ali nema smisla).
	 */
	private void ime_tipa(Node root) {
		// <ime_tipa> ::= <specifikator_tipa>
		// | KR_CONST <specifikator_tipa>

		if (root.getChildren().size() == 1) {
			Node specifikatorTipa = root.getChildren().get(0);
			root.setType(specifikatorTipa.getType());

		} else {
			Node specifikatorTipa = root.getChildren().get(1);
			if (specifikatorTipa.getType().equals("void")) {
				throw new IllegalArgumentException("Parameter cannot be void");
			}

			root.setType(specifikatorTipa.getType());
			root.isConstant = true;
		}

	}

	/**
	 * Nezavršni znak <cast_izraz> generira izraze s opcionalnim cast
	 * operatorom.
	 */
	private void cast_izraz(Node root) {
		// <cast_izraz> ::= <unarni_izraz>
		// | L_ZAGRADA <ime_tipa> D_ZAGRADA <cast_izraz>

		if (root.getChildren().size() == 1) {

			Node child = root.getChildren().get(0);
			root.setType(child.getType());
			root.setLeftOK(child.isLeftOK());

		} else {
			// tip ← <ime_tipa>.tip
			// l-izraz ← 0
			// 1. provjeri (<ime_tipa>)
			// 2. provjeri (<cast_izraz>)
			// 3. <cast_izraz>.tip se može pretvoriti u <ime_tipa>.tip po
			// poglavlju 4.3.1

		}
	}

	/**
	 * Nezavršni znak <unarni_operator> generira aritmetičke (PLUS i MINUS),
	 * bitovne (OP_TILDA) i logičke (OP_NEG) prefiks unarne operatore. Kako u
	 * ovim produkcijama u semantičkoj analizi ne treba ništa provjeriti,
	 * produkcije ovdje nisu navedene.
	 */
	private void unarni_operator(Node root) {
		// <unarni_operator> ::= PLUS
		// | MINUS
		// | OP_TILDA
		// | OP_NEG
	}

	/**
	 * Nezavršni znak <unarni_izraz> generira izraze s opcionalnim prefiks
	 * unarnim operatorima.
	 */
	private void unarni_izraz(Node root) {
		// <unarni_izraz> ::= <postfiks_izraz>
		// | OP_INC <unarni_izraz>
		// | OP_DEC <unarni_izraz>
		// | <unarni_operator> <cast_izraz>

		if (root.getChildren().size() == 1) {
			Node postfiksIzraz = root.getChildren().get(0);
			root.setType(postfiksIzraz.getType());
			root.setLeftOK(postfiksIzraz.isLeftOK());

		} else if (root.getChildren().get(1).getData().equals("<unarni_izraz>")) {
			Node unarniIzraz = root.getChildren().get(1);

			if (unarniIzraz.isLeftOK() && implicitInt(unarniIzraz.getType())) {
				root.setType("int");
				root.setLeftOK(false);
			} else {
				throw new IllegalArgumentException("Unarni izraz nije ispravan.");
			}

		} else {
			Node castIzraz = root.getChildren().get(1);

			if (implicitInt(castIzraz.getType())) {
				root.setType("int");
				root.setLeftOK(false);
			} else {
				throw new IllegalArgumentException("Unarni izraz nije ispravan.");
			}
		}
	}

	/**
	 * Nezavršni znak <lista_argumenata> generira listu argumenata za poziv
	 * funkcije, a za razliku od nezavršnih znakova koji generiraju izraze, imat
	 * će svojsto tipovi koje predstavlja listu tipova argumenata, s lijeva na
	 * desno.
	 */
	private void lista_argumenata(Node root) {
		// <lista_argumenata> ::= <izraz_pridruzivanja>
		// | <lista_argumenata> ZAREZ <izraz_pridruzivanja>

		if (root.getChildren().size() == 1) {

			// tipovi <- [<izraz_pridruzivanja>.tip]
			// 1. provjeri(<izraz_pridruzivanja>)

		} else {
			// tipovi ← <lista_argumenata>.tipovi + [<izraz_pridruzivanja>.tip]
			// 1. provjeri (<lista_argumenata>)
			// 2. provjeri (<izraz_pridruzivanja>)
		}

	}

	/**
	 * Nezavršni znak <postfiks_izraz> generira neki primarni izraz s
	 * opcionalnim postfiks- operatorima.
	 * 
	 * @throws SemanticAnalyserException
	 */
	private void postfiks_izraz(Node node) throws SemanticAnalyserException {
		// <postfiks_izraz> ::= <primarni_izraz>
		// | <postfiks_izraz> L_UGL_ZAGRADA <izraz> D_UGL_ZAGRADA
		// | <postfiks_izraz> L_ZAGRADA D_ZAGRADA
		// | <postfiks_izraz> L_ZAGRADA <lista_argumenata> D_ZAGRADA
		// | <postfiks_izraz> OP_INC
		// | <postfiks_izraz> OP_DEC

		InternalNodeContext context = new InternalNodeContext(node);

		if (context.isProduction("<postfiks_izraz> ::= <primarni_izraz>")) {
			if (check(context.child)) {
				context.symbolInfo.dataType = context.symbolInfo.dataType;
				context.symbolInfo.l_expr = context.symbolInfo.l_expr;
			} else {
				throw new SemanticAnalyserException("<postfiks_izraz> ::= <primarni_izraz>");
			}
		} else if (context.isProduction("<postfiks_izraz> ::= <postfiks_izraz> L_UGL_ZAGRADA <izraz> D_UGL_ZAGRADA")) {

		} else if (context.isProduction("<postfiks_izraz> ::= <postfiks_izraz> L_ZAGRADA D_ZAGRADA")) {

		} else if (context
				.isProduction("<postfiks_izraz> ::= <postfiks_izraz> L_ZAGRADA <lista_argumenata> D_ZAGRADA")) {

		} else if (context.isProduction("<postfiks_izraz> ::= <postfiks_izraz> OP_INC")) {

		} else if (context.isProduction("<postfiks_izraz> ::= <postfiks_izraz> OP_DEC")) {

		}
	}

	/**
	 * Nezavršni znak <primarni_izraz> generira najjednostavnije izraze koji se
	 * sastoje od jednog identifikatora, neke vrste konstante ili izraza u
	 * zagradi.
	 * 
	 * @throws SemanticAnalyserException
	 */
	private void primarni_izraz(Node node) throws SemanticAnalyserException {
		InternalNodeContext context = new InternalNodeContext(node);

		if (context.isProduction("<primarni_izraz> ::= IDN")) {
			String name = context.child.getTokenName();

			if (scope.isDeclared(name)) {
				context.symbolInfo.dataType = scope.getSymbolInfo(name).dataType;
				context.symbolInfo.l_expr = scope.getSymbolInfo(name).l_expr;
			} else {
				throw new SemanticAnalyserException("<primarni_izraz> ::= IDN");
			}
		} else if (context.isProduction("<primarni_izraz> ::= BROJ")) {
			context.symbolInfo.dataType = DataType.INT;
			context.symbolInfo.l_expr = false;

			if (!validIntRange(context.child.getTokenName())) {
				throw new SemanticAnalyserException("<primarni_izraz> ::= BROJ");
			}
		} else if (context.isProduction("<primarni_izraz> ::= ZNAK")) {
			context.symbolInfo.dataType = DataType.CHAR;
			context.symbolInfo.l_expr = false;

			if (!validChar(context.child.getTokenName())) {
				throw new SemanticAnalyserException("<primarni_izraz> ::= ZNAK");
			}
		} else if (context.isProduction("<primarni_izraz> ::= NIZ_ZNAKOVA")) {
			context.symbolInfo.dataType = DataType.CONST_CHAR_ARRAY;
			context.symbolInfo.l_expr = false;

			if (!validCharArray(context.child.getTokenName())) {
				throw new SemanticAnalyserException("<primarni_izraz> ::= NIZ_ZNAKOVA");
			}
		} else if (context.isProduction("<primarni_izraz> ::= L_ZAGRADA <izraz> D_ZAGRADA")) {
			Node child = node.getChildren().get(1);

			if (check(child)) {
				context.symbolInfo.dataType = child.getSymbolInfo().dataType;
				context.symbolInfo.l_expr = child.getSymbolInfo().l_expr;
			} else {
				throw new SemanticAnalyserException("<primarni_izraz> ::= L_ZAGRADA <izraz> D_ZAGRADA");
			}
		}
	}

	private void aditivni_izraz(Node root) {
		// <aditivni_izraz> ::= <multiplikativni_izraz>
		// | <aditivni_izraz> PLUS <multiplikativni_izraz>
		// | <aditivni_izraz> MINUS <multiplikativni_izraz>

		if (root.getChildren().size() == 1) {
			Node child = root.getChildren().get(0);
			root.setType(child.getType());
			root.setLeftOK(child.isLeftOK());

		} else { // same for both cases

			Node aditivniIzraz = root.getChildren().get(0);
			Node multiplikativniIzraz = root.getChildren().get(2);

			if (!implicitInt(aditivniIzraz.getType()) || !implicitInt(multiplikativniIzraz.getType())) {
				throw new IllegalArgumentException("Izraz mora biti implicitni int.");
			}

			root.setType("int");
			root.setLeftOK(false);
		}
	}

	private void izraz(Node root) {
		// <izraz> ::= <izraz_pridruzivanja>
		// | <izraz> ZAREZ <izraz_pridruzivanja>

		if (root.getChildren().size() == 1) {
			Node child = root.getChildren().get(0);
			root.setType(child.getType());
			root.setLeftOK(child.isLeftOK());

		} else {
			Node child = root.getChildren().get(2);
			root.setType(child.getType());
			root.setLeftOK(false);
		}
	}

	private static boolean implicitInt(DataType dataType) {
		return dataType == DataType.CHAR || dataType == DataType.INT;
	}

	private static boolean validIntRange(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static boolean validChar(String value) {
		return (value.length() == 3 && asciiEncoder.canEncode(value.charAt(1)))
				|| (value.length() == 4 && value.matches("'\\[tn0'\"]'"));
	}

	private static boolean validCharArray(String value) {
		return asciiEncoder.canEncode(value.subSequence(1, value.length() - 1));
	}
}
