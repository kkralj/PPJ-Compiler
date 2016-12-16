
public class InternalNodeContext {
	public Node node;
	public String production;
	public SymbolInfo symbolInfo;
	public Node firstChild;
	public Node child;
	public Node child2;
	public Node child3;

	public InternalNodeContext(Node node) {
		this.node = node;
		production = node.getProduction();
		symbolInfo = node.getSymbolInfo();
		if (!node.isLeaf()) {
			firstChild = node.getChildren().get(0); // default is first child, which
												// is often the case
			
			int childrenSize = node.getChildren().size();
			
			if(childrenSize==1) {
				child = node.getChild(0);
			}
			if(childrenSize==2){
				child2=node.getChild(1);
			}
			if(childrenSize==3){
				child3=node.getChild(2);
			}
		}
	}

	public boolean isProduction(String other) {
		return production.equals(other);
	}
}
