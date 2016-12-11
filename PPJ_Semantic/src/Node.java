import java.util.ArrayList;
import java.util.List;

public class Node {

    private String data;
    private Node parent;
    private List<Node> children = new ArrayList<>();

    private String type;
    private boolean leftOK;


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

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public boolean isLeftOK() {
        return leftOK;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLeftOK(boolean leftOK) {
        this.leftOK = leftOK;
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