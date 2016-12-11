public class SemanticAnalyzer {

    private SyntaxTree syntaxTree;

    public SemanticAnalyzer(SyntaxTree syntaxTree) {
        this.syntaxTree = syntaxTree;
    }

    public void analyze() {
        fillNodes(syntaxTree.getRoot());
    }

    private void fillNodes(Node root) {
        for (Node child : root.getChildren()) {
            fillNodes(child);
        }


//        String productionName = root.getData() + " ::= ";
//        for (Node child : root.getChildren()) {
//            productionName += child.getData() + " ";
//        }
//        productionName = productionName.trim();
//        System.out.println(productionName);


        if (root.getChildren().isEmpty()) { // leaf with no children
            root.setType(root.getData());
            root.setLeftOK(false);

        } else if (root.getData().equals("<specifikator_tipa>")) {
            root.setType(root.getChildren().get(0).getType());

        } else if (root.getData().equals("<ime_tipa>")) {
            

        }

    }

    private void completeAction(String productionName, Node root) {

    }


}
