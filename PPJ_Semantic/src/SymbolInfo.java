import java.util.ArrayList;
import java.util.List;

public class SymbolInfo {
	SymbolType symbolType;
	/**
	 * In case of a function, first element is return type, other elements are
	 * argument types. In case of variable, just one element is needed -- type
	 * of variable.
	 */
	List<DataType> dataType = new ArrayList<>();
	boolean l_expr;

	public SymbolInfo() {
	}

	public SymbolInfo(SymbolType symbolType, DataType dataType, boolean l_expr) {
		this.symbolType = symbolType;
		this.l_expr = l_expr;

		this.dataType.add(dataType);
	}

	public SymbolInfo(SymbolType symbolType, List<DataType> dataType, boolean l_expr) {
		this.symbolType = symbolType;
		this.l_expr = l_expr;

		this.dataType.addAll(dataType);
	}

	public List<DataType> dataTypeTail() {
		return dataType.subList(1, dataType.size() - 1);
	}

}
