
public class SymbolInfo {
	SymbolType symbolType;
	DataType dataType;
	boolean l_expr;

	public SymbolInfo() {
	}

	public SymbolInfo(SymbolType symbolType, DataType dataType, boolean l_expr) {
		this.symbolType = symbolType;
		this.dataType = dataType;
		this.l_expr = l_expr;
	}
	
	

}
