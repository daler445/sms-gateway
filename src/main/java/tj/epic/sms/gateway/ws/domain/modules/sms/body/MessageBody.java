package tj.epic.sms.gateway.ws.domain.modules.sms.body;

public class MessageBody {
	private String body;

	public MessageBody() {
	}

	public MessageBody(String body) {
		this.body = body.trim();
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body.trim();
	}
}
