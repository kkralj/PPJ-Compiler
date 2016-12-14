import java.util.Map;

public class Scope {

	private Map<String, SymbolInfo> symbolTable;
	private Scope parent;

	public Scope(Scope parent) {
		this.parent = parent;
	}

	public SymbolInfo addVariable(Node node) {
		if (symbolExists(node.getTokenName())) {
			throw new IllegalArgumentException("Variable already declared.");
		}

		String symbolName = node.getLabel().split(" ")[1];

		SymbolInfo symbolInfo = new SymbolInfo();

		symbolTable.put(symbolName, symbolInfo);

		return symbolInfo;
	}

	public boolean isDeclared(String name) {
		Scope current = this;

		while (current != null) {
			if (current.symbolExists(name)) {
				return true;
			}
			current = current.getParent();
		}

		return false;
	}

	private boolean symbolExists(String name) {
		return symbolTable.containsKey(name);
	}

	public SymbolInfo getSymbolInfo(String name) {
		return symbolTable.get(name);
	}

	public Scope getParent() {
		return parent;
	}

}
