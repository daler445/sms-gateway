package tj.epic.sms.gateway.ws.domain.modules.gateways.smpp;

import tj.epic.sms.gateway.ws.domain.modules.sms.GatewayTypes;

public class SMPPConfig {
	private String queueName;
	private String alias;
	private int preFetch = 1;
	private GatewayTypes type;
	private String host;
	private String port;
	private String login;
	private String password;
	private String systemTypes;
	private int interfaceVersion;
	private boolean sourceAddrAutodetect;
	private int sourceAddrTon;
	private int sourceAddrNpi;
	private int validationPeriod;
	private boolean transceiverMode;
	private int receivePort;
	private int enquireLinkInterval;
	private int waitAckExpire;
	private int maxPendingSubmits;
	private int throughput;
	private boolean useSsl;
	private String sslClientCertKeyFile;

	public SMPPConfig(String queueName, String alias, int preFetch, GatewayTypes type, String host, String port, String login, String password, String systemTypes, int interfaceVersion, boolean sourceAddrAutodetect, int sourceAddrTon, int sourceAddrNpi, int validationPeriod, boolean transceiverMode, int receivePort, int enquireLinkInterval, int waitAckExpire, int maxPendingSubmits, int throughput, boolean useSsl, String sslClientCertKeyFile) {
		this.queueName = queueName;
		this.alias = alias;
		this.preFetch = preFetch;
		this.type = type;
		this.host = host;
		this.port = port;
		this.login = login;
		this.password = password;
		this.systemTypes = systemTypes;
		this.interfaceVersion = interfaceVersion;
		this.sourceAddrAutodetect = sourceAddrAutodetect;
		this.sourceAddrTon = sourceAddrTon;
		this.sourceAddrNpi = sourceAddrNpi;
		this.validationPeriod = validationPeriod;
		this.transceiverMode = transceiverMode;
		this.receivePort = receivePort;
		this.enquireLinkInterval = enquireLinkInterval;
		this.waitAckExpire = waitAckExpire;
		this.maxPendingSubmits = maxPendingSubmits;
		this.throughput = throughput;
		this.useSsl = useSsl;
		this.sslClientCertKeyFile = sslClientCertKeyFile;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getPreFetch() {
		return preFetch;
	}

	public void setPreFetch(int preFetch) {
		this.preFetch = preFetch;
	}

	public GatewayTypes getType() {
		return type;
	}

	public void setType(GatewayTypes type) {
		this.type = type;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSystemTypes() {
		return systemTypes;
	}

	public void setSystemTypes(String systemTypes) {
		this.systemTypes = systemTypes;
	}

	public int getInterfaceVersion() {
		return interfaceVersion;
	}

	public void setInterfaceVersion(int interfaceVersion) {
		this.interfaceVersion = interfaceVersion;
	}

	public boolean isSourceAddrAutodetect() {
		return sourceAddrAutodetect;
	}

	public void setSourceAddrAutodetect(boolean sourceAddrAutodetect) {
		this.sourceAddrAutodetect = sourceAddrAutodetect;
	}

	public int getSourceAddrTon() {
		return sourceAddrTon;
	}

	public void setSourceAddrTon(int sourceAddrTon) {
		this.sourceAddrTon = sourceAddrTon;
	}

	public int getSourceAddrNpi() {
		return sourceAddrNpi;
	}

	public void setSourceAddrNpi(int sourceAddrNpi) {
		this.sourceAddrNpi = sourceAddrNpi;
	}

	public int getValidationPeriod() {
		return validationPeriod;
	}

	public void setValidationPeriod(int validationPeriod) {
		this.validationPeriod = validationPeriod;
	}

	public boolean isTransceiverMode() {
		return transceiverMode;
	}

	public void setTransceiverMode(boolean transceiverMode) {
		this.transceiverMode = transceiverMode;
	}

	public int getReceivePort() {
		return receivePort;
	}

	public void setReceivePort(int receivePort) {
		this.receivePort = receivePort;
	}

	public int getEnquireLinkInterval() {
		return enquireLinkInterval;
	}

	public void setEnquireLinkInterval(int enquireLinkInterval) {
		this.enquireLinkInterval = enquireLinkInterval;
	}

	public int getWaitAckExpire() {
		return waitAckExpire;
	}

	public void setWaitAckExpire(int waitAckExpire) {
		this.waitAckExpire = waitAckExpire;
	}

	public int getMaxPendingSubmits() {
		return maxPendingSubmits;
	}

	public void setMaxPendingSubmits(int maxPendingSubmits) {
		this.maxPendingSubmits = maxPendingSubmits;
	}

	public int getThroughput() {
		return throughput;
	}

	public void setThroughput(int throughput) {
		this.throughput = throughput;
	}

	public boolean isUseSsl() {
		return useSsl;
	}

	public void setUseSsl(boolean useSsl) {
		this.useSsl = useSsl;
	}

	public String getSslClientCertKeyFile() {
		return sslClientCertKeyFile;
	}

	public void setSslClientCertKeyFile(String sslClientCertKeyFile) {
		this.sslClientCertKeyFile = sslClientCertKeyFile;
	}
}
