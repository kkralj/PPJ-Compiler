import java.util.ArrayList;
import java.util.List;

public class Node {

	private String label;
	private Node parent;
	private List<Node> children = new ArrayList<>();

	private SymbolInfo symbolInfo;

	public Node(String label, Node parent) {
		this.label = label;
		this.parent = parent;
		this.symbolInfo = new SymbolInfo();
	}

	public void addChild(Node node) {
		children.add(node);
	}

	public void print(int level) {
		System.out.println(whitespace(level) + label);

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

	public String getProduction() {
		String production = label + " ::=";

		for (Node child : children) {
			production += " " + child.label;
		}

		return production;
	}

	public String getTokenName() {
		return label.split(" ")[2];
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public Node getParent() {
		return parent;
	}

	public SymbolInfo getSymbolInfo() {
		return symbolInfo;
	}

	public List<Node> getChildren() {
		return children;
	}

	public boolean isLeaf() {
		return children.isEmpty();
	}

}