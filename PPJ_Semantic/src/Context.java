import java.util.ArrayList;
import java.util.List;

public class Context {

    private List<Node> contextData = new ArrayList<>();

    private Context parent;

    public Context(Context parent) {
        this.parent = parent;
    }

//    public void declareVariable(Node node) {
//        if (variableExists(node)) {
//            throw new IllegalArgumentException("Variable already declared.");
//        }
//
//        variables.add(node);
//    }

    public Node findVariable(Node node) {
        Context current = this;

        while (current != null) {
            Node contextVariable = current.findCurrentContextVariable(node);
            if (contextVariable != null) {
                return contextVariable;
            }
            current = current.getParent();
        }

        return null;
    }

    private Node findCurrentContextVariable(Node node) {
        String[] varParams = node.line.split("\\s+");

        String nodeType = varParams[0];
//        Integer nodeLine = Integer.parseInt(varParams[1]);
        String nodeName = varParams[2];

        for (Node variable : contextData) {
            if (variable.variableName.equals(nodeName) && variable.isVariable && variable.variableType.equals(nodeType)) {
                return variable;
            }
        }
        return null;
    }

    public Context getParent() {
        return parent;
    }

}
