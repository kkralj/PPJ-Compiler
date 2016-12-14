
public class SemanticAnalyserException extends Exception {
	public SemanticAnalyserException() {
	}

	public SemanticAnalyserException(String production) {
		super(production);
	}
}
