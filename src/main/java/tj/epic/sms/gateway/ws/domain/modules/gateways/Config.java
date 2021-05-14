package tj.epic.sms.gateway.ws.domain.modules.gateways;

import tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.SMPPConfig;

public class Config {
	private final ConfigType configType;
	private final SMPPConfig smppConfig;

	public Config(ConfigType configType, SMPPConfig smppConfig) {
		this.configType = configType;
		this.smppConfig = smppConfig;
	}

	public SMPPConfig getSmppConfig() {
		return smppConfig;
	}

	public ConfigType getConfigType() {
		return configType;
	}

	public enum ConfigType {
		SMPP,
	}
}
