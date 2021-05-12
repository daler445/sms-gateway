package tj.epic.sms.gateway.ws.application.actions;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.lang.Nullable;
import tj.epic.sms.gateway.ws.application.common.ErrorType;

import java.util.Collections;
import java.util.List;

public class ActionError {
	private ErrorType type;
	private String errorCode;
	@Nullable
	private String description;
	@Nullable
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<String> errors;

	public ActionError(ErrorType type, String errorCode, @Nullable List<String> errors) {
		this.type = type;
		this.errorCode = errorCode;
		this.description = type.getDescription();
		if (errors != null && errors.size() > 0) {
			this.errors = errors;
		}
	}

	public ActionError(ErrorType type, String errorCode, @Nullable String error) {
		this.type = type;
		this.errorCode = errorCode;
		this.description = type.getDescription();
		if (error != null && !error.equals("")) {
			this.errors = Collections.singletonList(error);
		}
	}

	public ErrorType getType() {
		return type;
	}

	public void setType(ErrorType type) {
		this.type = type;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@Nullable
	public String getDescription() {
		return description;
	}

	public void setDescription(@Nullable String description) {
		this.description = description;
	}

	@Nullable
	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(@Nullable List<String> errors) {
		this.errors = errors;
	}
}
