package jadx.api.plugins.input.insns;

public enum Opcode {
	UNKNOWN,
	NOP,

	ADD_DOUBLE,
	ADD_FLOAT,
	ADD_INT,
	ADD_INT_LIT,
	ADD_LONG,

	AND_INT,
	AND_INT_LIT,
	AND_LONG,

	AGET,
	AGET_BOOLEAN,
	AGET_BYTE,
	AGET_BYTE_BOOLEAN,
	AGET_CHAR,
	AGET_OBJECT,
	AGET_SHORT,
	AGET_WIDE,

	APUT,
	APUT_BOOLEAN,
	APUT_BYTE,
	APUT_BYTE_BOOLEAN,
	APUT_CHAR,
	APUT_OBJECT,
	APUT_SHORT,
	APUT_WIDE,

	ARITH,
	ARRAY_LENGTH,

	CAST,
	CHECK_CAST,

	CMPG_DOUBLE,
	CMPG_FLOAT,
	CMPL_DOUBLE,
	CMPL_FLOAT,
	CMP_LONG,

	CONST,
	CONST_CLASS,
	CONST_STRING,
	CONST_WIDE,

	DIV_DOUBLE,
	DIV_FLOAT,
	DIV_INT,
	DIV_INT_LIT,
	DIV_LONG,

	DOUBLE_TO_FLOAT,
	DOUBLE_TO_INT,
	DOUBLE_TO_LONG,

	FLOAT_TO_DOUBLE,
	FLOAT_TO_INT,
	FLOAT_TO_LONG,

	GOTO,
	IF,
	IF_EQ,
	IF_EQZ,
	IF_GE,
	IF_GEZ,
	IF_GT,
	IF_GTZ,
	IF_LE,
	IF_LEZ,
	IF_LT,
	IF_LTZ,
	IF_NE,
	IF_NEZ,

	INSTANCE_OF,

	INT_TO_BYTE,
	INT_TO_CHAR,
	INT_TO_DOUBLE,
	INT_TO_FLOAT,
	INT_TO_LONG,
	INT_TO_SHORT,

	INVOKE_DIRECT,
	INVOKE_DIRECT_RANGE,
	INVOKE_INTERFACE,
	INVOKE_INTERFACE_RANGE,
	INVOKE_STATIC,
	INVOKE_STATIC_RANGE,
	INVOKE_SUPER,
	INVOKE_SUPER_RANGE,
	INVOKE_VIRTUAL,
	INVOKE_VIRTUAL_RANGE,
	INVOKE_SPECIAL,

	IGET,
	IPUT,

	SGET,
	SPUT,

	LONG_TO_DOUBLE,
	LONG_TO_FLOAT,
	LONG_TO_INT,

	MONITOR_ENTER,
	MONITOR_EXIT,

	MOVE,
	MOVE_MULTI,
	MOVE_EXCEPTION,
	MOVE_OBJECT,
	MOVE_RESULT,
	MOVE_WIDE,

	MUL_DOUBLE,
	MUL_FLOAT,
	MUL_INT,
	MUL_INT_LIT,
	MUL_LONG,

	NEG,
	NEG_DOUBLE,
	NEG_FLOAT,
	NEG_INT,
	NEG_LONG,
	NEW_INSTANCE,

	NOT_INT,
	NOT_LONG,

	OR_INT,
	OR_INT_LIT,
	OR_LONG,

	REM_DOUBLE,
	REM_FLOAT,
	REM_INT,
	REM_INT_LIT,
	REM_LONG,

	RETURN,
	RETURN_VOID,

	RSUB_INT,
	SHL_INT,
	SHL_INT_LIT,
	SHL_LONG,
	SHR_INT,
	SHR_INT_LIT,
	SHR_LONG,
	SUB_DOUBLE,
	SUB_FLOAT,
	SUB_INT,
	SUB_LONG,

	THROW,

	USHR_INT,
	USHR_INT_LIT,
	USHR_LONG,
	XOR_INT,
	XOR_INT_LIT,
	XOR_LONG,

	NEW_ARRAY,

	FILLED_NEW_ARRAY,
	FILLED_NEW_ARRAY_RANGE,
	FILL_ARRAY_DATA,
	FILL_ARRAY_DATA_PAYLOAD,

	PACKED_SWITCH,
	PACKED_SWITCH_PAYLOAD,
	SPARSE_SWITCH,
	SPARSE_SWITCH_PAYLOAD,

	INVOKE_POLYMORPHIC,
	INVOKE_POLYMORPHIC_RANGE,

	INVOKE_CUSTOM,
	INVOKE_CUSTOM_RANGE,

	CONST_METHOD_HANDLE,
	CONST_METHOD_TYPE,
}
