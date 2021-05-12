package tj.epic.sms.gateway.ws.application.exceptions;

import tj.epic.sms.gateway.ws.application.common.ErrorList;

public class HttpBadRequestException extends CustomHttpException {
	public HttpBadRequestException(ErrorList error) {
		super(error);
	}
}
