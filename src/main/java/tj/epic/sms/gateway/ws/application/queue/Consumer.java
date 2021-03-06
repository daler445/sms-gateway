package tj.epic.sms.gateway.ws.application.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tj.epic.sms.gateway.ws.domain.exceptions.gateway.smpp.BindFailedException;
import tj.epic.sms.gateway.ws.domain.exceptions.gateway.smpp.SmsSendingFailedException;
import tj.epic.sms.gateway.ws.domain.modules.gateways.Config;
import tj.epic.sms.gateway.ws.domain.modules.gateways.GatewayRepository;
import tj.epic.sms.gateway.ws.domain.modules.sms.GatewayTypes;
import tj.epic.sms.gateway.ws.domain.modules.sms.MessageBundle;
import tj.epic.sms.gateway.ws.infrastructure.persistent.smpp.InNetworkSmppRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Consumer {
	public static Logger logger = LoggerFactory.getLogger(Consumer.class);
	public static final String GLOBAL_QUEUE_NAME_LOCAL = "aster";
	public static final String GLOBAL_QUEUE_NAME_EXTERNAL = "cody";

	public static void build(Config config) throws BindFailedException {
		try {
			declareQueue(config, "");
		} catch (BindFailedException e) {
			logger.error("Could not bind [" + getQueueName(config) + "]");
			throw new BindFailedException();
		} catch (Exception e) {
			logger.error("Could not bind [" + getQueueName(config) + "]", e);
			throw new BindFailedException();
		}
	}

	public static void buildMain(List<Config> configList) {
		logger.info("Building main consumer");

		for (Config config : configList) {

			GatewayTypes type = getGatewayType(config);
			try {
				if (type.equals(GatewayTypes.Local) || type.equals(GatewayTypes.Both)) {
					declareQueue(config, GLOBAL_QUEUE_NAME_LOCAL);
				}
				if (type.equals(GatewayTypes.External) || type.equals(GatewayTypes.Both)) {
					declareQueue(config, GLOBAL_QUEUE_NAME_EXTERNAL);
				}
			} catch (BindFailedException e) {
				logger.error("Could not bind [" + getQueueName(config) + "]");
			} catch (Exception e) {
				logger.error("Could not bind [" + getQueueName(config) + "]", e);
			}
		}
	}

	private static void declareQueue(Config config, String queueName) throws BindFailedException, Exception {
		String finalQueueName;
		if (queueName.equals("")) {
			finalQueueName = getQueueName(config);
		} else {
			finalQueueName = queueName;
		}
		logger.info("Building queue [" + finalQueueName + "] for " + getAlias(config));

		GatewayRepository gatewayRepository = new InNetworkSmppRepository();

		try {
			gatewayRepository.bind(config);
		} catch (Exception e) {
			throw new BindFailedException();
		}

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");

		Connection connection;
		try {
			connection = factory.newConnection();

			Channel channel = connection.createChannel();

			channel.basicQos(getPreFetchCount(config));

			channel.queueDeclare(
					finalQueueName,
					false,
					false,
					false,
					null
			);

			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

				ObjectMapper mapper = new ObjectMapper();
				MessageBundle bundle = mapper.readValue(message, MessageBundle.class);

				if (bundle.isMultiple()) {
					logger.info("[" + finalQueueName + "] [" + getAlias(config) + "] Received message, to '" + Arrays.toString(bundle.getReceivers()) + "', from '" + bundle.getSender().getName() + "', priority " + bundle.getMessagePriority().getPriorityText());

					try {
						gatewayRepository.sendMultipleSms(bundle.getReceivers(), bundle.getSender(), bundle.getMessageBody(), bundle.getMessagePriority(), bundle.getMessageSchedule(), bundle.getSmsId());
					} catch (SmsSendingFailedException e) {
						e.printStackTrace();
					}
				} else {
					logger.info("[" + finalQueueName + "] [" + getAlias(config) + "] Received message, to '" + bundle.getReceiver().getRawValue() + "', from '" + bundle.getSender().getName() + "', priority " + bundle.getMessagePriority().getPriorityText());

					try {
						gatewayRepository.sendSms(bundle.getReceiver(), bundle.getSender(), bundle.getMessageBody(), bundle.getMessagePriority(), bundle.getMessageSchedule(), bundle.getSmsId());
					} catch (SmsSendingFailedException e) {
						e.printStackTrace();
					}
				}
			};

			CancelCallback cancelCallback = (consumerTag) -> logger.info("[" + finalQueueName + "] Cancelled sending message");

			channel.basicConsume(
					finalQueueName,
					true,
					deliverCallback,
					cancelCallback
			);
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private static String getQueueName(Config config) {
		if (config.getConfigType().equals(Config.ConfigType.SMPP)) {
			return config.getSmppConfig().getQueueName();
		}
		return "queue";
	}

	private static int getPreFetchCount(Config config) {
		if (config.getConfigType().equals(Config.ConfigType.SMPP)) {
			return config.getSmppConfig().getPreFetchCount();
		}
		return 0;
	}

	private static GatewayTypes getGatewayType(Config config) {
		if (config.getConfigType().equals(Config.ConfigType.SMPP)) {
			return config.getSmppConfig().getGatewayType();
		}
		return GatewayTypes.External;
	}

	private static String getAlias(Config config) {
		if (config.getConfigType().equals(Config.ConfigType.SMPP)) {
			return config.getSmppConfig().getAlias();
		}
		return "Undefined";
	}

}
