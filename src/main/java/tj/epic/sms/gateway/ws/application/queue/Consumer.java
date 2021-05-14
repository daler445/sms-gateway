package tj.epic.sms.gateway.ws.application.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import tj.epic.sms.gateway.ws.domain.exceptions.gateway.smpp.BindFailedException;
import tj.epic.sms.gateway.ws.domain.exceptions.gateway.smpp.SmsSendingFailedException;
import tj.epic.sms.gateway.ws.domain.modules.gateways.Config;
import tj.epic.sms.gateway.ws.domain.modules.gateways.GatewayRepository;
import tj.epic.sms.gateway.ws.domain.modules.sms.GatewayTypes;
import tj.epic.sms.gateway.ws.domain.modules.sms.MessageBundle;
import tj.epic.sms.gateway.ws.infrastructure.persistent.smpp.InNetworkSmppRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Consumer {
	public static final String GLOBAL_QUEUE_NAME_LOCAL = "aster";
	public static final String GLOBAL_QUEUE_NAME_EXTERNAL = "cody";

	public static void build(Config config) {
		try {
			declareQueue(config, "");
		} catch (BindFailedException e) {
			System.out.println("Bind failed for " + getQueueName(config));
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*System.out.println("Building consumer");

		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.basicQos(config.getSmppConfig().getPreFetch());

			channel.queueDeclare(getQueueName(config), false, false, false, null);

			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
				System.out.println(" [x] Received '" + message + "'");
			};

			CancelCallback cancelCallback = (consumerTag) -> {
				System.out.println(" [e][" + config.getSmppConfig().getQueueName() + "] Failed to send message '" + consumerTag + "'");
			};

			channel.basicConsume(getQueueName(config), true, deliverCallback, cancelCallback);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	public static void buildMain(List<Config> configList) {
		System.out.println("Building main consumer...");

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
				System.out.println("Bind failed for " + getQueueName(config));
			} catch (Exception e) {
				e.printStackTrace();
			}
			/*
			GatewayRepository gatewayRepository = new InNetworkSmppRepository();
			try {
				gatewayRepository.bind(config);
			} catch (BindFailedException e) {
				e.printStackTrace();
				continue;
			}
			activeConnections++;

			System.out.println("Building main queue for " + config.getSmppConfig().getQueueName());
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			try {
				Connection connection = factory.newConnection();
				Channel channel = connection.createChannel();

				channel.basicQos(config.getSmppConfig().getPreFetch());

				channel.queueDeclare(
						(config.getSmppConfig().getType().equals(GatewayTypes.Local)) ? GLOBAL_QUEUE_NAME_LOCAL : GLOBAL_QUEUE_NAME_EXTERNAL,
						false,
						false,
						false,
						null
				);

				DeliverCallback deliverCallback = (consumerTag, delivery) -> {
					String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

					ObjectMapper mapper = new ObjectMapper();
					MessageBundle bundle = mapper.readValue(message, MessageBundle.class);

					System.out.println(" [X][" + config.getSmppConfig().getQueueName() + "] Received message, should send to '" + bundle.getReceiver().getRawValue() + "' from " + bundle.getSender().getName());
					try {
						gatewayRepository.sendSms(bundle.getReceiver(), bundle.getSender(), bundle.getMessageBody());
					} catch (SmsSendingFailedException e) {
						e.printStackTrace();
					}
				};

				CancelCallback cancelCallback = (consumerTag) -> {
					System.out.println(" [E][" + config.getSmppConfig().getQueueName() + "] Failed to send message '" + consumerTag + "'");
				};

				channel.basicConsume(
						(config.getSmppConfig().getType().equals(GatewayTypes.Local)) ? GLOBAL_QUEUE_NAME_LOCAL : GLOBAL_QUEUE_NAME_EXTERNAL,
						true,
						deliverCallback,
						cancelCallback
				);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
		}
	}

	private static void declareQueue(Config config, String queueName) throws BindFailedException, Exception {
		String finalQueueName;
		if (queueName.equals("")) {
			finalQueueName = getQueueName(config);
		} else {
			finalQueueName = queueName;
		}
		System.out.println(" [I] Building queue [" + finalQueueName + "] for " + getAlias(config));

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

				System.out.println(" [x] [" + finalQueueName + "] [" + getAlias(config) + "] Received message, to '" + bundle.getReceiver().getRawValue() + "', from '" + bundle.getSender().getName() + "'");
				try {
					gatewayRepository.sendSms(bundle.getReceiver(), bundle.getSender(), bundle.getMessageBody());
				} catch (SmsSendingFailedException e) {
					e.printStackTrace();
				}
			};

			CancelCallback cancelCallback = (consumerTag) -> System.out.println(" [E][" + finalQueueName + "] Cancelled sending message");

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
			return config.getSmppConfig().getPreFetch();
		}
		return 0;
	}

	private static GatewayTypes getGatewayType(Config config) {
		if (config.getConfigType().equals(Config.ConfigType.SMPP)) {
			return config.getSmppConfig().getType();
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
