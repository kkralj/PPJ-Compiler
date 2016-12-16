
public class SemanticAnalyserException extends Exception {
	private Node node;
	
	public SemanticAnalyserException() {
	}

	public SemanticAnalyserException(Node node) {
		super("Semantic error");
		this.node = node;
	}
	
	public String getProduction() {
		return node.getFullProduction();
	}
}
