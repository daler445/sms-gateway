package tj.epic.sms.gateway.ws.domain.modules.sms.sender;

public class Sender {
	private String name;

	public Sender() {
	}

	public Sender(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
