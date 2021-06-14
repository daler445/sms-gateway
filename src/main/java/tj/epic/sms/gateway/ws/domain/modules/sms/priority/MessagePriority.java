package tj.epic.sms.gateway.ws.domain.modules.sms.priority;

public enum MessagePriority {
	BULK(0),
	NORMAL(1),
	HIGH(2),
	URGENT(3),
	;

	private final int priorityCode;
	MessagePriority(int priority) {
		this.priorityCode = priority;
	}

	public int getPriorityCode() {
		return priorityCode;
	}

	public String getPriorityText() {
		switch (priorityCode) {
			case 0:
				return "BULK";
			case 2:
				return "HIGH";
			case 3:
				return "URGENT";
			case 1:
			default:
				return "NORMAL";
		}
	}
}
