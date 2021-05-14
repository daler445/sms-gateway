package tj.epic.sms.gateway.ws.domain.modules.gateways;

import tj.epic.sms.gateway.ws.domain.exceptions.gateway.smpp.BindFailedException;
import tj.epic.sms.gateway.ws.domain.exceptions.gateway.smpp.SmsSendingFailedException;
import tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.SMPPConfig;
import tj.epic.sms.gateway.ws.domain.modules.sms.Body.MessageBody;
import tj.epic.sms.gateway.ws.domain.modules.sms.Receiver.Receiver;
import tj.epic.sms.gateway.ws.domain.modules.sms.Sender.Sender;

public interface GatewayRepository {
	void bind(Config config) throws BindFailedException;
	void sendSms(Receiver receiver, Sender sender, MessageBody messageBody) throws SmsSendingFailedException;
}
