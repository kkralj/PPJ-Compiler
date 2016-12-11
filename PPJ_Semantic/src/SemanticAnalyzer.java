public class SemanticAnalyzer {

    private SyntaxTree syntaxTree;
    private Context context;

    public SemanticAnalyzer(SyntaxTree syntaxTree) {
        this.syntaxTree = syntaxTree;
    }

    public void analyze() {
        this.context = new Context(null); // global context, no parent
        fillNodes(syntaxTree.getRoot());
    }

    private void fillNodes(Node root) {
        boolean assignedContext = checkContext(root); // pokusaj dodijeliti kontekst

        for (Node child : root.getChildren()) {
            fillNodes(child);
        }

        /* ispis produkcija, za debugging... */
//        String productionName = root.getData() + " ::= ";
//        for (Node child : root.getChildren()) {
//            productionName += child.getData() + " ";
//        }
//        productionName = productionName.trim();
//        System.out.println(productionName);

        completeActions(root);

        if (assignedContext) { // izlazimo iz konteksta pošto je produkcija gotova
            this.context = this.context.getParent();
        }
    }

    /**
     * Returns true if new context got assigned to the given Node.
     */
    private boolean checkContext(Node root) { // TODO: ovo treba nadopuniti
        String rootName = root.getData();

        if (rootName.equals("<slozena naredba>") || rootName.equals("<vanjska_deklaracija>")) {
            this.context = new Context(this.context); // change current context to new one
            return true;
        }

        return false;
    }

    private void completeActions(Node root) {
        if (root.getChildren().isEmpty()) { // leaf
            root.setType(root.getData());
            return;
        }

        String nodeName = root.getData();

        switch (nodeName) {
            case "<primarni_izraz>": {
                primarni_izraz(root);
                break;
            }
            case "<postfiks_izraz>": {
                postfiks_izraz(root);
                break;
            }
            case "<lista_argumenata>": {
                lista_argumenata(root);
                break;
            }
            case "<unarni_izraz>": {
                unarni_izraz(root);
                break;
            }
            case "<unarni_operator>": {
                unarni_operator(root);
                break;
            }
            case "<cast_izraz>": {
                cast_izraz(root);
                break;
            }
            case "<ime_tipa>": {
                ime_tipa(root);
                break;
            }
            case "<specifikator_tipa>": {
                specifikator_tipa(root);
                break;
            }
            case "<multiplikativni_izraz>": {
                multiplikativni_izraz(root);
                break;
            }
            case "<aditivni_izraz>": {
                aditivni_izraz(root);
                break;
            }
            case "<odnosni_izraz>": {
                odnosni_izraz(root);
                break;
            }
            case "<jednakosni_izraz>": {
                jednakosni_izraz(root);
                break;
            }
            case "<bin_i_izraz>": {
                bin_i_izraz(root);
                break;
            }
            case "<bin_xili_izraz>": {
                bin_xili_izraz(root);
                break;
            }
            case "<bin_ili_izraz>": {
                bin_ili_izraz(root);
                break;
            }
            case "<log_i_izraz>": {
                log_i_izraz(root);
                break;
            }
            case "<log_ili_izraz>": {
                log_ili_izraz(root);
                break;
            }
            case "<izraz_pridruzivanja>": {
                izraz_pridruzivanja(root);
                break;
            }
            case "<izraz>": {
                izraz(root);
                break;
            }
            case "<slozena_naredba>": {
                slozena_naredba(root);
                break;
            }
            case "<lista_naredbi>": {
                lista_naredbi(root);
                break;
            }
            case "<naredba>": {
                naredba(root);
                break;
            }
            case "<izraz_naredba>": {
                izraz_naredba(root);
                break;
            }
            case "<naredba_grananja>": {
                naredba_grananja(root);
                break;
            }
            case "<naredba_petlje>": {
                naredba_petlje(root);
                break;
            }
            case "<naredba_skoka>": {
                naredba_skoka(root);
                break;
            }
            case "<prijevodna_jedinica>": {
                prijevodna_jedinica(root);
                break;
            }
            case "<vanjska_deklaracija>": {
                vanjska_deklaracija(root);
                break;
            }
            case "<definicija_funkcije>": {
                definicija_funkcije(root);
                break;
            }
            case "<lista_parametara>": {
                lista_parametara(root);
                break;
            }
            case "<deklaracija_parametra>": {
                deklaracija_parametra(root);
                break;
            }
            case "<lista_deklaracija>": {
                lista_deklaracija(root);
                break;
            }
            case "<deklaracija>": {
                deklaracija(root);
                break;
            }
            case "<lista_init_deklaratora>": {
                lista_init_deklaratora(root);
                break;
            }
            case "<init_deklarator>": {
                init_deklarator(root);
                break;
            }
            case "<izravni_deklarator>": {
                izravni_deklarator(root);
                break;
            }
            case "<inicijalizator>": {
                inicijalizator(root);
                break;
            }
            case "<lista_izraza_pridruzivanja>": {
                lista_izraza_pridruzivanja(root);
                break;
            }
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
    }

    private void naredba_skoka(Node root) {
    }

    private void naredba_petlje(Node root) {
    }

    private void vanjska_deklaracija(Node root) {
    }

    private void prijevodna_jedinica(Node root) {
    }

    private void naredba_grananja(Node root) {
    }

    private void izraz_naredba(Node root) {
    }

    private void naredba(Node root) {
    }

    private void lista_naredbi(Node root) {
    }

    private void slozena_naredba(Node root) {
    }

    private void izraz(Node root) {
    }

    private void log_ili_izraz(Node root) {
    }

    private void log_i_izraz(Node root) {
    }

    private void bin_ili_izraz(Node root) {
    }

    private void bin_xili_izraz(Node root) {
    }

    private void izraz_pridruzivanja(Node root) {
    }

    private void bin_i_izraz(Node root) {
    }

    private void jednakosni_izraz(Node root) {
    }

    private void odnosni_izraz(Node root) {
    }

    private void aditivni_izraz(Node root) {
    }

    private void multiplikativni_izraz(Node root) {
    }

    private void specifikator_tipa(Node root) {
    }

    /**
     * Nezavršni znak <ime_tipa> generira imena opcionalno const-kvalificiranih brojevnih ti-
     * pova i ključnu riječ void. U ovim produkcijama će se izračunati izvedeno svojstvo tip
     * koje se koristi u produkcijama gdje se <ime_tipa> pojavljuje s desne strane i dodatno će
     * se onemogućiti tip const void (koji je sintaksno ispravan, ali nema smisla).
     */
    private void ime_tipa(Node root) {
//       <ime_tipa> ::= <specifikator_tipa>
//                | KR_CONST <specifikator_tipa>


    }

    /**
     * Nezavršni znak <cast_izraz> generira izraze s opcionalnim cast operatorom.
     */
    private void cast_izraz(Node root) {
//      <cast_izraz> ::= <unarni_izraz>
//                | L_ZAGRADA <ime_tipa> D_ZAGRADA <cast_izraz>

        if (root.getChildren().size() == 1) {
//            tip ← <unarni_izraz>.tip
//            l-izraz ← <unarni_izraz>.l-izraz
//            1. provjeri (<unarni_izraz>)

        } else {
//            tip ← <ime_tipa>.tip
//            l-izraz ← 0
//            1. provjeri (<ime_tipa>)
//            2. provjeri (<cast_izraz>)
//            3. <cast_izraz>.tip se može pretvoriti u <ime_tipa>.tip po poglavlju 4.3.1

        }
    }

    /**
     * Nezavršni znak <unarni_operator> generira aritmetičke (PLUS i MINUS), bitovne (OP_TILDA)
     * i logičke (OP_NEG) prefiks unarne operatore. Kako u ovim produkcijama u semantičkoj
     * analizi ne treba ništa provjeriti, produkcije ovdje nisu navedene.
     */
    private void unarni_operator(Node root) {
//        <unarni_operator> ::= PLUS
//                | MINUS
//                | OP_TILDA
//                | OP_NEG
    }

    /**
     * Nezavršni znak <unarni_izraz> generira izraze s opcionalnim prefiks unarnim operatorima.
     */
    private void unarni_izraz(Node root) {
//      <unarni_izraz> ::= <postfiks_izraz>
//                | OP_INC <unarni_izraz>
//                | OP_DEC <unarni_izraz>
//                | <unarni_operator> <cast_izraz>

        if (root.getChildren().size() == 1) {
//            tip ← <postfiks_izraz>.tip
//            l-izraz ← <postfiks_izraz>.l-izraz
//            1. provjeri (<postfiks_izraz>)

        } else if (root.getChildren().get(0).getData().equals("OP_INC")) {
//            tip ← int
//            l-izraz ← 0
//            1. provjeri (<unarni_izraz>)
//            2. <unarni_izraz>.l-izraz = 1 i <unarni_izraz>.tip ∼ int

        } else if (root.getChildren().get(0).getData().equals("OP_DEC")) {
//            isto kao INC

        } else {
//            tip ← int
//            l-izraz ← 0
//            1. provjeri (<cast_izraz>)
//            2. <cast_izraz>.tip ∼ int
        }
    }

    /**
     * Nezavršni znak <lista_argumenata> generira listu argumenata za poziv funkcije, a za
     * razliku od nezavršnih znakova koji generiraju izraze, imat će svojsto tipovi koje predstavlja
     * listu tipova argumenata, s lijeva na desno.
     */
    private void lista_argumenata(Node root) {
//        <lista_argumenata> ::= <izraz_pridruzivanja>
//                | <lista_argumenata> ZAREZ <izraz_pridruzivanja>

        if (root.getChildren().size() == 1) {
//            tipovi <- [<izraz_pridruzivanja>.tip]
//            1. provjeri(<izraz_pridruzivanja>)

        } else {
//            tipovi ← <lista_argumenata>.tipovi + [<izraz_pridruzivanja>.tip]
//            1. provjeri (<lista_argumenata>)
//            2. provjeri (<izraz_pridruzivanja>)
        }

    }

    /**
     * Nezavršni znak <postfiks_izraz> generira neki primarni izraz s opcionalnim postfiks-
     * operatorima.
     */
    private void postfiks_izraz(Node root) {
//        <postfiks_izraz> ::= <primarni_izraz>
//                | <postfiks_izraz> L_UGL_ZAGRADA <izraz> D_UGL_ZAGRADA
//                | <postfiks_izraz> L_ZAGRADA D_ZAGRADA
//                | <postfiks_izraz> L_ZAGRADA <lista_argumenata> D_ZAGRADA
//                | <postfiks_izraz> OP_INC
//                | <postfiks_izraz> OP_DEC


        if (root.getChildren().get(0).getData().equals("<primarni_izraz>")) {
//            tip <- <primarni_izraz>.tip
//            l-izraz <- <primarni_izraz>.l-izraz
//            1. provjeri(<primarni_izraz>)

        } else if (root.getChildren().size() == 4 && root.getChildren().get(1).getData().equals("L_UGL_ZAGRADA")) {
//            tip <- X
//            l-izraz <- X != const(T)
//            1. provjeri (<postfiks_izraz>)
//            2. <postfiks_izraz>.tip = niz (X)
//            3. provjeri (<izraz>)
//            4. <izraz>.tip ∼ int

        } else if (root.getChildren().size() == 3 && root.getChildren().get(2).getData().equals("D_ZAGRADA")) {
//            tip ← pov
//            l-izraz ← 0
//            1. provjeri (<postfiks_izraz>)
//            2. <postfiks_izraz>.tip = funkcija(void → pov)

        } else if (root.getChildren().size() == 4 && root.getChildren().get(2).getData().equals("<lista_argumenata>")) {
//            tip ← pov
//            l-izraz ← 0
//            1. provjeri (<postfiks_izraz>)
//            2. provjeri (<lista_argumenata>)
//            3. <postfiks_izraz>.tip = funkcija(params → pov ) i redom po elementima
//            arg-tip iz <lista_argumenata>.tipovi i param-tip iz params vrijedi arg-tip ~ param-tip

        } else if (root.getChildren().size() == 2 && root.getChildren().get(1).getData().equals("OP_INC")) {
//            tip ← int
//            l-izraz ← 0
//            1. provjeri (<postfiks_izraz>)
//            2. <postfiks_izraz>.l-izraz = 1 i <postfiks_izraz>.tip ∼ int

        } else { // <postfiks_izraz> OP_DEC

            // isto kao INC
        }

    }

    /**
     * Nezavršni znak <primarni_izraz> generira najjednostavnije izraze koji se sastoje od
     * jednog identifikatora, neke vrste konstante ili izraza u zagradi.
     */
    private void primarni_izraz(Node root) {
//                <primarni_izraz> ::= IDN
//                        | BROJ
//                        | ZNAK
//                        | NIZ_ZNAKOVA
//                        | L_ZAGRADA <izraz> D_ZAGRADA

        if (root.getChildren().size() == 3) {

            // provjeri(child1 = L_ZAGRADA)
            // provjeri(child3 = D_ZAGRADA)
            // provjeri(<izraz>)
            // tip <- <izraz>.tip
            // l-izraz <- <izraz>.l-izraz

        } else {
            Node child = root.getChildren().get(0);
            String childName = child.getData();

            switch (childName) {
                case "IDN": {
//                        tip ← IDN.tip
//                        l-izraz ← IDN.l-izraz
//                        1. IDN.ime je deklarirano
                    break;
                }
                case "BROJ": {
//                        tip ← int
//                        l-izraz ← 0
//                        1. vrijednost je u rasponu tipa int
                    break;
                }
                case "ZNAK": {
//                        tip ← char
//                        l-izraz ← 0
//                        1. znak je ispravan po 4.3.2
                    break;
                }
                case "NIZ_ZNAKOVA": {
//                        tip ← niz (const(char))
//                        l-izraz ← 0
//                        1. konstantni niz znakova je ispravan po 4.3.2
                    break;
                }
            }


        }
    }

}
