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
import tj.epic.sms.gateway.ws.domain.modules.sms.priority.CheckPriority;
import tj.epic.sms.gateway.ws.domain.modules.sms.priority.MessagePriority;
import tj.epic.sms.gateway.ws.domain.modules.sms.SmsAcceptStatus;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.body.BodyContainsInvalidCharactersException;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.body.BodyIsTooBigException;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.body.BodyIsTooSmallException;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.receiver.BrokenPhoneNumberException;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.sender.BrokenSenderNameException;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.sender.SenderNameIsTooBigException;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.sender.SenderNameIsTooSmallException;
import tj.epic.sms.gateway.ws.domain.modules.sms.body.CheckBody;
import tj.epic.sms.gateway.ws.domain.modules.sms.body.MessageBody;
import tj.epic.sms.gateway.ws.domain.modules.sms.receiver.CheckReceiver;
import tj.epic.sms.gateway.ws.domain.modules.sms.receiver.Receiver;
import tj.epic.sms.gateway.ws.domain.modules.sms.schedule.MessageSchedule;
import tj.epic.sms.gateway.ws.domain.modules.sms.sender.CheckSender;
import tj.epic.sms.gateway.ws.domain.modules.sms.sender.Sender;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
		ResponseEntity<JsonNode> response = this.action(phoneNumber, senderName, messageBody, Consumer.GLOBAL_QUEUE_NAME_LOCAL, "normal", "now");
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
			@RequestParam(name = "gateway", required = false, defaultValue = "") String gateway,
			@RequestParam(name = "priority", required = false, defaultValue = "normal") String priority,
			@RequestParam(name = "schedule", required = false, defaultValue = "now") String schedule
	) {
		return this.action(phoneNumber, senderName, messageBody, gateway, priority, schedule);
	}

	@RequestMapping(value = "/send", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<JsonNode> retrievePOST(
			@RequestParam(name = "receiver") String phoneNumber,
			@RequestParam(name = "sender") String senderName,
			@RequestParam(name = "text") String messageBody,
			@RequestParam(name = "gateway", required = false, defaultValue = "auto") String gateway,
			@RequestParam(name = "priority", required = false, defaultValue = "normal") String priority,
			@RequestParam(name = "schedule", required = false, defaultValue = "now") String schedule
	) {
		return this.action(phoneNumber, senderName, messageBody, gateway, priority, schedule);
	}

	@RequestMapping(value = "/sendMultiple", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<JsonNode> retrieveMultiplePOST(
			@RequestParam(name = "receivers") String phoneNumbers,
			@RequestParam(name = "sender") String senderName,
			@RequestParam(name = "text") String messageBody,
			@RequestParam(name = "gateway", required = false, defaultValue = "auto") String gateway,
			@RequestParam(name = "priority", required = false, defaultValue = "normal") String priority,
			@RequestParam(name = "schedule", required = false, defaultValue = "now") String schedule
	) {

		MultipleAcceptStatus rejectedStatus = new MultipleAcceptStatus();
		MultipleAcceptStatus acceptedStatus = new MultipleAcceptStatus();

		String[] phoneNumberList = phoneNumbers.split(";");

		// remove duplicates
		phoneNumberList = Arrays.stream(phoneNumberList).distinct().toArray(String[]::new);

		for(String phoneNumber : phoneNumberList) {
			try {
				this.action(phoneNumber, senderName, messageBody, gateway, priority, schedule);
				acceptedStatus.addNewPhone(phoneNumber);
			} catch (HttpInternalServerErrorException|HttpBadRequestException e) {
				if (!e.getCode().equals(ErrorList.E0x00000f01.getErrorCode())) {
					throw new HttpBadRequestException(ErrorList.getErrorFromCode(e.getCode()));
				}
				rejectedStatus.addNewPhone(phoneNumber);
			}
		}

		HashMap<String, Object> response = new HashMap<>();
		response.put("sender", senderName);
		response.put("gateway", gateway);
		response.put("rejected", rejectedStatus);
		response.put("accepted", acceptedStatus);

		return this.respondWithData(200, response);
	}

	private ResponseEntity<JsonNode> action(String phoneNumber, String senderName, String messageBody, String gateway, String priority, String schedule) {
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

		MessagePriority messagePriority = CheckPriority.parse(priority);

		MessageSchedule messageSchedule = new MessageSchedule("now");
		try {
			if (!schedule.equals("now")) {
				messageSchedule = new MessageSchedule(schedule);
			}
		} catch (DateTimeParseException e) {
			logger.warn("Incorrect date time format for schedule provided, using current time", e);
		}

		try {
			SmsAcceptStatus responseData = Producer.send(receiver, sender, body, gateway, messagePriority, messageSchedule);
			logger.info("SMS accepted: From [" + sender.getName() + "] to [" + receiver.getNumber() + "] via [" + gateway + "] priority [" + messagePriority.getPriorityText() + "] on [" + messageSchedule.getDateTime() + "]");

			HashMap<String, Object> response = new HashMap<>();
			response.put("sms", responseData);
			return this.respondWithData(200, response);
		} catch (CouldNotAcceptSmsException e) {
			logger.error("Could not accept sms: " + e.getMessage());
			throw new HttpInternalServerErrorException(ErrorList.E0x00000f08);
		}
	}

	private static class MultipleAcceptStatus {
		private int count;
		private final ArrayList<String> phoneNumbers;

		public MultipleAcceptStatus() {
			this.phoneNumbers = new ArrayList<>();
			this.count = 0;
		}

		void addNewPhone(String phoneNumber) {
			this.phoneNumbers.add(phoneNumber);
			this.count++;
		}

		public int getCount() {
			return count;
		}

		public ArrayList<String> getPhoneNumbers() {
			return phoneNumbers;
		}
	}
}
