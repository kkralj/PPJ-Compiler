import java.util.List;

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

        // popuni sadržaj funkcije

//        for (Node child : root.getChildren()) {
//            fillNodes(child);
//        }

        /* ispis produkcija, za debugging... */

        String productionName = root.line + " ::= ";
        System.out.println("Obilazim:  " + productionName);

//        for (Node child : root.getChildren()) {
//            productionName += child.getData() + " ";
//        }
//        productionName = productionName.trim();

        completeActions(root);

        if (assignedContext) { // izlazimo iz konteksta pošto je produkcija gotova
            this.context = this.context.getParent();
        }
    }

    private boolean checkContext(Node root) { // TODO: ovo treba nadopuniti

        String rootName = root.line;

        if (rootName.equals("<slozena naredba>") || rootName.equals("<vanjska_deklaracija>")) {
            this.context = new Context(this.context); // change current context to new one
            return true;
        }

        return false;
    }

    private void primarni_izraz(Node root) {
//                <primarni_izraz> ::= IDN
//                        | BROJ
//                        | ZNAK
//                        | NIZ_ZNAKOVA
//                        | L_ZAGRADA <izraz> D_ZAGRADA

        if (root.getChildren().size() == 3) {
            Node child = root.getChildren().get(1);
            izraz(child);

            root.type = child.type;
            root.lSide = child.lSide;

        } else {

            Node child = root.getChildren().get(0);

            if (child.line.startsWith("IDN")) {

                Node contextChild = context.findVariable(child);
                if (contextChild != null) {
                    root.type = contextChild.type;
                    root.lSide = contextChild.lSide;
                    root.storeData(contextChild);

                } else {
                    throw new IllegalArgumentException("Not declared!");
                }

            } else if (child.line.startsWith("BROJ")) {

                if (child.isValidNumber()) {
                    root.type = "int";
                    root.lSide = false;
                } else {
                    throw new IllegalArgumentException("Number not in range!");
                }

            } else if (child.line.startsWith("ZNAK")) {

                if (child.isValidChar()) {
                    // znak je ispravan po 4.3.2
                    root.type = "char";
                    root.lSide = false;
                }

            } else if (child.line.startsWith("NIZ_ZNAKOVA")) {

                if (child.isValidCharArray()) {
                    // konstantni niz znakova je ispravan po 4.3.2
                    root.type = "niz-char";
                    root.lSide = false;
                }

            } else {
                throw new IllegalArgumentException("nepoznat oblik");
            }
        }
    }

    private void postfiks_izraz(Node root) {
//        <postfiks_izraz> ::= <primarni_izraz>
//                | <postfiks_izraz> L_UGL_ZAGRADA <izraz> D_UGL_ZAGRADA
//                | <postfiks_izraz> L_ZAGRADA D_ZAGRADA
//                | <postfiks_izraz> L_ZAGRADA <lista_argumenata> D_ZAGRADA
//                | <postfiks_izraz> OP_INC
//                | <postfiks_izraz> OP_DEC

        if (root.getChildren().get(0).line.equals("<primarni_izraz>")) {
            Node primarniIzrazNode = root.getChildren().get(0);

            primarni_izraz(primarniIzrazNode);

            root.type = primarniIzrazNode.type;
            root.lSide = primarniIzrazNode.lSide;
            root.storeData(primarniIzrazNode);

        } else if (root.getChildren().size() == 4 && root.getChildren().get(2).line.equals("<izraz>")) {
            Node postfiksIzrazNode = root.getChildren().get(0);
            postfiks_izraz(postfiksIzrazNode);
            if (!postfiksIzrazNode.isArray) {
                throw new IllegalArgumentException("Krivi postfiks_izraz");
            }

            Node izrazNode = root.getChildren().get(2);
            izraz(izrazNode);
            if (!implicitInt(izrazNode.getType())) {
                throw new IllegalArgumentException("Krivi postfiks_izraz");
            }

            root.type = postfiksIzrazNode.type;
            root.lSide = !postfiksIzrazNode.isConstant;

        } else if (root.getChildren().size() == 3) {
            Node postfiksIzrazNode = root.getChildren().get(0);
            postfiks_izraz(postfiksIzrazNode);

            if (postfiksIzrazNode.isFunction && postfiksIzrazNode.getChildren().get(2).getType().equals("void")) {
                root.type = postfiksIzrazNode.type;
                root.lSide = false;

            } else {
                throw new IllegalArgumentException("Krivi postfiks_izraz");
            }

        } else if (root.getChildren().size() == 4 && root.getChildren().get(2).line.equals("<lista_argumenata>")) {
            Node postfiksIzrazNode = root.getChildren().get(0);
            Node listaArgumenataNode = root.getChildren().get(2);

            postfiks_izraz(postfiksIzrazNode);
            lista_argumenata(listaArgumenataNode);

            if (!postfiksIzrazNode.isFunction || !listaArgumenataNode.isArray) {
                throw new IllegalArgumentException();
            }

            if (listaArgumenataNode.argumentTypes.size() != postfiksIzrazNode.getChildren().get(2).argumentTypes.size()) {
                throw new IllegalArgumentException();
            }

            List<String> listArguments = listaArgumenataNode.argumentTypes;
            List<String> functionArguments = postfiksIzrazNode.getChildren().get(2).argumentTypes;
            for (int i = 0; i < listArguments.size(); i++) {
                if (!implicitTypes(listArguments.get(i), functionArguments.get(i))) {
                    throw new IllegalArgumentException();
                }
            }

            root.type = postfiksIzrazNode.type;
            root.lSide = false;

        } else if (root.getChildren().size() == 2) {
            Node postfiksIzrazNode = root.getChildren().get(0);

            postfiks_izraz(postfiksIzrazNode);

            if (postfiksIzrazNode.lSide && implicitInt(postfiksIzrazNode.type)) {
                root.type = "int";
                root.lSide = false;
            } else {
                throw new IllegalArgumentException("Krivi postfiks_izraz");
            }

        }

    }

    private void unarni_izraz(Node root) {
//      <unarni_izraz> ::= <postfiks_izraz>
//                | OP_INC <unarni_izraz>
//                | OP_DEC <unarni_izraz>
//                | <unarni_operator> <cast_izraz>

        if (root.getChildren().size() == 1) {
            Node postfiksIzrazNode = root.getChildren().get(0);

            postfiks_izraz(postfiksIzrazNode);

            root.type = postfiksIzrazNode.type;
            root.lSide = postfiksIzrazNode.lSide;

            root.storeData(postfiksIzrazNode);

        } else if (root.getChildren().get(1).line.equals("<unarni_izraz>")) {
            Node unarniIzrazNode = root.getChildren().get(1);

            unarni_izraz(unarniIzrazNode);

            if (unarniIzrazNode.lSide && implicitInt(unarniIzrazNode.getType())) {
                root.type = "int";
                root.lSide = false;
            } else {
                throw new IllegalArgumentException("Unarni izraz nije ispravan.");
            }

        } else {
            Node castIzrazNode = root.getChildren().get(1);

            cast_izraz(castIzrazNode);

            if (implicitInt(castIzrazNode.type)) {
                root.setType("int");
                root.lSide = false;
            } else {
                throw new IllegalArgumentException("Unarni izraz nije ispravan.");
            }
        }
    }

    private void unarni_operator(Node root) {
//        <unarni_operator> ::= PLUS
//                | MINUS
//                | OP_TILDA
//                | OP_NEG

        // ne radi nista
    }

    private void cast_izraz(Node root) {
//      <cast_izraz> ::= <unarni_izraz>
//                | L_ZAGRADA <ime_tipa> D_ZAGRADA <cast_izraz>

        if (root.getChildren().size() == 1) {
            Node unarniIzrazNode = root.getChildren().get(0);
            unarni_izraz(unarniIzrazNode);

            root.setType(unarniIzrazNode.type);
            root.setlSide(unarniIzrazNode.lSide);

        } else {
            Node imeTipaNode = root.getChildren().get(1);
            Node castIzrazNode = root.getChildren().get(3);

            ime_tipa(imeTipaNode);
            cast_izraz(castIzrazNode);

            if (implicitTypes(castIzrazNode.type, imeTipaNode.type)) {
                root.type = imeTipaNode.type;
                root.lSide = false;

            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    private void ime_tipa(Node root) {
//       <ime_tipa> ::= <specifikator_tipa>
//                | KR_CONST <specifikator_tipa>

        if (root.getChildren().size() == 1) {
            Node specifikatorTipaNode = root.getChildren().get(0);
            specifikator_tipa(specifikatorTipaNode);

            root.type = specifikatorTipaNode.type;

        } else {
            Node specifikatorTipaNode = root.getChildren().get(1);
            specifikator_tipa(specifikatorTipaNode);

            if (!specifikatorTipaNode.type.equals("void")) {

                root.type = specifikatorTipaNode.type;
                root.isConstant = true;

            } else {
                throw new IllegalArgumentException("Parameter cannot be void");
            }
        }
    }

    private void specifikator_tipa(Node root) {
//        <specifikator_tipa> ::= KR_VOID
//                | KR_CHAR
//                | KR_INT

        String line = root.getChildren().get(0).line;

        if (line.startsWith("KR_VOID")) {
            root.setType("void");

        } else if (line.startsWith("KR_CHAR")) {
            root.setType("char");

        } else if (line.startsWith("KR_INT")) {
            root.setType("int");

        } else {
            throw new IllegalArgumentException("Krivi <specifikator_tipa>");
        }
    }

    private void multiplikativni_izraz(Node root) {
//        <multiplikativni_izraz> ::= <cast_izraz>
//                | <multiplikativni_izraz> OP_PUTA <cast_izraz>
//                | <multiplikativni_izraz> OP_DIJELI <cast_izraz>
//                | <multiplikativni_izraz> OP_MOD <cast_izraz>

        if (root.getChildren().size() == 1) {
            Node castIzrazNode = root.getChildren().get(0);
            cast_izraz(castIzrazNode);

            root.type = castIzrazNode.type;
            root.lSide = castIzrazNode.lSide;

            root.storeData(castIzrazNode);

        } else {
            Node multiplikativniIzrazNode = root.getChildren().get(0);
            Node castIzrazNode = root.getChildren().get(2);

            multiplikativni_izraz(multiplikativniIzrazNode);

            if (!implicitInt(multiplikativniIzrazNode.type)) {
                throw new IllegalArgumentException();
            }

            cast_izraz(castIzrazNode);

            if (!implicitInt(castIzrazNode.type)) {
                throw new IllegalArgumentException();
            }

            root.type = "int";
            root.lSide = false;
        }
    }

    private void aditivni_izraz(Node root) {
//        <aditivni_izraz> ::= <multiplikativni_izraz>
//                | <aditivni_izraz> PLUS <multiplikativni_izraz>
//                | <aditivni_izraz> MINUS <multiplikativni_izraz>

        if (root.getChildren().size() == 1) {
            Node multiplikativniIzrazNode = root.getChildren().get(0);
            multiplikativni_izraz(multiplikativniIzrazNode);

            root.type = multiplikativniIzrazNode.type;
            root.lSide = multiplikativniIzrazNode.lSide;

        } else {
            Node aditivniIzrazNode = root.getChildren().get(0);
            Node multiplikativniIzrazNode = root.getChildren().get(2);

            aditivni_izraz(aditivniIzrazNode);

            if (!implicitInt(aditivniIzrazNode.type)) {
                throw new IllegalArgumentException();
            }

            multiplikativni_izraz(multiplikativniIzrazNode);

            if (!implicitInt(multiplikativniIzrazNode.type)) {
                throw new IllegalArgumentException();
            }
            root.type = "int";
            root.lSide = false;
        }
    }

    private void odnosni_izraz(Node root) {
//        <odnosni_izraz> ::= <aditivni_izraz>
//                | <odnosni_izraz> OP_LT <aditivni_izraz>
//                | <odnosni_izraz> OP_GT <aditivni_izraz>
//                | <odnosni_izraz> OP_LTE <aditivni_izraz>
//                | <odnosni_izraz> OP_GTE <aditivni_izraz>

        if (root.getChildren().size() == 1) {
            Node aditivniIzrazNode = root.getChildren().get(0);
            aditivni_izraz(aditivniIzrazNode);

            root.type = aditivniIzrazNode.type;
            root.lSide = aditivniIzrazNode.lSide;

        } else {
            Node odnosniIzrazNode = root.getChildren().get(0);
            Node aditivniIzrazNode = root.getChildren().get(2);

            odnosni_izraz(odnosniIzrazNode);

            if (!implicitInt(odnosniIzrazNode.type)) {
                throw new IllegalArgumentException();
            }

            aditivni_izraz(aditivniIzrazNode);

            if (!implicitInt(aditivniIzrazNode.type)) {
                throw new IllegalArgumentException();
            }

            root.type = "int";
            root.lSide = false;
        }
    }

    private void jednakosni_izraz(Node root) {
//        <jednakosni_izraz> ::= <odnosni_izraz>
//                | <jednakosni_izraz> OP_EQ <odnosni_izraz>
//                | <jednakosni_izraz> OP_NEQ <odnosni_izraz>

        if (root.getChildren().size() == 1) {
            Node odnosniIzrazNode = root.getChildren().get(0);
            odnosni_izraz(odnosniIzrazNode);

            root.type = odnosniIzrazNode.type;
            root.lSide = odnosniIzrazNode.lSide;

        } else {
            Node jednakosniIzrazNode = root.getChildren().get(0);
            Node odnosniIzrazNode = root.getChildren().get(2);

            jednakosni_izraz(jednakosniIzrazNode);

            if (!implicitInt(jednakosniIzrazNode.type)) {
                throw new IllegalArgumentException();
            }

            odnosni_izraz(odnosniIzrazNode);

            if (!implicitInt(odnosniIzrazNode.type)) {
                throw new IllegalArgumentException();
            }

            root.type = "int";
            root.lSide = false;
        }

    }

    private void bin_i_izraz(Node root) {
//        <bin_i_izraz> ::= <jednakosni_izraz>
//                | <bin_i_izraz> OP_BIN_I <jednakosni_izraz>

        if (root.getChildren().size() == 1) {
            Node jednakosniIzrazNode = root.getChildren().get(0);

            jednakosni_izraz(jednakosniIzrazNode);

            root.type = jednakosniIzrazNode.type;
            root.lSide = jednakosniIzrazNode.lSide;

        } else {
            Node binIizrazNode = root.getChildren().get(0);
            Node jednakosniIzrazNode = root.getChildren().get(2);

            bin_i_izraz(binIizrazNode);

            if (!implicitInt(binIizrazNode.type)) {
                throw new IllegalArgumentException();
            }

            jednakosni_izraz(jednakosniIzrazNode);

            if (!implicitInt(jednakosniIzrazNode.type)) {
                throw new IllegalArgumentException();
            }

            root.type = "int";
            root.lSide = false;
        }
    }

    private void bin_xili_izraz(Node root) {
//        <bin_xili_izraz> ::= <bin_i_izraz>
//                | <bin_xili_izraz> OP_BIN_XILI <bin_i_izraz>

        if (root.getChildren().size() == 1) {
            Node binIizrazNode = root.getChildren().get(0);

            bin_i_izraz(binIizrazNode);

            root.type = binIizrazNode.type;
            root.lSide = binIizrazNode.lSide;

        } else {
            Node binXIliIzrazNode = root.getChildren().get(0);
            Node binIizrazNode = root.getChildren().get(2);

            bin_xili_izraz(binXIliIzrazNode);

            if (!implicitInt(binXIliIzrazNode.type)) {
                throw new IllegalArgumentException();
            }

            bin_i_izraz(binIizrazNode);

            if (!implicitInt(binIizrazNode.type)) {
                throw new IllegalArgumentException();
            }

            root.type = "int";
            root.lSide = false;
        }
    }

    private void bin_ili_izraz(Node root) {
//        <bin_ili_izraz> ::= <bin_xili_izraz>
//                | <bin_ili_izraz> OP_BIN_ILI <bin_xili_izraz>

        if (root.getChildren().size() == 1) {
            Node binXiliIzrazNode = root.getChildren().get(0);
            bin_xili_izraz(binXiliIzrazNode);

            root.type = binXiliIzrazNode.type;
            root.lSide = binXiliIzrazNode.lSide;

        } else {
            Node binIliIzrazNode = root.getChildren().get(0);
            Node binXiliIzrazNode = root.getChildren().get(2);

            bin_ili_izraz(binIliIzrazNode);

            if (!implicitInt(binIliIzrazNode.type)) {
                throw new IllegalArgumentException();
            }

            bin_xili_izraz(binXiliIzrazNode);

            if (!implicitInt(binXiliIzrazNode.type)) {
                throw new IllegalArgumentException();
            }

            root.type = "int";
            root.lSide = false;
        }
    }

    private void log_i_izraz(Node root) {
//        <log_i_izraz> ::= <bin_ili_izraz>
//                | <log_i_izraz> OP_I <bin_ili_izraz>

        if (root.getChildren().size() == 1) {
            Node binIliIzrazNode = root.getChildren().get(0);

            bin_ili_izraz(binIliIzrazNode);

            root.type = binIliIzrazNode.type;
            root.lSide = binIliIzrazNode.lSide;

        } else {
            Node logIIzrazNode = root.getChildren().get(0);
            Node binIliIzrazNode = root.getChildren().get(2);

            log_i_izraz(logIIzrazNode);

            if (!implicitInt(logIIzrazNode.type)) {
                throw new IllegalArgumentException();
            }

            bin_ili_izraz(binIliIzrazNode);

            if (!implicitInt(binIliIzrazNode.type)) {
                throw new IllegalArgumentException();
            }

            root.type = "int";
            root.lSide = false;
        }
    }

    private void log_ili_izraz(Node root) {
//        <log_ili_izraz> ::= <log_i_izraz>
//                | <log_ili_izraz> OP_ILI <log_i_izraz>

        if (root.getChildren().size() == 1) {
            Node logIIzrazNode = root.getChildren().get(0);
            log_i_izraz(logIIzrazNode);

            root.type = logIIzrazNode.type;
            root.lSide = logIIzrazNode.lSide;

        } else {
            Node logIliIzrazNode = root.getChildren().get(2);
            Node logIIzrazNode = root.getChildren().get(0);

            log_ili_izraz(logIliIzrazNode);

            if (!implicitInt(logIliIzrazNode.type)) {
                throw new IllegalArgumentException();
            }

            log_i_izraz(logIIzrazNode);

            if (!implicitInt(logIIzrazNode.type)) {
                throw new IllegalArgumentException();
            }

            root.type = "int";
            root.lSide = false;
        }
    }

    private void izraz_pridruzivanja(Node root) {
//        <izraz_pridruzivanja> ::= <log_ili_izraz>
//                | <postfiks_izraz> OP_PRIDRUZI <izraz_pridruzivanja>

        if (root.getChildren().size() == 1) {
            Node logIliIzrazNode = root.getChildren().get(0);

            log_ili_izraz(logIliIzrazNode);

            root.type = logIliIzrazNode.type;
            root.lSide = logIliIzrazNode.lSide;

        } else {
            Node postfiksIzrazNode = root.getChildren().get(0);
            Node izrazPridruzivanjaNode = root.getChildren().get(2);

            postfiks_izraz(postfiksIzrazNode);

            if (!postfiksIzrazNode.lSide) {
                throw new IllegalArgumentException();
            }

            izraz_pridruzivanja(izrazPridruzivanjaNode);

            if (!implicitTypes(izrazPridruzivanjaNode.type, postfiksIzrazNode.type)) {
                throw new IllegalArgumentException();
            }
        }
    }

    private void izraz(Node root) {
//        <izraz> ::= <izraz_pridruzivanja>
//                | <izraz> ZAREZ <izraz_pridruzivanja>

        if (root.getChildren().size() == 1) {
            Node izrazPridruzivanjaNode = root.getChildren().get(0);

            izraz_pridruzivanja(izrazPridruzivanjaNode);

            root.type = izrazPridruzivanjaNode.type;
            root.lSide = izrazPridruzivanjaNode.lSide;

        } else {
            Node izrazNode = root.getChildren().get(0);
            Node izrazPridruzivanjaNode = root.getChildren().get(2);

            izraz(izrazNode);

            izraz_pridruzivanja(izrazPridruzivanjaNode);

            root.type = izrazPridruzivanjaNode.type;
            root.lSide = false;
        }
    }

    private void slozena_naredba(Node root) {
//       <slozena_naredba> ::= L_VIT_ZAGRADA <lista_naredbi> D_VIT_ZAGRADA
//      	    | L_VIT_ZAGRADA <lista_deklaracija> <lista_naredbi> D_VIT_ZAGRADA

        if (root.getChildren().size() == 3) {
            Node listaNaredbiNode = root.getChildren().get(1);
            lista_naredbi(listaNaredbiNode);

        } else {
            Node listaDeklaracijaNode = root.getChildren().get(1);
            Node listaNaredbiNode = root.getChildren().get(2);

            lista_deklaracija(listaDeklaracijaNode);
            lista_naredbi(listaNaredbiNode);
        }

    }

    private void lista_naredbi(Node root) {
//        <lista_naredbi> ::= <naredba>
//                | <lista_naredbi> <naredba>

        if (root.getChildren().size() == 1) {
            Node naredbaNode = root.getChildren().get(0);
            naredba(naredbaNode);

        } else {
            Node listaNaredbiNode = root.getChildren().get(0);
            Node naredbaNode = root.getChildren().get(1);

            lista_naredbi(listaNaredbiNode);

            naredba(naredbaNode);
        }
    }

    private void naredba(Node root) {
//        <naredba> ::= <slozena_naredba>
//                | <izraz_naredba>
//                | <naredba_grananja>
//                | <naredba_petlje>
//                | <naredba_skoka>

        Node firstChild = root.getChildren().get(0);

        switch (firstChild.line) {
            case "<slozena_naredba>": {
                slozena_naredba(firstChild);
                break;
            }
            case "<izraz_naredba>": {
                izraz_naredba(firstChild);
                break;
            }
            case "<naredba_grananja>": {
                naredba_grananja(firstChild);
                break;
            }
            case "<naredba_petlje>": {
                naredba_petlje(firstChild);
                break;
            }
            case "<naredba_skoka>": {
                naredba_skoka(firstChild);
                break;
            }
        }
    }

    private void izraz_naredba(Node root) {
//        <izraz_naredba> ::= TOCKAZAREZ
//                | <izraz> TOCKAZAREZ

        if (root.getChildren().size() == 1) {
            root.setType("int");

        } else {
            Node izrazNode = root.getChildren().get(0);
            izraz(izrazNode);

            root.type = izrazNode.type;
        }
    }

    private void naredba_grananja(Node root) {
//        <naredba_grananja> ::= KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba>
//                | KR_IF L_ZAGRADA <izraz> D_ZAGRADA <naredba> KR_ELSE <naredba>


        if (root.getChildren().size() == 5) {
            Node izrazNode = root.getChildren().get(2);
            Node naredbaNode = root.getChildren().get(4);

            izraz(izrazNode);

            if (!implicitInt(izrazNode.type)) {
                throw new IllegalArgumentException();
            }

            naredba(naredbaNode);

        } else {
            Node izrazNode = root.getChildren().get(2);
            Node firstNaredbaNode = root.getChildren().get(4);
            Node secondNaredbaNode = root.getChildren().get(6);

            izraz(izrazNode);

            if (!implicitInt(izrazNode.type)) {
                throw new IllegalArgumentException();
            }

            naredba(firstNaredbaNode);

            naredba(secondNaredbaNode);
        }
    }

    private void naredba_petlje(Node root) {
//        <naredba_petlje> ::= KR_WHILE L_ZAGRADA <izraz> D_ZAGRADA <naredba>
//                | KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> D_ZAGRADA <naredba>
//                | KR_FOR L_ZAGRADA <izraz_naredba> <izraz_naredba> <izraz> D_ZAGRADA <naredba>

        if (root.getChildren().size() == 5) {
            Node izrazNode = root.getChildren().get(2);
            Node naredbaNode = root.getChildren().get(4);

            izraz(izrazNode);

            if (!implicitInt(izrazNode.type)) {
                throw new IllegalArgumentException("Greska u naredbi grananja.");
            }

            naredba(naredbaNode);

        } else if (root.getChildren().size() == 6) {
            Node firstIzrazNaredbaNode = root.getChildren().get(2);
            Node secondIzrazNaredbaNode = root.getChildren().get(3);
            Node naredbaNode = root.getChildren().get(5);

            izraz_naredba(firstIzrazNaredbaNode);
            izraz_naredba(secondIzrazNaredbaNode);

            if (!implicitInt(secondIzrazNaredbaNode.type)) {
                throw new IllegalArgumentException();
            }

            naredba(naredbaNode);

        } else {
            Node firstIzrazNaredbaNode = root.getChildren().get(2);
            Node secondIzrazNaredbaNode = root.getChildren().get(3);
            Node izrazNode = root.getChildren().get(4);
            Node naredbaNode = root.getChildren().get(6);

            izraz_naredba(firstIzrazNaredbaNode);

            izraz_naredba(secondIzrazNaredbaNode);

            if (!implicitInt(secondIzrazNaredbaNode.type)) {
                throw new IllegalArgumentException();
            }

            izraz(izrazNode);

            naredba(naredbaNode);
        }
    }

    // OVO FALI
    private void naredba_skoka(Node root) {
//        <naredba_skoka> ::= KR_CONTINUE TOCKAZAREZ
//               	| KR_BREAK TOCKAZAREZ
//              	| KR_RETURN TOCKAZAREZ
//	                | KR_RETURN <izraz> TOCKAZAREZ

        if (root.getChildren().size() == 3 && root.getChildren().get(1).line.equals("<izraz>")) {
//            1. provjeri (<izraz>)
//            2. naredba se nalazi unutar funkcije tipa funkcija(params → pov ) i vrijedi <izraz>.tip ∼ pov

        }
    }

    private void prijevodna_jedinica(Node root) {
//        <prijevodna_jedinica> ::= <vanjska_deklaracija>
//                | <prijevodna_jedinica> <vanjska_deklaracija>

        if (root.getChildren().size() == 1) {
            Node vanjskaDeklaracijaNode = root.getChildren().get(0);
            vanjska_deklaracija(vanjskaDeklaracijaNode);

        } else {
            Node prijevodnaJedinicaNode = root.getChildren().get(0);
            Node vanjskaDeklaracijaNode = root.getChildren().get(1);

            prijevodna_jedinica(prijevodnaJedinicaNode);

            vanjska_deklaracija(vanjskaDeklaracijaNode);
        }
    }

    private void vanjska_deklaracija(Node root) {
//        <vanjska_deklaracija> ::= <definicija_funkcije>
//                | <deklaracija>

        Node firstChild = root.getChildren().get(0);

        if (firstChild.line.equals("<definicija_funkcije>")) {
            definicija_funkcije(firstChild);

        } else {
            deklaracija(firstChild);
        }
    }

    // OVO ISTO FALI
    private void definicija_funkcije(Node root) {
//        <definicija_funkcije> ::= <ime_tipa> IDN L_ZAGRADA KR_VOID D_ZAGRADA <slozena_naredba>
//	                              | <ime_tipa> IDN L_ZAGRADA <lista_parametara> D_ZAGRADA <slozena_naredba>

        if (root.getChildren().get(3).line.startsWith("KR_VOID")) {
            Node imeTipaNode = root.getChildren().get(0);
            Node slozenaNaredbaNode = root.getChildren().get(5);

            ime_tipa(imeTipaNode);

            if (imeTipaNode.isConstant) {
                throw new IllegalArgumentException();
            }


        } else {

        }
    }

    private void lista_parametara(Node root) {
//        <lista_parametara> ::= <deklaracija_parametra>
//                | <lista_parametara> ZAREZ <deklaracija_parametra>

        if (root.getChildren().size() == 1) {
            Node deklaracijaParametraNode = root.getChildren().get(0);

            deklaracija_parametra(deklaracijaParametraNode);

            root.setArgumentNames(deklaracijaParametraNode.argumentNames.get(0));

            root.setArgumentTypes(deklaracijaParametraNode.argumentTypes.get(0));

        } else {
            Node listaParametaraNode = root.getChildren().get(0);
            Node deklaracijaParametraNode = root.getChildren().get(2);

            lista_parametara(listaParametaraNode);

            deklaracija_parametra(deklaracijaParametraNode);

            for (int i = 0; i < listaParametaraNode.argumentNames.size(); i++) {
                // ne smiju biti 2 ista naziva argumenta
                if (listaParametaraNode.argumentNames.get(i).equals(deklaracijaParametraNode.argumentNames.get(0))) {
                    throw new IllegalArgumentException();
                }
            }

            root.argumentNames = listaParametaraNode.argumentNames;
            root.argumentNames.add(deklaracijaParametraNode.argumentNames.get(0));

            root.argumentTypes = listaParametaraNode.argumentTypes;
            root.argumentTypes.add(deklaracijaParametraNode.argumentTypes.get(0));
        }
    }

    private void deklaracija_parametra(Node root) {
//      <deklaracija_parametra> ::= <ime_tipa> IDN
//                | <ime_tipa> IDN L_UGL_ZAGRADA D_UGL_ZAGRADA

        if (root.getChildren().size() == 2) {
            Node imeTipaNode = root.getChildren().get(0);
            Node idnNode = root.getChildren().get(1);

            ime_tipa(imeTipaNode);

            if (imeTipaNode.type.equals("void")) {
                throw new IllegalArgumentException();
            }

            root.type = imeTipaNode.type;
            root.variableName = idnNode.variableName;

        } else {
            Node imeTipaNode = root.getChildren().get(0);
            Node idnNode = root.getChildren().get(1);

            ime_tipa(imeTipaNode);

            if (imeTipaNode.type.equals("void")) {
                throw new IllegalArgumentException();
            }

            root.type = imeTipaNode.type;
            root.variableName = idnNode.variableName;
            root.isArray = true;

        }
    }

    private void lista_deklaracija(Node root) {
//        <lista_deklaracija> ::= <deklaracija>
//                | <lista_deklaracija> <deklaracija>

        if (root.getChildren().size() == 1) {
            Node deklaracijaNode = root.getChildren().get(0);
            deklaracija(deklaracijaNode);

        } else {
            Node listaDeklaracijaNode = root.getChildren().get(0);
            Node deklaracijaNode = root.getChildren().get(1);

            lista_deklaracija(listaDeklaracijaNode);
            deklaracija(deklaracijaNode);
        }
    }

    private void deklaracija(Node root) {
//        <deklaracija> ::= <ime_tipa> <lista_init_deklaratora> TOCKAZAREZ

        Node imeTipaNode = root.getChildren().get(0);
        Node listaInitDeklaratoraNode = root.getChildren().get(1);

        ime_tipa(imeTipaNode);

        listaInitDeklaratoraNode.ntip = imeTipaNode.ntip;

        lista_init_deklaratora(listaInitDeklaratoraNode);
    }

    private void lista_init_deklaratora(Node root) {
//        <lista_init_deklaratora> ::= <init_deklarator>
//                | <lista_init_deklaratora> ZAREZ <init_deklarator>

        if (root.getChildren().size() == 1) {
            Node initDeklaratorNode = root.getChildren().get(0);

            initDeklaratorNode.ntip = root.ntip;
            init_deklarator(initDeklaratorNode);

        } else {
            Node listaInitDeklaratoraNode = root.getChildren().get(0);
            Node initDeklaratorNode = root.getChildren().get(2);

            listaInitDeklaratoraNode.ntip = root.ntip;
            lista_init_deklaratora(listaInitDeklaratoraNode);

            initDeklaratorNode.ntip = root.ntip;
            init_deklarator(initDeklaratorNode);
        }
    }

    // DOVRŠI UVJETE
    private void init_deklarator(Node root) {
//        <init_deklarator> ::= <izravni_deklarator>
//                | <izravni_deklarator> OP_PRIDRUZI <inicijalizator>

        if (root.getChildren().size() == 1) {
            Node izravniDeklaratorNode = root.getChildren().get(0);

            izravniDeklaratorNode.ntip = root.ntip;
            izravni_deklarator(izravniDeklaratorNode);

            if (izravniDeklaratorNode.isConstant) {
                throw new IllegalArgumentException();
            }

        } else {
            Node izravniDeklaratorNode = root.getChildren().get(0);
            Node inicijalizatorNode = root.getChildren().get(2);

            izravniDeklaratorNode.ntip = root.ntip;
            izravni_deklarator(izravniDeklaratorNode);

            inicijalizator(inicijalizatorNode);

            if (!izravniDeklaratorNode.isArray && implicitTypes(inicijalizatorNode.type, inicijalizatorNode.type)) {

            } else if (inicijalizatorNode.argumentTypes.size() <= izravniDeklaratorNode.argumentTypes.size()) {

            } else {
                throw new IllegalArgumentException();
            }

        }

    }

    
    private void inicijalizator(Node root) {
    }

    private void lista_argumenata(Node root) {
//        <lista_argumenata> ::= <izraz_pridruzivanja>
//                | <lista_argumenata> ZAREZ <izraz_pridruzivanja>

        if (root.getChildren().size() == 1) {
            Node izrazPridruzivanjaNode = root.getChildren().get(0);

            izraz_pridruzivanja(izrazPridruzivanjaNode);

            izrazPridruzivanjaNode.argumentTypes.add(izrazPridruzivanjaNode.type);

        } else {
            Node listaArgumenataNode = root.getChildren().get(0);
            Node izrazPridruzivanjaNode = root.getChildren().get(2);

            lista_argumenata(listaArgumenataNode);
            izraz_pridruzivanja(izrazPridruzivanjaNode);

            root.argumentTypes = listaArgumenataNode.argumentTypes;
            root.argumentTypes.add(izrazPridruzivanjaNode.type);
        }

    }

    private void completeActions(Node root) {
        if (root.getChildren().isEmpty()) { // leaf
            root.setType(root.line);
            return;
        }

        String nodeName = root.line;

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


    private void izravni_deklarator(Node root) {
    }


    private void lista_izraza_pridruzivanja(Node root) {
    }


    private boolean implicitInt(String type) {
        return type.equals("int") || type.equals("char");
    }

    private boolean implicitTypes(String t1, String t2) {
        if (t1.equals(t2)) {
            return true;
        }

        if (t1.equals("char") && t2.equals("int")) {
            return true;
        }

        return false;
    }

}
