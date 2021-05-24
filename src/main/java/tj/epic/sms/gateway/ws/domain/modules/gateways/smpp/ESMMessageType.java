package tj.epic.sms.gateway.ws.domain.modules.gateways.smpp;

import org.jsmpp.bean.MessageType;

public enum ESMMessageType {
	DEFAULT,
	ESME_DEL_ACK,
	ESME_MAN_ACK,
	SMSC_DEL_RECEIPT,
	SME_DEL_ACK,
	SME_MAN_ACK,
	CONV_ABORT,
	INTER_DEL_NOTIF,
	;

	public MessageType get() {
		switch (this) {
			case CONV_ABORT:
				return MessageType.CONV_ABORT;
			case SME_DEL_ACK:
				return MessageType.SME_DEL_ACK;
			case SME_MAN_ACK:
				return MessageType.SME_MAN_ACK;
			case ESME_DEL_ACK:
				return MessageType.ESME_DEL_ACK;
			case ESME_MAN_ACK:
				return MessageType.ESME_MAN_ACK;
			case INTER_DEL_NOTIF:
				return MessageType.INTER_DEL_NOTIF;
			case SMSC_DEL_RECEIPT:
				return MessageType.SMSC_DEL_RECEIPT;
			case DEFAULT:
			default:
				return MessageType.DEFAULT;
		}
	}
}
