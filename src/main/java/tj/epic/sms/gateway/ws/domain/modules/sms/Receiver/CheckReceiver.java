package tj.epic.sms.gateway.ws.domain.modules.sms.Receiver;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.receiver.BrokenPhoneNumberException;
import tj.epic.sms.gateway.ws.domain.modules.sms.GatewayTypes;

public class CheckReceiver {
	public static Receiver parse(String phoneNumber) throws BrokenPhoneNumberException {
		String rawNumber = phoneNumber.replaceAll("[^\\d.]", "");
		Receiver receiver = new Receiver(rawNumber);
		if (rawNumber.length() == 9) {
			receiver.setCountryCode("992");
			receiver.setFormattedNumber(rawNumber);
			receiver.setNumber("992" + rawNumber);
			receiver.setGatewayType(GatewayTypes.Local);
			return receiver;
		}

		if (rawNumber.length() == 12 && rawNumber.startsWith("992")) {
			receiver.setCountryCode("992");
			receiver.setFormattedNumber(rawNumber.substring(3, 12));
			receiver.setGatewayType(GatewayTypes.Local);
			return receiver;
		}

		if (rawNumber.length() > 9) {
			PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
			try {
				Phonenumber.PhoneNumber parsed = phoneNumberUtil.parse("+"+rawNumber, "US");
				if (phoneNumberUtil.isValidNumber(parsed)) {
					receiver.setFormattedNumber(String.valueOf(parsed.getNationalNumber()));
					receiver.setCountryCode(""+parsed.getCountryCode());
					if (receiver.getCountryCode().equals("992")) {
						receiver.setGatewayType(GatewayTypes.Local);
					} else {
						receiver.setGatewayType(GatewayTypes.External);
					}
					return receiver;
				}
			} catch (NumberParseException ignored) {
			}
		}
		throw new BrokenPhoneNumberException();
	}
}
