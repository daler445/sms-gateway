package tj.epic.sms.gateway.ws.application.actions.RetrieveSms;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tj.epic.sms.gateway.ws.application.actions.Action;
import tj.epic.sms.gateway.ws.application.common.ErrorList;
import tj.epic.sms.gateway.ws.application.exceptions.HttpBadRequestException;
import tj.epic.sms.gateway.ws.application.exceptions.HttpInternalServerErrorException;
import tj.epic.sms.gateway.ws.application.queue.Producer;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.CouldNotAcceptSmsException;
import tj.epic.sms.gateway.ws.domain.modules.sms.SmsAcceptStatus;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.body.BodyContainsInvalidCharactersException;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.body.BodyIsTooBigException;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.body.BodyIsTooSmallException;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.receiver.BrokenPhoneNumberException;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.sender.BrokenSenderNameException;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.sender.SenderNameIsTooBigException;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.sender.SenderNameIsTooSmallException;
import tj.epic.sms.gateway.ws.domain.modules.sms.Body.CheckBody;
import tj.epic.sms.gateway.ws.domain.modules.sms.Body.MessageBody;
import tj.epic.sms.gateway.ws.domain.modules.sms.Receiver.CheckReceiver;
import tj.epic.sms.gateway.ws.domain.modules.sms.Receiver.Receiver;
import tj.epic.sms.gateway.ws.domain.modules.sms.Sender.CheckSender;
import tj.epic.sms.gateway.ws.domain.modules.sms.Sender.Sender;

import java.util.HashMap;

@RestController
public class RetrieveSmsInGenericThreadAction extends Action {
	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public ResponseEntity<JsonNode> retrieveGet(
			@RequestParam(name = "receiver") String phoneNumber,
			@RequestParam(name = "sender") String senderName,
			@RequestParam(name = "text") String messageBody,
			@RequestParam(name = "gateway", required = false, defaultValue = "auto") String gateway
	) {
		return this.action(phoneNumber, senderName, messageBody, gateway);
	}

	@RequestMapping(value = "/send", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<JsonNode> retrievePOST(
			@RequestParam(name = "receiver") String phoneNumber,
			@RequestParam(name = "sender") String senderName,
			@RequestParam(name = "text") String messageBody,
			@RequestParam(name = "gateway", required = false, defaultValue = "auto") String gateway
	) {
		return this.action(phoneNumber, senderName, messageBody, gateway);
	}

	private ResponseEntity<JsonNode> action(String phoneNumber, String senderName, String messageBody, String gateway) {
		Receiver receiver;
		try {
			receiver = CheckReceiver.parse(phoneNumber);
		} catch (BrokenPhoneNumberException e) {
			throw new HttpBadRequestException(ErrorList.E0x00000f01);
		}

		Sender sender;
		try {
			sender = CheckSender.parse(senderName);
		} catch (BrokenSenderNameException e) {
			throw new HttpBadRequestException(ErrorList.E0x00000f02);
		} catch (SenderNameIsTooSmallException e) {
			throw new HttpBadRequestException(ErrorList.E0x00000f03);
		} catch (SenderNameIsTooBigException e) {
			throw new HttpBadRequestException(ErrorList.E0x00000f04);
		}

		MessageBody body;
		try {
			body = CheckBody.parse(messageBody);
		} catch (BodyIsTooSmallException e) {
			throw new HttpBadRequestException(ErrorList.E0x00000f05);
		} catch (BodyIsTooBigException e) {
			throw new HttpBadRequestException(ErrorList.E0x00000f06);
		} catch (BodyContainsInvalidCharactersException e) {
			throw new HttpBadRequestException(ErrorList.E0x00000f07);
		}

		try {
			SmsAcceptStatus responseData = Producer.send(receiver, sender, body, gateway);

			HashMap<String, Object> response = new HashMap<>();
			response.put("sms", responseData);
			return this.respondWithData(200, response);
		} catch (CouldNotAcceptSmsException e) {
			e.printStackTrace();
			throw new HttpInternalServerErrorException(ErrorList.E0x00000f08);
		}
	}
}
