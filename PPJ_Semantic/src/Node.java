import java.util.ArrayList;
import java.util.List;

public class Node {

    public String data;
    public Node parent;
    public List<Node> children = new ArrayList<Node>();

    public Node(String data, Node parent) {
        this.data = data;
        this.parent = parent;
    }

    public void addChild(Node node) {
        children.add(node);
    }

    public void print(int level) {
        System.out.println(whitespace(level) + data);

        for (Node node : children) {
            node.print(level + 1);
        }
    }

    private String whitespace(int length) {
        String res = "";
        for (int i = 0; i < length; i++) {
            res += " ";
        }
        return res;
    }

    public String getData() {
        return data;
    }

    public Node getParent() {
        return parent;
    }

    public List<Node> getChildren() {
        return children;
    }
}