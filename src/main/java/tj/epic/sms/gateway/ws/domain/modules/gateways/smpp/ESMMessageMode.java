package tj.epic.sms.gateway.ws.domain.modules.gateways.smpp;

import org.jsmpp.bean.MessageMode;

public enum ESMMessageMode {
	DEFAULT,
	DATAGRAM,
	TRANSACTION,
	STORE_AND_FORWARD,
	;

	public org.jsmpp.bean.MessageMode get() {
		switch (this) {
			case DATAGRAM:
				return MessageMode.DATAGRAM;
			case TRANSACTION:
				return MessageMode.TRANSACTION;
			case STORE_AND_FORWARD:
				return MessageMode.STORE_AND_FORWARD;
			case DEFAULT:
			default:
				return MessageMode.DEFAULT;
		}
	}
}
