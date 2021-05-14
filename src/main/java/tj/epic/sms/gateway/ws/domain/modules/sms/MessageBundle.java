package tj.epic.sms.gateway.ws.domain.modules.sms;

import tj.epic.sms.gateway.ws.domain.modules.sms.Body.MessageBody;
import tj.epic.sms.gateway.ws.domain.modules.sms.Receiver.Receiver;
import tj.epic.sms.gateway.ws.domain.modules.sms.Sender.Sender;

public class MessageBundle {
	private Receiver receiver;
	private Sender sender;
	private MessageBody messageBody;

	public MessageBundle() {
	}

	public MessageBundle(Receiver receiver, Sender sender, MessageBody messageBody) {
		this.receiver = receiver;
		this.sender = sender;
		this.messageBody = messageBody;
	}

	public Receiver getReceiver() {
		return receiver;
	}

	public Sender getSender() {
		return sender;
	}

	public MessageBody getMessageBody() {
		return messageBody;
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public void setSender(Sender sender) {
		this.sender = sender;
	}

	public void setMessageBody(MessageBody messageBody) {
		this.messageBody = messageBody;
	}
}
