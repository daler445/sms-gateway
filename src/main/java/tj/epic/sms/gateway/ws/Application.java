package tj.epic.sms.gateway.ws;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tj.epic.sms.gateway.ws.application.configReader.ReadConfig;
import tj.epic.sms.gateway.ws.application.queue.Consumer;
import tj.epic.sms.gateway.ws.domain.exceptions.gateway.smpp.BindFailedException;
import tj.epic.sms.gateway.ws.domain.modules.gateways.Config;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Application implements ApplicationRunner {
	public static Logger logger = LoggerFactory.getLogger(Application.class);
	public static ArrayList<Config> boundList = new ArrayList<>();

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Options options = new Options();

		Option configPathOption = new Option("c", "configPath", true, "Config file path");
		configPathOption.setRequired(false);
		options.addOption(configPathOption);

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args.getSourceArgs());

		String configPath = cmd.getOptionValue("configPath");

		if (configPath == null || configPath.equals("")) {
			logger.warn("Config path not defined, using default config path");
			configPath = "D:/JavaProjects/sms.gateway.ws/config.xml";
		}
		logger.info("Config path: " + configPath);

		List<Config> configList = getConfigurations(configPath);

		if (configList.size() == 0) {
			logger.warn("No gateway configurations available, shutting down");
			System.exit(1);
		}

		logger.debug(configList.size() + " gateway configs available");
		Consumer.buildMain(configList);
		for (Config config : configList) {
			try {
				Consumer.build(config);
				this.boundList.add(config);
			} catch (BindFailedException e) {
				logger.error("Could not bind to gateway");
			}
		}
	}

	private static List<Config> getConfigurations(String configUrl) {
		ReadConfig readConfig = new ReadConfig();

		return readConfig.SMPP(configUrl);
	}
}
