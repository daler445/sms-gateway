package tj.epic.sms.gateway.ws.domain.modules.gateways.smpp;

public enum TypeOfNumber {
	UNKNOWN,
	INTERNATIONAL,
	NATIONAL,
	NETWORK_SPECIFIC,
	SUBSCRIBER_NUMBER,
	ALPHANUMERIC,
	ABBREVIATED,
	;

	public org.jsmpp.bean.TypeOfNumber get() {
		switch (this) {
			case NATIONAL:
				return org.jsmpp.bean.TypeOfNumber.NATIONAL;
			case ABBREVIATED:
				return org.jsmpp.bean.TypeOfNumber.ABBREVIATED;
			case ALPHANUMERIC:
				return org.jsmpp.bean.TypeOfNumber.ALPHANUMERIC;
			case INTERNATIONAL:
				return org.jsmpp.bean.TypeOfNumber.INTERNATIONAL;
			case NETWORK_SPECIFIC:
				return org.jsmpp.bean.TypeOfNumber.NETWORK_SPECIFIC;
			case SUBSCRIBER_NUMBER:
				return org.jsmpp.bean.TypeOfNumber.SUBSCRIBER_NUMBER;
			case UNKNOWN:
			default:
				return org.jsmpp.bean.TypeOfNumber.UNKNOWN;
		}
	}
}
