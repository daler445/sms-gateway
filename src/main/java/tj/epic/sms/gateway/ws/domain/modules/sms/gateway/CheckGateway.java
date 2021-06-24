package tj.epic.sms.gateway.ws.domain.modules.sms.gateway;

import tj.epic.sms.gateway.ws.domain.modules.gateways.Config;

import java.util.List;

public class CheckGateway {
	public static boolean isGatewayAvailable(List<Config> boundedGateways, String gatewayName) {
		for(Config config : boundedGateways) {
			if (config.getConfigType().equals(Config.ConfigType.SMPP) && config.getSmppConfig().getQueueName().equals(gatewayName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSubmitMultiAvailable(List<Config> boundedGateways, String gatewayName) {
		for(Config config : boundedGateways) {
			if (config.getConfigType().equals(Config.ConfigType.SMPP) && config.getSmppConfig().getQueueName().equals(gatewayName)) {
				if (config.getSmppConfig().isSubmitMultiAvailable()) {
					return true;
				}
				return false;
			}
		}
		return false;
	}
}
