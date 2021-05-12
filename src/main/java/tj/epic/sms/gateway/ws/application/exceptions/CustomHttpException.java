package tj.epic.sms.gateway.ws.application.exceptions;

import tj.epic.sms.gateway.ws.application.common.ErrorList;

abstract public class CustomHttpException extends RuntimeException{
	protected final String code;
	protected final String description;

	public CustomHttpException(ErrorList error) {
		this.code = error.getErrorCode();
		this.description = error.getErrorDescription();
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
}
