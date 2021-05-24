package tj.epic.sms.gateway.ws.domain.modules.gateways.smpp;

public enum BindType {
	TRANSMITTER,
	RECEIVER,
	TRANSCEIVER,;

	public org.jsmpp.bean.BindType get() {
		if (this.equals(TRANSMITTER)) {
			return org.jsmpp.bean.BindType.BIND_TX;
		}
		if (this.equals(RECEIVER)) {
			return org.jsmpp.bean.BindType.BIND_RX;
		}
		return org.jsmpp.bean.BindType.BIND_TRX;
	}
}
