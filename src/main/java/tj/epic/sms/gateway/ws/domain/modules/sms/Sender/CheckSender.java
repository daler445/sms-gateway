package tj.epic.sms.gateway.ws.domain.modules.sms.Sender;

import tj.epic.sms.gateway.ws.domain.exceptions.sms.sender.BrokenSenderNameException;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.sender.SenderNameIsTooBigException;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.sender.SenderNameIsTooSmallException;

public class CheckSender {
	public static Sender parse(String senderName) throws BrokenSenderNameException, SenderNameIsTooSmallException, SenderNameIsTooBigException {
		if (!senderName.matches("^[a-zA-Z][a-zA-Z0-9- ]+$")) {
			throw new BrokenSenderNameException();
		}

		if (senderName.length() <= 2) {
			throw new SenderNameIsTooSmallException();
		}

		if (senderName.length() >= 15) {
			throw new SenderNameIsTooBigException();
		}

		return new Sender(senderName);
	}
}
