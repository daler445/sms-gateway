package tj.epic.sms.gateway.ws.application.configReader;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tj.epic.sms.gateway.ws.Application;
import tj.epic.sms.gateway.ws.domain.modules.gateways.Config;
import tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.SMPPConfig;

import java.awt.geom.IllegalPathStateException;
import java.util.ArrayList;
import java.util.List;

public class ReadConfig {
	public Logger logger = LoggerFactory.getLogger(ReadConfig.class);

	public ArrayList<Config> SMPP(String uri) {
		XMLConfiguration configuration;
		try {
			configuration = this.getConfigs(uri);
		} catch (IllegalPathStateException e) {
			logger.error(e.getMessage());
			throw new IllegalStateException(e);
		}

		List<Config> smppConfigurations = parseSMPPConfigs(configuration);

		return new ArrayList<>(smppConfigurations);
	}

	private XMLConfiguration getConfigs(String uri) {
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
				.configure(params.xml().setFileName(uri).setValidating(false));

		try {
			return builder.getConfiguration();
		} catch (ConfigurationException e) {
			throw new IllegalPathStateException("File \"" + uri + "\" not exists or is not readable");
		}
	}

	private List<Config> parseSMPPConfigs(XMLConfiguration config) {
		List<Config> configList = new ArrayList<>();

		List<HierarchicalConfiguration<ImmutableNode>> gateways = config.configurationsAt("gateways.gateway");
		for (HierarchicalConfiguration<ImmutableNode> node : gateways) {
			try {
				SMPPConfig smppConfig = new SMPPConfig(
						node.getString("queueName"),
						node.getString("alias"),
						node.getString("gatewayType"),
						node.getInt("preFetchCount"),
						node.getString("host"),
						node.getInt("port"),
						node.getString("login"),
						node.getString("password"),
						node.getString("bindType"),
						node.getString("systemType"),
						node.getString("ESMMessageNode"),
						node.getString("ESMMessageType"),
						node.getString("ESMGSMSpecificFeature"),
						node.getInt("protocolId"),
						node.getString("sourceAddrTon"),
						node.getString("sourceAddrNpi"),
						node.getString("destinationAddrTon"),
						node.getString("destinationAddrNpi"),
						node.getBoolean("replacePending")
				);
				Config configObj = new Config(Config.ConfigType.SMPP, smppConfig);
				configList.add(configObj);
			} catch (IllegalArgumentException e) {
				logger.error(e.getMessage());
			}
		}

		return configList;
	}
}
