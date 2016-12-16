
public class InternalNodeContext {
	public Node node;
	public String production;
	public SymbolInfo symbolInfo;
	public Node firstChild;

	public InternalNodeContext(Node node) {
		this.node = node;
		production = node.getProduction();
		symbolInfo = node.getSymbolInfo();
		if (!node.isLeaf()) {
			firstChild = node.getChildren().get(0); // default is first child, which
												// is often the case
		}
	}

	public boolean isProduction(String other) {
		return production.equals(other);
	}
}
