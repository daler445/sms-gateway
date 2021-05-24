package tj.epic.sms.gateway.ws.domain.modules.gateways.smpp;

import tj.epic.sms.gateway.ws.domain.modules.sms.GatewayTypes;

public class SMPPConfig {
	private final String queueName;
	private final String alias;
	private final GatewayTypes gatewayType;
	private final int preFetchCount;
	private final String host;
	private final int port;
	private final String login;
	private final String password;
	private final BindType bindType;
	private final SystemType systemType;
	private final ESMMessageMode ESMMessageMode;
	private final ESMMessageType ESMMessageType;
	private final ESMGSMSpecificFeature ESMGSMSpecificFeature;
	private final int protocolId;
	private final TypeOfNumber sourceAddrTon;
	private final NumberingPlanIndicator sourceAddrNpi;
	private final TypeOfNumber destinationAddrTon;
	private final NumberingPlanIndicator destinationAddrNpi;
	private final boolean replacePending;

	public SMPPConfig(String queueName, String alias, String gatewayType, int preFetchCount, String host, int port, String login, String password, String bindType, String systemType, String ESMMessageMode, String ESMMessageType, String ESMGSMSpecificFeature, int protocolId, String sourceAddrTon, String sourceAddrNpi, String destinationAddrTon, String destinationAddrNpi, boolean replacePending) {
		this.queueName = queueName;
		this.alias = alias;

		if (preFetchCount <= 0) {
			throw new IllegalArgumentException("Prefetch count must be 1 or greater");
		}
		this.preFetchCount = preFetchCount;

		this.host = host;
		this.port = port;
		this.login = login;
		this.password = password;

		switch (gatewayType.toLowerCase()) {
			case "both":
				this.gatewayType = GatewayTypes.Both;
				break;
			case "external":
				this.gatewayType = GatewayTypes.External;
				break;
			case "local":
				this.gatewayType = GatewayTypes.Local;
				break;
			default:
				throw new IllegalArgumentException("Gateway type must be one of [both, external, local]");
		}

		switch (bindType.toLowerCase()) {
			case "transmitter":
				this.bindType = BindType.TRANSMITTER;
				break;
			case "receiver":
				this.bindType = BindType.RECEIVER;
				break;
			case "transceiver":
				this.bindType = BindType.TRANSCEIVER;
				break;
			default:
				throw new IllegalArgumentException("Bind type must be one of [transmitter, receiver, transceiver]");
		}

		switch (systemType.toLowerCase()) {
			case "mcon1":
				this.systemType = SystemType.MCON1;
				break;
			case "mcon2":
				this.systemType = SystemType.MCON2;
				break;
			case "mcon3":
				this.systemType = SystemType.MCON3;
				break;
			case "mcon4":
				this.systemType = SystemType.MCON4;
				break;
			case "mcon5":
				this.systemType = SystemType.MCON5;
				break;
			case "mcon6":
				this.systemType = SystemType.MCON6;
				break;
			case "mcon7":
				this.systemType = SystemType.MCON7;
				break;
			case "mcon8":
				this.systemType = SystemType.MCON8;
				break;
			case "mcon9":
				this.systemType = SystemType.MCON9;
				break;
			case "iso":
				this.systemType = SystemType.ISO;
				break;
			case "single":
				this.systemType = SystemType.SINGLE;
				break;
			case "null":
			case "":
				this.systemType = SystemType.NULL;
			default:
				throw new IllegalArgumentException("System type must be one of [mcon1, mcon2, mcon3, mcon4, mcon5, mcon6, mcon7, mcon8, mcon9, iso, single, null]");
		}

		switch (ESMMessageMode.toLowerCase()) {
			case "default":
				this.ESMMessageMode = tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMMessageMode.DEFAULT;
				break;
			case "datagram":
				this.ESMMessageMode = tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMMessageMode.DATAGRAM;
				break;
			case "transaction":
				this.ESMMessageMode = tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMMessageMode.TRANSACTION;
				break;
			case "store_and_forward":
				this.ESMMessageMode = tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMMessageMode.STORE_AND_FORWARD;
				break;
			default:
				throw new IllegalArgumentException("ESM message mode must be one of [default, datagram, transaction, store_and_forward]");
		}

		switch (ESMMessageType.toLowerCase()) {
			case "default":
				this.ESMMessageType = tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMMessageType.DEFAULT;
				break;
			case "esme_del_ack":
				this.ESMMessageType = tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMMessageType.ESME_DEL_ACK;
				break;
			case "esme_man_ack":
				this.ESMMessageType = tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMMessageType.ESME_MAN_ACK;
				break;
			case "smsc_del_receipt":
				this.ESMMessageType = tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMMessageType.SMSC_DEL_RECEIPT;
				break;
			case "sme_del_ack":
				this.ESMMessageType = tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMMessageType.SME_DEL_ACK;
				break;
			case "sme_man_ack":
				this.ESMMessageType = tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMMessageType.SME_MAN_ACK;
				break;
			case "conv_abort":
				this.ESMMessageType = tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMMessageType.CONV_ABORT;
				break;
			case "inter_del_notif":
				this.ESMMessageType = tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMMessageType.INTER_DEL_NOTIF;
				break;
			default:
				throw new IllegalArgumentException("ESM Message Type must be one of [default, esme_del_ack, esme_man_ack, smsc_del_receipt, sme_del_ack, sme_man_ack, conv_abort, inter_del_notif]");
		}

		switch (ESMGSMSpecificFeature.toLowerCase()) {
			case "default":
				this.ESMGSMSpecificFeature = tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMGSMSpecificFeature.DEFAULT;
				break;
			case "udhi":
				this.ESMGSMSpecificFeature = tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMGSMSpecificFeature.UDHI;
				break;
			case "replypath":
				this.ESMGSMSpecificFeature = tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMGSMSpecificFeature.REPLYPATH;
				break;
			case "udhi_replypath":
				this.ESMGSMSpecificFeature = tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMGSMSpecificFeature.UDHI_REPLYPATH;
				break;
			default:
				throw new IllegalArgumentException("ESM GSM Specific Feature must be one of [default, udhi, replypath, udhi_replypath]");
		}

		switch (sourceAddrTon.toLowerCase()) {
			case "unknown":
				this.sourceAddrTon = TypeOfNumber.UNKNOWN;
				break;
			case "international":
				this.sourceAddrTon = TypeOfNumber.INTERNATIONAL;
				break;
			case "national":
				this.sourceAddrTon = TypeOfNumber.NATIONAL;
				break;
			case "network_specific":
				this.sourceAddrTon = TypeOfNumber.NETWORK_SPECIFIC;
				break;
			case "subscriber_number":
				this.sourceAddrTon = TypeOfNumber.SUBSCRIBER_NUMBER;
				break;
			case "alphanumeric":
				this.sourceAddrTon = TypeOfNumber.ALPHANUMERIC;
				break;
			case "abbreviated":
				this.sourceAddrTon = TypeOfNumber.ABBREVIATED;
				break;
			default:
				throw new IllegalArgumentException("Source addr ton must be one of [unknown, international, national, network_specific, subscriber_number, alphanumeric, abbreviated]");
		}

		switch (destinationAddrTon.toLowerCase()) {
			case "unknown":
				this.destinationAddrTon = TypeOfNumber.UNKNOWN;
				break;
			case "international":
				this.destinationAddrTon = TypeOfNumber.INTERNATIONAL;
				break;
			case "national":
				this.destinationAddrTon = TypeOfNumber.NATIONAL;
				break;
			case "network_specific":
				this.destinationAddrTon = TypeOfNumber.NETWORK_SPECIFIC;
				break;
			case "subscriber_number":
				this.destinationAddrTon = TypeOfNumber.SUBSCRIBER_NUMBER;
				break;
			case "alphanumeric":
				this.destinationAddrTon = TypeOfNumber.ALPHANUMERIC;
				break;
			case "abbreviated":
				this.destinationAddrTon = TypeOfNumber.ABBREVIATED;
				break;
			default:
				throw new IllegalArgumentException("Destination addr ton must be one of [unknown, international, national, network_specific, subscriber_number, alphanumeric, abbreviated]");
		}

		switch (sourceAddrNpi.toLowerCase()) {
			case "unknown":
				this.sourceAddrNpi = NumberingPlanIndicator.UNKNOWN;
				break;
			case "isdn":
				this.sourceAddrNpi = NumberingPlanIndicator.ISDN;
				break;
			case "data":
				this.sourceAddrNpi = NumberingPlanIndicator.DATA;
				break;
			case "telex":
				this.sourceAddrNpi = NumberingPlanIndicator.TELEX;
				break;
			case "land_mobile":
				this.sourceAddrNpi = NumberingPlanIndicator.LAND_MOBILE;
				break;
			case "national":
				this.sourceAddrNpi = NumberingPlanIndicator.NATIONAL;
				break;
			case "private":
				this.sourceAddrNpi = NumberingPlanIndicator.PRIVATE;
				break;
			case "ermes":
				this.sourceAddrNpi = NumberingPlanIndicator.ERMES;
				break;
			case "internet":
				this.sourceAddrNpi = NumberingPlanIndicator.INTERNET;
				break;
			case "wap":
				this.sourceAddrNpi = NumberingPlanIndicator.WAP;
				break;
			default:
				throw new IllegalArgumentException("Source address numbering plain indicator must be one of [unknown, isdn, data, telex, land_mobile, national, private, ermes, internet, wap]");
		}

		switch (destinationAddrNpi.toLowerCase()) {
			case "unknown":
				this.destinationAddrNpi = NumberingPlanIndicator.UNKNOWN;
				break;
			case "isdn":
				this.destinationAddrNpi = NumberingPlanIndicator.ISDN;
				break;
			case "data":
				this.destinationAddrNpi = NumberingPlanIndicator.DATA;
				break;
			case "telex":
				this.destinationAddrNpi = NumberingPlanIndicator.TELEX;
				break;
			case "land_mobile":
				this.destinationAddrNpi = NumberingPlanIndicator.LAND_MOBILE;
				break;
			case "national":
				this.destinationAddrNpi = NumberingPlanIndicator.NATIONAL;
				break;
			case "private":
				this.destinationAddrNpi = NumberingPlanIndicator.PRIVATE;
				break;
			case "ermes":
				this.destinationAddrNpi = NumberingPlanIndicator.ERMES;
				break;
			case "internet":
				this.destinationAddrNpi = NumberingPlanIndicator.INTERNET;
				break;
			case "wap":
				this.destinationAddrNpi = NumberingPlanIndicator.WAP;
				break;
			default:
				throw new IllegalArgumentException("Destination address numbering plain indicator must be one of [unknown, isdn, data, telex, land_mobile, national, private, ermes, internet, wap]");
		}

		this.protocolId = protocolId;
		this.replacePending = replacePending;
	}

	public String getQueueName() {
		return queueName;
	}

	public String getAlias() {
		return alias;
	}

	public int getPreFetchCount() {
		return preFetchCount;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public BindType getBindType() {
		return bindType;
	}

	public SystemType getSystemType() {
		return systemType;
	}

	public ESMMessageMode getESMMessageMode() {
		return ESMMessageMode;
	}

	public tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMMessageType getESMMessageType() {
		return ESMMessageType;
	}

	public tj.epic.sms.gateway.ws.domain.modules.gateways.smpp.ESMGSMSpecificFeature getESMGSMSpecificFeature() {
		return ESMGSMSpecificFeature;
	}

	public int getProtocolId() {
		return protocolId;
	}

	public TypeOfNumber getSourceAddrTon() {
		return sourceAddrTon;
	}

	public NumberingPlanIndicator getSourceAddrNpi() {
		return sourceAddrNpi;
	}

	public TypeOfNumber getDestinationAddrTon() {
		return destinationAddrTon;
	}

	public NumberingPlanIndicator getDestinationAddrNpi() {
		return destinationAddrNpi;
	}

	public boolean isReplacePending() {
		return replacePending;
	}

	public GatewayTypes getGatewayType() {
		return gatewayType;
	}
}
