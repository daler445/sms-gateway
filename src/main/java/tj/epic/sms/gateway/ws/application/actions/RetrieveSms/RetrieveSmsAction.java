package tj.epic.sms.gateway.ws.application.actions.RetrieveSms;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import tj.epic.sms.gateway.ws.application.queue.Consumer;
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
public class RetrieveSmsAction extends Action {
	private static final Logger logger = LoggerFactory.getLogger(RetrieveSmsAction.class);

	@RequestMapping(value = "/old", method = RequestMethod.GET)
	@Deprecated
	public String retrieveOldCompatibility(
			@RequestParam(name = "_to") String phoneNumber,
			@RequestParam(name = "_from") String senderName,
			@RequestParam(name = "_text") String messageBody
	) {
		ResponseEntity<JsonNode> response = this.action(phoneNumber, senderName, messageBody, Consumer.GLOBAL_QUEUE_NAME_LOCAL);
		if (response.getStatusCode().is2xxSuccessful()) {
			return "Success. SMS ID: xxxxxxxx";
		}
		return "Error";
	}

	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public ResponseEntity<JsonNode> retrieveGet(
			@RequestParam(name = "receiver") String phoneNumber,
			@RequestParam(name = "sender") String senderName,
			@RequestParam(name = "text") String messageBody,
			@RequestParam(name = "gateway", required = false, defaultValue = "") String gateway
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
			logger.warn("Broken receiver phone number");
			throw new HttpBadRequestException(ErrorList.E0x00000f01);
		}

		Sender sender;
		try {
			sender = CheckSender.parse(senderName);
		} catch (BrokenSenderNameException e) {
			logger.warn("Broken sender name");
			throw new HttpBadRequestException(ErrorList.E0x00000f02);
		} catch (SenderNameIsTooSmallException e) {
			logger.warn("Sender name is too small");
			throw new HttpBadRequestException(ErrorList.E0x00000f03);
		} catch (SenderNameIsTooBigException e) {
			logger.warn("Sender name is too big");
			throw new HttpBadRequestException(ErrorList.E0x00000f04);
		}

		MessageBody body;
		try {
			body = CheckBody.parse(messageBody);
		} catch (BodyIsTooSmallException e) {
			logger.warn("Body is too small");
			throw new HttpBadRequestException(ErrorList.E0x00000f05);
		} catch (BodyIsTooBigException e) {
			logger.warn("Body is too big");
			throw new HttpBadRequestException(ErrorList.E0x00000f06);
		} catch (BodyContainsInvalidCharactersException e) {
			logger.warn("Body contains invalid characters");
			throw new HttpBadRequestException(ErrorList.E0x00000f07);
		}

		try {
			SmsAcceptStatus responseData = Producer.send(receiver, sender, body, gateway);
			logger.info("SMS accepted: From [" + sender.getName() + "] to [" + receiver.getNumber() + "] via [" + gateway + "]");

			HashMap<String, Object> response = new HashMap<>();
			response.put("sms", responseData);
			return this.respondWithData(200, response);
		} catch (CouldNotAcceptSmsException e) {
			logger.error("Could not accept sms: " + e.getMessage());
			throw new HttpInternalServerErrorException(ErrorList.E0x00000f08);
		}
	}
}
