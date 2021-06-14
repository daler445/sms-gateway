package tj.epic.sms.gateway.ws.domain.modules.sms.MessagePriority;

public class CheckPriority {
	public static MessagePriority parse(String priority) {
		switch (priority.toLowerCase()) {
			case "bulk":
			case "0":
				return MessagePriority.BULK;
			case "high":
			case "2":
				return MessagePriority.HIGH;
			case "urgent":
			case "3":
				return MessagePriority.URGENT;
			case "normal":
			case "1":
			default:
				return MessagePriority.NORMAL;
		}
	}
}
