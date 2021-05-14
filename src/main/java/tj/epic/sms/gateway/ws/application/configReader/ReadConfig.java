package tj.epic.sms.gateway.ws.application.configReader;

import tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.SMPPConfig;
import tj.epic.sms.gateway.ws.domain.modules.sms.GatewayTypes;

import java.util.ArrayList;
import java.util.List;

public class ReadConfig {
	public static List<SMPPConfig> SMPP(String uri) {
		// @TODO implement external configuration parsing
		SMPPConfig config = new SMPPConfig("con1", "ZET-mobile", 1, GatewayTypes.Local, "127.0.0.1", "5051", "con1", "", "", 0, false, 1, 0, 300, false, 8081, 300, 0, 10, 1, false, "");
		SMPPConfig config2 = new SMPPConfig("con2", "Babilon-m", 15, GatewayTypes.Local, "localhost", "5052", "con2", "", "", 0, false, 1, 0, 300, false, 8081, 300, 0, 10, 1, false, "");
		SMPPConfig config3 = new SMPPConfig("con3", "smsc.ru", 15, GatewayTypes.External, "localhost", "5052", "con2", "", "", 0, false, 1, 0, 300, false, 8081, 300, 0, 10, 1, false, "");
		List<SMPPConfig> configList = new ArrayList<>();
		configList.add(config);
		configList.add(config2);
		configList.add(config3);
		return configList;
	}
}
