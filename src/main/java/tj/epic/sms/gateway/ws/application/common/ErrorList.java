package tj.epic.sms.gateway.ws.application.common;

public enum ErrorList {
	E0x00000f01("0x00000f01", "Broken receiver phone number provided"),
	E0x00000f02("0x00000f02", "Broken sender name provided"),
	E0x00000f03("0x00000f03", "Sender name must be at least 3 characters"),
	E0x00000f04("0x00000f04", "Sender name must not exceed 12 character length"),
	E0x00000f05("0x00000f05", "Body must be at least 4 characters"),
	E0x00000f06("0x00000f06", "Body must be less than 2500 characters"),
	E0x00000f07("0x00000f07", "Body contains invalid characters"),
	E0x00000f08("0x00000f08", "Could not accept sms"),
	;

	private final String errorCode;
	private final String errorDescription;

	ErrorList(String errorCode, String errorDescription) {
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}
}
