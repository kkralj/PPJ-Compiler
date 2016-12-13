import java.util.ArrayList;
import java.util.List;

public class Context {

    private List<Node> variables = new ArrayList<>();
    private Context parent;

    public Context(Context parent) {
        this.parent = parent;
    }

    public void declareVariable(Node node) {
        if (variableExists(node)) {
            throw new IllegalArgumentException("Variable already declared.");
        }

        variables.add(node);
    }

    public boolean isDeclared(Node node) {
        Context current = this;

        while (current != null) {
            if (current.variableExists(node)) {
                return true;
            }
            current = current.getParent();
        }

        return false;
    }

    private boolean variableExists(Node node) {
        for (Node variableNode : variables) {
            if (variableNode.getData().equals(node.getData())) {
                return true;
            }
        }
        return false;
    }

    public Context getParent() {
        return parent;
    }

}
