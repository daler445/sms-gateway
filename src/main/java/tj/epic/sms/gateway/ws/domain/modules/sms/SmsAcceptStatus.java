package tj.epic.sms.gateway.ws.domain.modules.sms;

public class SmsAcceptStatus {
	private final String status;
	private final String receiver;
	private final String sender;
	private final String gateway;

	public SmsAcceptStatus(String receiver, String sender, String gateway) {
		this.status = "accepted";
		this.receiver = receiver;
		this.sender = sender;
		this.gateway = gateway;
	}

	public String getStatus() {
		return status;
	}

	public String getReceiver() {
		return receiver;
	}

	public String getSender() {
		return sender;
	}

	public String getGateway() {
		return gateway;
	}
}
