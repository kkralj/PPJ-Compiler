import java.util.ArrayList;
import java.util.List;

public class Node {

    private Node parent;

    public String line;

    public boolean lSide;
    public String type;

    public String variableName;
    public String variableType;

    public List<String> argumentTypes = new ArrayList<>();
    public List<String> argumentNames = new ArrayList<>();

    public int intValue;
    public int charValue;

    public String ntip;

    public boolean isFunction;
    public boolean isConstant;
    public boolean isVariable;
    public boolean isArray;

    public void setArgumentNames(String argumentName) {
        this.argumentNames = new ArrayList<>();
        this.argumentNames.add(argumentName);
    }

    public void setArgumentTypes(String argumentType) {
        this.argumentTypes = new ArrayList<>();
        this.argumentTypes.add(argumentType);
    }

    public void storeData(Node other) {
        this.variableName = other.variableName;
        this.variableType = other.variableType;

        this.argumentTypes = other.argumentTypes;
        this.argumentNames = other.argumentNames;

        this.intValue = other.intValue;
        this.charValue = other.charValue;

        this.isFunction = other.isFunction;
        this.isConstant = other.isConstant;
        this.isVariable = other.isVariable;
        this.isArray = other.isArray;
    }


    public boolean isValidNumber() {
        try {
            Integer intTest = Integer.parseInt(line.split("\\s+")[2]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidChar() {
        String charTest = line.split("\\s+")[2];
        return false;
    }

    public boolean isValidCharArray() {
        String charArrayTest = line.split("\\s+")[2];
        return false;
    }

    private List<Node> children = new ArrayList<>();


    public Node(String line, Node parent) {
        this.line = line.trim();
        this.parent = parent;
    }

    public void addChild(Node node) {
        children.add(node);
    }

    public void print(int level) {
        System.out.println(whitespace(level) + line);

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


    public Node getParent() {
        return parent;
    }

    public List<Node> getChildren() {
        return children;
    }


    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public boolean islSide() {
        return lSide;
    }

    public void setlSide(boolean lSide) {
        this.lSide = lSide;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}