
public class InternalNodeContext {
	public Node node;
	public String production;
	public SymbolInfo symbolInfo;
	public Node child;
	public Node child2;
	public Node child3;

	public InternalNodeContext(Node node) {
		this.node = node;
		production = node.getProduction();
		symbolInfo = node.getSymbolInfo();
		if (!node.isLeaf()) {
			child = node.getChildren().get(0); // default is first child, which
												// is often the case
			if(node.getChildren().size()==2){
				child2=node.getChildren().get(1);
			}
			if(node.getChildren().size()==3){
				child2=node.getChildren().get(2);
			}
		}
	}

	public boolean isProduction(String other) {
		return production.equals(other);
	}
}
