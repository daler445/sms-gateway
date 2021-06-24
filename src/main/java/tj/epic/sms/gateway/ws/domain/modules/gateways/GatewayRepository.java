package tj.epic.sms.gateway.ws.domain.modules.gateways;

import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.*;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.Session;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import tj.epic.sms.gateway.ws.domain.exceptions.gateway.smpp.BindFailedException;
import tj.epic.sms.gateway.ws.domain.exceptions.gateway.smpp.SmsSendingFailedException;
import tj.epic.sms.gateway.ws.domain.modules.sms.body.MessageBody;
import tj.epic.sms.gateway.ws.domain.modules.sms.priority.MessagePriority;
import tj.epic.sms.gateway.ws.domain.modules.sms.receiver.Receiver;
import tj.epic.sms.gateway.ws.domain.modules.sms.schedule.MessageSchedule;
import tj.epic.sms.gateway.ws.domain.modules.sms.sender.Sender;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

public interface GatewayRepository {
	void bind(Config config) throws BindFailedException;
	void sendSms(Receiver receiver, Sender sender, MessageBody messageBody, MessagePriority messagePriority, MessageSchedule messageSchedule, long smsDBItemId) throws SmsSendingFailedException;
	void sendMultipleSms(Receiver[] receivers, Sender sender, MessageBody messageBody, MessagePriority messagePriority, MessageSchedule messageSchedule, long smsDBItemId) throws SmsSendingFailedException;
}
