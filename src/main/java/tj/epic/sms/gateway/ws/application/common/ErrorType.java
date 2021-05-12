package tj.epic.sms.gateway.ws.application.common;

public enum ErrorType {
	BAD_REQUEST("BAD_REQUEST", "Error occurred while processing your data"),
	INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Error occurred while processing request"),
	METHOD_NOT_IMPLEMENTED("METHOD_NOT_IMPLEMENTED", "Method not implemented"),
	;

	private final String name;
	private final String description;

	ErrorType(String typeName, String description) {
		this.name = typeName;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}
