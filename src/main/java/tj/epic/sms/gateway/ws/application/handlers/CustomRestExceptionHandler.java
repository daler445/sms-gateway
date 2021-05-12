package tj.epic.sms.gateway.ws.application.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tj.epic.sms.gateway.ws.application.actions.ActionError;
import tj.epic.sms.gateway.ws.application.actions.ActionPayload;
import tj.epic.sms.gateway.ws.application.common.ErrorType;
import tj.epic.sms.gateway.ws.application.exceptions.CustomHttpException;

import java.util.Objects;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		ActionError error = new ActionError(ErrorType.BAD_REQUEST, "500", "Method argument is not valid");
		ActionPayload apiError = new ActionPayload(500, null, error);
		return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
			HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request
	) {
		StringBuilder builder = new StringBuilder();
		builder.append(ex.getMethod());
		builder.append(" method is not supported for this request. Supported methods are");
		Objects.requireNonNull(ex.getSupportedHttpMethods()).forEach(t -> builder.append(" ").append(t));

		ActionError error = new ActionError(ErrorType.METHOD_NOT_IMPLEMENTED, "405", builder.toString());
		ActionPayload apiError = new ActionPayload(405, null, error);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED);
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		ActionError error = new ActionError(ErrorType.BAD_REQUEST, "400", "Missing request parameter (parameters)");
		ActionPayload apiError = new ActionPayload(405, null, error);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<JsonNode> handleAll(Exception ex, WebRequest request) {
		if (ex instanceof CustomHttpException) { // manually thrown exceptions
			ActionError actionError = new ActionError(ErrorType.BAD_REQUEST, ((CustomHttpException) ex).getCode(), ((CustomHttpException) ex).getDescription());
			ActionPayload actionPayload = new ActionPayload(400, null, actionError);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.valueToTree(actionPayload);
			return new ResponseEntity<>(jsonNode, new HttpHeaders(), HttpStatus.BAD_REQUEST);
		}

		// uncaught exception occurred
		ActionError apiError = new ActionError(ErrorType.INTERNAL_SERVER_ERROR, "0x000a", ex.getMessage());
		ActionPayload actionPayload = new ActionPayload(500, null, apiError);
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.valueToTree(actionPayload);
		return new ResponseEntity<>(jsonNode, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
