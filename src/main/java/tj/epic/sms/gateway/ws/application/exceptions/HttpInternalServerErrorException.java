package tj.epic.sms.gateway.ws.application.exceptions;

import tj.epic.sms.gateway.ws.application.common.ErrorList;

public class HttpInternalServerErrorException extends CustomHttpException {
	public HttpInternalServerErrorException(ErrorList error) {
		super(error);
	}
}
