package tj.epic.sms.gateway.ws.domain.modules.gateways.smpp;

public enum  NumberingPlanIndicator {
	UNKNOWN,
	ISDN,
	DATA,
	TELEX,
	LAND_MOBILE,
	NATIONAL,
	PRIVATE,
	ERMES,
	INTERNET,
	WAP,
	;

	public org.jsmpp.bean.NumberingPlanIndicator get() {
		switch (this) {
			case NATIONAL:
				return org.jsmpp.bean.NumberingPlanIndicator.NATIONAL;
			case WAP:
				return org.jsmpp.bean.NumberingPlanIndicator.WAP;
			case DATA:
				return org.jsmpp.bean.NumberingPlanIndicator.DATA;
			case ISDN:
				return org.jsmpp.bean.NumberingPlanIndicator.ISDN;
			case ERMES:
				return org.jsmpp.bean.NumberingPlanIndicator.ERMES;
			case TELEX:
				return org.jsmpp.bean.NumberingPlanIndicator.TELEX;
			case PRIVATE:
				return org.jsmpp.bean.NumberingPlanIndicator.PRIVATE;
			case INTERNET:
				return org.jsmpp.bean.NumberingPlanIndicator.INTERNET;
			case LAND_MOBILE:
				return org.jsmpp.bean.NumberingPlanIndicator.LAND_MOBILE;
			case UNKNOWN:
			default:
				return org.jsmpp.bean.NumberingPlanIndicator.UNKNOWN;
		}
	}
}
