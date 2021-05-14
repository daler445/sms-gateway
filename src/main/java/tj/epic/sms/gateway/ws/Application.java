package tj.epic.sms.gateway.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tj.epic.sms.gateway.ws.application.configReader.ReadConfig;
import tj.epic.sms.gateway.ws.application.queue.Consumer;
import tj.epic.sms.gateway.ws.domain.modules.gateways.Config;
import tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.SMPPConfig;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

		List<Config> configList = getConfigurations();

		Consumer.buildMain(configList);
		for (Config config : configList) {
			Consumer.build(config);
		}
	}

	private static List<Config> getConfigurations() {
		List<Config> configList = new ArrayList<>();

		List<SMPPConfig> smppConfigs = ReadConfig.SMPP("");
		for (SMPPConfig config : smppConfigs) {
			configList.add(new Config(Config.ConfigType.SMPP, config));
		}

		return configList;
	}

}
