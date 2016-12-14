
public enum DataType {
	VOID, CHAR, INT, CONST_CHAR, CONST_INT, CHAR_ARRAY, INT_ARRAY, CONST_CHAR_ARRAY, CONST_INT_ARRAY;

	public static DataType getDataType(String dataType) {
		if (dataType == "void") {
			return VOID;
		} else if (dataType == "char") {
			return CHAR;
		} else if (dataType == "int") {
			return INT;
		}

		return VOID;
	}
}
