package tj.epic.sms.gateway.ws.domain.modules.sms.Receiver;

import tj.epic.sms.gateway.ws.domain.modules.sms.GatewayTypes;

public class Receiver {
	private String rawValue;
	private String formattedNumber;

	private String countryCode;
	private String number;
	private GatewayTypes gatewayType;

	public Receiver() {
	}

	public Receiver(String rawValue) {
		this.rawValue = rawValue;
	}

	public Receiver(String rawValue, String formattedNumber, String countryCode, String number, GatewayTypes gatewayType) {
		this.rawValue = rawValue;
		this.formattedNumber = formattedNumber;
		this.countryCode = countryCode;
		this.number = number;
		this.gatewayType = gatewayType;
	}

	public String getRawValue() {
		return rawValue;
	}

	public String getFormattedNumber() {
		return formattedNumber;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getNumber() {
		return getCountryCode() + getFormattedNumber();
	}

	public void setRawValue(String rawValue) {
		this.rawValue = rawValue;
	}

	public void setFormattedNumber(String formattedNumber) {
		this.formattedNumber = formattedNumber;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public GatewayTypes getGatewayType() {
		return gatewayType;
	}

	public void setGatewayType(GatewayTypes gatewayType) {
		this.gatewayType = gatewayType;
	}
}
