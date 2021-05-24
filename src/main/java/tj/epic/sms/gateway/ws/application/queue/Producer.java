package tj.epic.sms.gateway.ws.application.queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tj.epic.sms.gateway.ws.application.common.Helpers;
import tj.epic.sms.gateway.ws.domain.exceptions.sms.CouldNotAcceptSmsException;
import tj.epic.sms.gateway.ws.domain.modules.sms.SmsAcceptStatus;
import tj.epic.sms.gateway.ws.domain.modules.sms.Body.MessageBody;
import tj.epic.sms.gateway.ws.domain.modules.sms.MessageBundle;
import tj.epic.sms.gateway.ws.domain.modules.sms.Receiver.Receiver;
import tj.epic.sms.gateway.ws.domain.modules.sms.Sender.Sender;

public class Producer {
	public static Logger logger = LoggerFactory.getLogger(Producer.class);

	public static SmsAcceptStatus send(Receiver receiver, Sender sender, MessageBody messageBody, String gateway) throws CouldNotAcceptSmsException {
		if (gateway == null || gateway.equals("")) {
			if (!receiver.getCountryCode().equals("992")) {
				gateway = Consumer.GLOBAL_QUEUE_NAME_EXTERNAL;
			} else {
				gateway = Consumer.GLOBAL_QUEUE_NAME_LOCAL;
			}
		}

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try (
				Connection connection = factory.newConnection();
				Channel channel = connection.createChannel()
		) {
			MessageBundle bundle = new MessageBundle(receiver, sender, messageBody);
			String message = Helpers.objectToJson(bundle);

			channel.queueDeclare(gateway, false, false, false, null);
			channel.basicPublish("", gateway, null, message.getBytes());

			logger.debug("Sent " + message);

			return new SmsAcceptStatus(receiver.getRawValue(), sender.getName(), gateway);
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new CouldNotAcceptSmsException();
	}
}
