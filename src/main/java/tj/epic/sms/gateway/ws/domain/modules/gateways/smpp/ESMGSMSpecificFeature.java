package tj.epic.sms.gateway.ws.domain.modules.gateways.smpp;

import org.jsmpp.bean.GSMSpecificFeature;

public enum ESMGSMSpecificFeature {
	DEFAULT,

	/**
	 * User data header indicator.
	 */
	UDHI,

	/**
	 * Reply path.
	 */
	REPLYPATH,

	/**
	 * User data header indicator and Reply path.
	 */
	UDHI_REPLYPATH,
	;

	public GSMSpecificFeature get() {
		switch (this) {
			case UDHI:
				return GSMSpecificFeature.UDHI;
			case REPLYPATH:
				return GSMSpecificFeature.REPLYPATH;
			case UDHI_REPLYPATH:
				return GSMSpecificFeature.UDHI_REPLYPATH;
			case DEFAULT:
			default:
				return GSMSpecificFeature.DEFAULT;
		}
	}
}
