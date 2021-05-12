package tj.epic.sms.gateway.ws.application.actions;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.lang.Nullable;

public class ActionPayload {
	private int statusCode;

	@Nullable
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Object data;

	@Nullable
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ActionError error;

	public ActionPayload(int statusCode, @Nullable Object data, @Nullable ActionError error) {
		this.statusCode = statusCode;
		this.data = data;
		this.error = error;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	@Nullable
	public Object getData() {
		return data;
	}

	public void setData(@Nullable Object data) {
		this.data = data;
	}

	@Nullable
	public ActionError getError() {
		return error;
	}

	public void setActionError(@Nullable ActionError actionError) {
		this.error = actionError;
	}
}
