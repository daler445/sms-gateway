package tj.epic.sms.gateway.ws.infrastructure.persistent.smpp;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.*;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.*;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.jsmpp.util.TimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tj.epic.sms.gateway.ws.application.common.Helpers;
import tj.epic.sms.gateway.ws.application.database.DatabaseOperations;
import tj.epic.sms.gateway.ws.domain.exceptions.gateway.smpp.BindFailedException;
import tj.epic.sms.gateway.ws.domain.exceptions.gateway.smpp.SmsSendingFailedException;
import tj.epic.sms.gateway.ws.domain.modules.gateways.Config;
import tj.epic.sms.gateway.ws.domain.modules.gateways.GatewayRepository;
import tj.epic.sms.gateway.ws.domain.modules.sms.body.MessageBody;
import tj.epic.sms.gateway.ws.domain.modules.sms.priority.MessagePriority;
import tj.epic.sms.gateway.ws.domain.modules.sms.receiver.Receiver;
import tj.epic.sms.gateway.ws.domain.modules.sms.schedule.MessageSchedule;
import tj.epic.sms.gateway.ws.domain.modules.sms.sender.Sender;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class InNetworkSmppRepository implements GatewayRepository {
	public Logger logger = LoggerFactory.getLogger(GatewayRepository.class);
	private SMPPSession session;
	private boolean isBound = false;
	private static final TimeFormatter TIME_FORMATTER = new AbsoluteTimeFormatter();

	private Config config;

	@Override
	public void bind(Config config) throws BindFailedException {
		if (config.getConfigType().equals(Config.ConfigType.SMPP)) {
			this.config = config;
			session = new SMPPSession();
			try {
				String systemId = session.connectAndBind(
						this.config.getSmppConfig().getHost(),
						this.config.getSmppConfig().getPort(),
						new BindParameter(
								this.config.getSmppConfig().getBindType().get(),
								this.config.getSmppConfig().getLogin(),
								this.config.getSmppConfig().getPassword(),
								"EPIC_SG",
								this.config.getSmppConfig().getDestinationAddrTon().get(),
								this.config.getSmppConfig().getDestinationAddrNpi().get(),
								null
						)
				);
				logger.info("Bind successful with login [" + systemId + "] for [" + this.config.getSmppConfig().getAlias() + "]");
				isBound = true;
			} catch (IOException e) {
				logger.error("Bound failed for host " + this.config.getSmppConfig().getHost() + " as \"" + this.config.getSmppConfig().getLogin() + "\": " + e.getMessage());
				throw new BindFailedException();
			}
		}
	}

	@Override
	public void sendSms(Receiver receiver, Sender sender, MessageBody messageBody, MessagePriority messagePriority, MessageSchedule messageSchedule, long smsDBItemId) throws SmsSendingFailedException {
		if (isBound && session.getSessionState().isBound()) {
			String messageId = null;
			try {

				session.setMessageReceiverListener(new MessageReceiverListener() {
					@Override
					public void onAcceptDeliverSm(DeliverSm deliverSm) {
						if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {
							try {
								DeliveryReceipt delReceipt = deliverSm.getShortMessageAsDeliveryReceipt();
								long id = Long.parseLong(delReceipt.getId());
								String messageId = Long.toString(id, 16).toUpperCase();

								logger.info("Receiving delivery receipt for message '" + messageId + "' from " + deliverSm.getSourceAddr() + " to " + deliverSm.getDestAddress() + ": " + delReceipt);
							} catch (InvalidDeliveryReceiptException e) {
								logger.warn("Failed getting delivery receipt: " + e.getMessage());
							}
						}
					}

					@Override
					public void onAcceptAlertNotification(AlertNotification alertNotification) {

					}

					@Override
					public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) {
						return null;
					}
				});

				Date scheduleDate = new Date();
				if (messageSchedule.isScheduleSending()) {
					Instant instant = messageSchedule.getDateTimeObj().atZone(ZoneId.systemDefault()).toInstant();
					scheduleDate = Date.from(instant);
				}

				int sequenceSize = 254; // utf 8
				boolean containsUnicodeCharacters = containsUnicodeCharacters(messageBody.getBody());

				if (containsUnicodeCharacters) { // utf-16
					sequenceSize = 254 / 8;
				}

				int segmentCount = 1;
				if (messageBody.getBody().length() > sequenceSize) {
					segmentCount = (messageBody.getBody().length() / sequenceSize) + 1;
				}

				if (segmentCount == 1) {
					//boolean containsUnicodeCharacters = containsUnicodeCharacters(messageBody.getBody());
					byte[] messageBodyBytes;
					if (containsUnicodeCharacters) {
						messageBodyBytes = messageBody.getBody().getBytes(StandardCharsets.UTF_16);
					} else {
						messageBodyBytes = messageBody.getBody().getBytes(StandardCharsets.UTF_8);
					}

					messageId = submitSingleOneSequence(
							session,
							"",
							this.config.getSmppConfig().getSourceAddrTon().get(),
							this.config.getSmppConfig().getSourceAddrNpi().get(),
							sender.getName(),
							this.config.getSmppConfig().getDestinationAddrTon().get(),
							this.config.getSmppConfig().getDestinationAddrNpi().get(),
							receiver.getNumber(),
							new ESMClass(
									this.config.getSmppConfig().getESMMessageMode().get(),
									this.config.getSmppConfig().getESMMessageType().get(),
									this.config.getSmppConfig().getESMGSMSpecificFeature().get()
							),
							(byte) this.config.getSmppConfig().getProtocolId(), // protocol id
							(byte) messagePriority.getPriorityCode(), // priority
							(messageSchedule.isScheduleSending()) ? TIME_FORMATTER.format(scheduleDate) : null,
							new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
							(this.config.getSmppConfig().isReplacePending()) ? (byte) 1 : (byte) 0, // replace if presents flag
							new GeneralDataCoding(
									(containsUnicodeCharacters) ? Alphabet.ALPHA_UCS2 : Alphabet.ALPHA_DEFAULT
							),
							(byte) 0, // sms default msg id
							messageBodyBytes
					);
				} else {
					Random random = new Random();
					short seqRefNum = (short) random.nextInt();

					OptionalParameter sarMsgRefNum = OptionalParameters.newSarMsgRefNum(seqRefNum);
					OptionalParameter sarTotalSegments = OptionalParameters.newSarTotalSegments(segmentCount);

					String[] segmentParts = Iterables.toArray(
							Splitter
									.fixedLength(sequenceSize)
									.split(messageBody.getBody()),
							String.class
					);

					for (int i = 0; i < segmentCount; i++) {
						logger.debug("Segment part " + i + " [Segment " + seqRefNum + "]: " + segmentParts[i]);
						//boolean containsUnicodeCharacters = containsUnicodeCharacters(segmentParts[i]);
						byte[] messageBodyBytes;
						if (containsUnicodeCharacters) {
							messageBodyBytes = segmentParts[i].getBytes(StandardCharsets.UTF_16);
						} else {
							messageBodyBytes = segmentParts[i].getBytes(StandardCharsets.UTF_8);
						}

						OptionalParameter sarSegmentSeqNum = OptionalParameters.newSarSegmentSeqnum(i + 1);
						messageId = submitSingleMultipleSequence(
								session,
								"",
								this.config.getSmppConfig().getSourceAddrTon().get(),
								this.config.getSmppConfig().getSourceAddrNpi().get(),
								sender.getName(),
								this.config.getSmppConfig().getDestinationAddrTon().get(),
								this.config.getSmppConfig().getDestinationAddrNpi().get(),
								receiver.getNumber(),
								new ESMClass(
										this.config.getSmppConfig().getESMMessageMode().get(),
										this.config.getSmppConfig().getESMMessageType().get(),
										this.config.getSmppConfig().getESMGSMSpecificFeature().get()
								),
								(byte) this.config.getSmppConfig().getProtocolId(), // protocol id
								(byte) messagePriority.getPriorityCode(), // priority
								(messageSchedule.isScheduleSending()) ? TIME_FORMATTER.format(scheduleDate) : null,
								new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
								(this.config.getSmppConfig().isReplacePending()) ? (byte) 1 : (byte) 0, // replace if presents flag
								new GeneralDataCoding(
										(containsUnicodeCharacters) ? Alphabet.ALPHA_UCS2 : Alphabet.ALPHA_DEFAULT
								),
								(byte) 0, // sms default msg id
								messageBodyBytes,
								sarMsgRefNum,
								sarSegmentSeqNum,
								sarTotalSegments
						);
					}
				}

					/*messageId = session.submitShortMessage(
							"",
							this.config.getSmppConfig().getSourceAddrTon().get(),
							this.config.getSmppConfig().getSourceAddrNpi().get(),
							sender.getName(),
							this.config.getSmppConfig().getDestinationAddrTon().get(),
							this.config.getSmppConfig().getDestinationAddrNpi().get(),
							receiver.getNumber(),
							new ESMClass(
									this.config.getSmppConfig().getESMMessageMode().get(),
									this.config.getSmppConfig().getESMMessageType().get(),
									this.config.getSmppConfig().getESMGSMSpecificFeature().get()
							),
							(byte) this.config.getSmppConfig().getProtocolId(), // protocol id
							(byte) messagePriority.getPriorityCode(), // priority
							(messageSchedule.isScheduleSending()) ? TIME_FORMATTER.format(scheduleDate) : null,
							//null, //TIME_FORMATTER.format(scheduleDeliveryTime),

							null,
							new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE),
							(this.config.getSmppConfig().isReplacePending()) ? (byte) 1 : (byte) 0, // replace if presents flag
							new GeneralDataCoding(
									(containsUnicodeCharacters) ? Alphabet.ALPHA_UCS2 : Alphabet.ALPHA_DEFAULT
							),
							(byte) 0, // sms default msg id
							messageBodyBytes,
							sarMsgRefNum,
							sarSegmentSeqNum,
							sarTotalSegments
					);*/
			} catch (IOException | InvalidResponseException | NegativeResponseException | ResponseTimeoutException | PDUException e) {
				logger.error("SMS sending failed: " + e.getMessage());
				updateSMSStatusInDatabase(smsDBItemId, "failed", "");
				throw new SmsSendingFailedException();
			}
			if (messageId == null) {
				logger.error("Failed to send sms, unexpected response");
				updateSMSStatusInDatabase(smsDBItemId, "failed", "");
				throw new SmsSendingFailedException();
			}
			updateSMSStatusInDatabase(smsDBItemId, "sent", messageId);
			logger.info("SMS sent, ID: " + messageId);
		} else {
			logger.error("Not bound");
			throw new SmsSendingFailedException();
		}
	}

	@Override
	public void sendMultipleSms(Receiver[] receivers, Sender sender, MessageBody messageBody, MessagePriority messagePriority, MessageSchedule messageSchedule, long smsDBItemId) throws SmsSendingFailedException {
		if (isBound && session.getSessionState().isBound()) {
			String messageId = null;

			try {
				session.setMessageReceiverListener(new MessageReceiverListener() {
					@Override
					public void onAcceptDeliverSm(DeliverSm deliverSm) {
						if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {
							try {
								DeliveryReceipt delReceipt = deliverSm.getShortMessageAsDeliveryReceipt();
								long id = Long.parseLong(delReceipt.getId());
								String messageId = Long.toString(id, 16).toUpperCase();

								logger.info("Receiving delivery receipt for message '" + messageId + "' from " + deliverSm.getSourceAddr() + " to " + deliverSm.getDestAddress() + ": " + delReceipt);
							} catch (InvalidDeliveryReceiptException e) {
								//logger.warn("Failed getting delivery receipt: " + e.getMessage());
							}
						}
					}

					@Override
					public void onAcceptAlertNotification(AlertNotification alertNotification) {

					}

					@Override
					public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) {
						return null;
					}
				});


				Date scheduleDate = new Date();
				if (messageSchedule.isScheduleSending()) {
					Instant instant = messageSchedule.getDateTimeObj().atZone(ZoneId.systemDefault()).toInstant();
					scheduleDate = Date.from(instant);
				}

				int sequenceSize = 254; // utf 8
				boolean containsUnicodeCharacters = containsUnicodeCharacters(messageBody.getBody());

				if (containsUnicodeCharacters) { // utf-16
					sequenceSize = 254 / 4;
				}

				int segmentCount = 1;
				if (messageBody.getBody().length() > sequenceSize) {
					segmentCount = (messageBody.getBody().length() / sequenceSize) + 1;
				}

				String[] receiverArray = new String[receivers.length];
				int i = 0;
				for (Receiver receiver : receivers) {
					receiverArray[i] = receiver.getNumber();
					i++;
				}

				if (segmentCount == 1) {
					//boolean containsUnicodeCharacters = containsUnicodeCharacters(messageBody.getBody());
					byte[] messageBodyBytes;
					if (containsUnicodeCharacters) {
						messageBodyBytes = messageBody.getBody().getBytes(StandardCharsets.UTF_16);
					} else {
						messageBodyBytes = messageBody.getBody().getBytes(StandardCharsets.UTF_8);
					}


					messageId = submitMultipleOneSequence(session,
							"",
							this.config.getSmppConfig().getSourceAddrTon().get(),
							this.config.getSmppConfig().getSourceAddrNpi().get(),
							sender.getName(),
							this.config.getSmppConfig().getDestinationAddrTon().get(),
							this.config.getSmppConfig().getDestinationAddrNpi().get(),
							receiverArray,
							new ESMClass(
									this.config.getSmppConfig().getESMMessageMode().get(),
									this.config.getSmppConfig().getESMMessageType().get(),
									this.config.getSmppConfig().getESMGSMSpecificFeature().get()
							),
							(byte) this.config.getSmppConfig().getProtocolId(), // protocol id
							(byte) messagePriority.getPriorityCode(), // priority
							(messageSchedule.isScheduleSending()) ? TIME_FORMATTER.format(scheduleDate) : null,
							new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
							(this.config.getSmppConfig().isReplacePending()) ? (byte) 1 : (byte) 0, // replace if presents flag
							new GeneralDataCoding(
									(containsUnicodeCharacters) ? Alphabet.ALPHA_UCS2 : Alphabet.ALPHA_DEFAULT
							),
							(byte) 0, // sms default msg id
							messageBodyBytes);

				} else {
					Random random = new Random();
					short seqRefNum = (short) random.nextInt();

					OptionalParameter sarMsgRefNum = OptionalParameters.newSarMsgRefNum(seqRefNum);
					OptionalParameter sarTotalSegments = OptionalParameters.newSarTotalSegments(segmentCount);

					String[] segmentParts = Iterables.toArray(
							Splitter
									.fixedLength(sequenceSize)
									.split(messageBody.getBody()),
							String.class
					);

					for (int k = 0; k < segmentCount; k++) {
						logger.debug("Segment part " + k + " [Segment " + seqRefNum + "]: " + segmentParts[k]);
						//boolean containsUnicodeCharacters = containsUnicodeCharacters(segmentParts[i]);
						byte[] messageBodyBytes;
						if (containsUnicodeCharacters) {
							messageBodyBytes = segmentParts[k].getBytes(StandardCharsets.UTF_16);
						} else {
							messageBodyBytes = segmentParts[k].getBytes(StandardCharsets.UTF_8);
						}

						OptionalParameter sarSegmentSeqNum = OptionalParameters.newSarSegmentSeqnum(k + 1);

						messageId = submitMultipleMultipleSequence(session,
								"",
								this.config.getSmppConfig().getSourceAddrTon().get(),
								this.config.getSmppConfig().getSourceAddrNpi().get(),
								sender.getName(),
								this.config.getSmppConfig().getDestinationAddrTon().get(),
								this.config.getSmppConfig().getDestinationAddrNpi().get(),
								receiverArray,
								new ESMClass(
										this.config.getSmppConfig().getESMMessageMode().get(),
										this.config.getSmppConfig().getESMMessageType().get(),
										this.config.getSmppConfig().getESMGSMSpecificFeature().get()
								),
								(byte) this.config.getSmppConfig().getProtocolId(), // protocol id
								(byte) messagePriority.getPriorityCode(), // priority
								(messageSchedule.isScheduleSending()) ? TIME_FORMATTER.format(scheduleDate) : null,
								new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
								(this.config.getSmppConfig().isReplacePending()) ? (byte) 1 : (byte) 0, // replace if presents flag
								new GeneralDataCoding(
										(containsUnicodeCharacters) ? Alphabet.ALPHA_UCS2 : Alphabet.ALPHA_DEFAULT
								),
								(byte) 0, // sms default msg id
								messageBodyBytes,
								sarMsgRefNum,
								sarSegmentSeqNum,
								sarTotalSegments);
					}
				}
				if (messageId == null) {
					logger.error("Failed to send sms, unexpected response");
					updateSMSStatusInDatabase(smsDBItemId, "failed", "");
					throw new SmsSendingFailedException();
				}
			} catch (IOException | InvalidResponseException | NegativeResponseException | ResponseTimeoutException | PDUException e) {
				logger.error("SMS sending failed: " + e.getMessage());
				updateSMSStatusInDatabase(smsDBItemId, "failed", "");
				throw new SmsSendingFailedException();
			}

			updateSMSStatusInDatabase(smsDBItemId, "sent", messageId);
			logger.info("SMS sent, ID: " + messageId);
		} else {
			logger.error("Not bound");
			throw new SmsSendingFailedException();
		}
	}

	private String submitSingleOneSequence(SMPPSession session, String serviceType, TypeOfNumber sTON, NumberingPlanIndicator sNPI, String sender, TypeOfNumber dTON, NumberingPlanIndicator dNPI, String receiver, ESMClass emsClass, byte protocolId, byte priorityCode, String scheduleDate, RegisteredDelivery registeredDelivery, byte replacePending, GeneralDataCoding generalDataCoding, byte defaultMsgId, byte[] body) throws PDUException, IOException, InvalidResponseException, NegativeResponseException, ResponseTimeoutException {
		return session.submitShortMessage(
				serviceType,
				sTON,
				sNPI,
				sender,
				dTON,
				dNPI,
				receiver,
				emsClass,
				protocolId, // protocol id
				priorityCode, // priority
				scheduleDate,
				null,
				registeredDelivery,
				replacePending, // replace if presents flag
				generalDataCoding,
				defaultMsgId, // sms default msg id
				body
		);
	}

	private String submitSingleMultipleSequence(SMPPSession session, String serviceType, TypeOfNumber sTON, NumberingPlanIndicator sNPI, String sender, TypeOfNumber dTON, NumberingPlanIndicator dNPI, String receiver, ESMClass emsClass, byte protocolId, byte priorityCode, String scheduleDate, RegisteredDelivery registeredDelivery, byte replacePending, GeneralDataCoding generalDataCoding, byte defaultMsgId, byte[] body, OptionalParameter sarMsgRefNum, OptionalParameter sarSegmentSeqNum, OptionalParameter sarTotalSegments) throws PDUException, IOException, InvalidResponseException, NegativeResponseException, ResponseTimeoutException {
		return session.submitShortMessage(
				serviceType,
				sTON,
				sNPI,
				sender,
				dTON,
				dNPI,
				receiver,
				emsClass,
				protocolId, // protocol id
				priorityCode, // priority
				scheduleDate,
				null,
				registeredDelivery,
				replacePending, // replace if presents flag
				generalDataCoding,
				defaultMsgId, // sms default msg id
				body,
				sarMsgRefNum,
				sarSegmentSeqNum,
				sarTotalSegments
		);
	}

	private String submitMultipleOneSequence(SMPPSession session, String serviceType, TypeOfNumber sTON, NumberingPlanIndicator sNPI, String sender, TypeOfNumber dTON, NumberingPlanIndicator dNPI, String[] receivers, ESMClass emsClass, byte protocolId, byte priorityCode, String scheduleDate, RegisteredDelivery registeredDelivery, byte replacePending, GeneralDataCoding generalDataCoding, byte defaultMsgId, byte[] body) throws PDUException, IOException, InvalidResponseException, NegativeResponseException, ResponseTimeoutException {

		Address[] receiverList = new Address[receivers.length];
		for (int i = 0; i < receivers.length; i++) {
			receiverList[i] = new Address(dTON, dNPI, receivers[i]);
		}

		session.setTransactionTimer(10000);

		SubmitMultiResult result = session.submitMultiple(
				serviceType,
				sTON,
				sNPI,
				sender,
				receiverList,
				emsClass,
				protocolId, // protocol id
				priorityCode, // priority
				scheduleDate,
				null,
				registeredDelivery,
				new ReplaceIfPresentFlag(replacePending), // replace if presents flag
				generalDataCoding,
				defaultMsgId, // sms default msg id
				body
		);
		return result.getMessageId();
	}

	private String submitMultipleMultipleSequence(SMPPSession session, String serviceType, TypeOfNumber sTON, NumberingPlanIndicator sNPI, String sender, TypeOfNumber dTON, NumberingPlanIndicator dNPI, String[] receivers, ESMClass emsClass, byte protocolId, byte priorityCode, String scheduleDate, RegisteredDelivery registeredDelivery, byte replacePending, GeneralDataCoding generalDataCoding, byte defaultMsgId, byte[] body, OptionalParameter sarMsgRefNum, OptionalParameter sarSegmentSeqNum, OptionalParameter sarTotalSegments) throws PDUException, IOException, InvalidResponseException, NegativeResponseException, ResponseTimeoutException {

		Address[] receiverList = new Address[receivers.length];
		for (int i = 0; i < receivers.length; i++) {
			receiverList[i + 1] = new Address(dTON, dNPI, receivers[i]);
		}

		SubmitMultiResult result = session.submitMultiple(
				serviceType,
				sTON,
				sNPI,
				sender,
				receiverList,
				emsClass,
				protocolId, // protocol id
				priorityCode, // priority
				scheduleDate,
				null,
				registeredDelivery,
				new ReplaceIfPresentFlag(replacePending), // replace if presents flag
				generalDataCoding,
				defaultMsgId, // sms default msg id
				body,
				sarMsgRefNum,
				sarSegmentSeqNum,
				sarTotalSegments
		);
		return result.getMessageId();
	}

	private void updateSMSStatusInDatabase(long smsDBItemId, String status, String smsId) {
		try {
			DatabaseOperations.updateSMSStatus(smsDBItemId, status, smsId);
		} catch (SQLException e) {
			logger.error("Failed updating SMS status in DB", e);
		}
	}

	private boolean containsUnicodeCharacters(String needle) {
		for (int i = 0; i < needle.length(); i++) {
			int c = (int) needle.charAt(i);
			if (c < 32 || c > 126)
				return true;
		}
		return false;
	}
}
