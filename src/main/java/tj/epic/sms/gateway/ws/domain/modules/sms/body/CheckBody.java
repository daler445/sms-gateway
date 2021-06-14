package tj.epic.sms.gateway.ws.domain.modules.sms.body;

import tj.epic.sms.gateway.ws.domain.exceptions.sms.body.BodyContainsInvalidCharactersException;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.body.BodyIsTooBigException;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.body.BodyIsTooSmallException;

public class CheckBody {
	public static MessageBody parse(String body) throws BodyIsTooSmallException, BodyIsTooBigException, BodyContainsInvalidCharactersException {

		if (body.length() <= 3) {
			throw new BodyIsTooSmallException();
		}

		if (body.length() > 2500) {
			throw new BodyIsTooBigException();
		}

		if (!body.matches("^[a-zA-Zа-яА-ЯёЁӢӣҚқӮӯҲҳҶҷҒғ0-9\\-_+%\\\\/–—$#№?* ]+$")) {
			throw new BodyContainsInvalidCharactersException();
		}

		return new MessageBody(body);
	}
}
