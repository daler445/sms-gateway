package tj.epic.sms.gateway.ws.domain.modules.sms;

import tj.epic.sms.gateway.ws.domain.modules.sms.body.MessageBody;
import tj.epic.sms.gateway.ws.domain.modules.sms.priority.MessagePriority;
import tj.epic.sms.gateway.ws.domain.modules.sms.receiver.Receiver;
import tj.epic.sms.gateway.ws.domain.modules.sms.schedule.MessageSchedule;
import tj.epic.sms.gateway.ws.domain.modules.sms.sender.Sender;

public class MessageBundle {
	private Receiver receiver;
	private Sender sender;
	private MessageBody messageBody;
	private MessagePriority messagePriority;
	private MessageSchedule messageSchedule;
	private long smsId;

	public MessageBundle() {
	}

	public MessageBundle(Receiver receiver, Sender sender, MessageBody messageBody, MessagePriority messagePriority, MessageSchedule messageSchedule, long smsId) {
		this.receiver = receiver;
		this.sender = sender;
		this.messageBody = messageBody;
		this.messagePriority = messagePriority;
		this.messageSchedule = messageSchedule;
		this.smsId = smsId;
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

	public MessagePriority getMessagePriority() {
		return messagePriority;
	}

	public void setMessagePriority(MessagePriority messagePriority) {
		this.messagePriority = messagePriority;
	}

	public MessageSchedule getMessageSchedule() {
		return messageSchedule;
	}

	public void setMessageSchedule(MessageSchedule messageSchedule) {
		this.messageSchedule = messageSchedule;
	}

	public long getSmsId() {
		return smsId;
	}

	public void setSmsId(long smsId) {
		this.smsId = smsId;
	}
}
