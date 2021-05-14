package tj.epic.sms.gateway.ws.infrastructure.persistent.smpp;

import tj.epic.sms.gateway.ws.domain.exceptions.gateway.smpp.BindFailedException;
import tj.epic.sms.gateway.ws.domain.exceptions.gateway.smpp.SmsSendingFailedException;
import tj.epic.sms.gateway.ws.domain.modules.gateways.Config;
import tj.epic.sms.gateway.ws.domain.modules.gateways.GatewayRepository;
import tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.SMPPConfig;
import tj.epic.sms.gateway.ws.domain.modules.sms.Body.MessageBody;
import tj.epic.sms.gateway.ws.domain.modules.sms.Receiver.Receiver;
import tj.epic.sms.gateway.ws.domain.modules.sms.Sender.Sender;

import java.util.concurrent.TimeUnit;

public class InNetworkSmppRepository implements GatewayRepository {
	@Override
	public void bind(Config config) throws BindFailedException {
		System.out.println("Bind successful");
	}

	@Override
	public void sendSms(Receiver receiver, Sender sender, MessageBody messageBody) throws SmsSendingFailedException {
		try {
			TimeUnit.SECONDS.sleep(1);
			System.out.println("SMS was sent to " + receiver.getRawValue() + " from " + sender.getName() + " successfully");
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("SMS was not sent to " + receiver.getRawValue() + " due to timeunit interruption");
		}
	}
}
