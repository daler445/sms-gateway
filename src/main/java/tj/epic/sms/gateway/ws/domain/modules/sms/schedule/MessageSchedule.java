package tj.epic.sms.gateway.ws.domain.modules.sms.schedule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageSchedule {
	@JsonProperty("scheduleSending")
	public boolean scheduleSending = false;
	@JsonProperty("dateTime")
	public String dateTime = "now";

	public MessageSchedule() {
	}

	public MessageSchedule(String dateTime) throws DateTimeParseException {
		if (dateTime.equals("now".toLowerCase())) {
			this.scheduleSending = false;
			this.dateTime = "now";
			return;
		}

		LocalDateTime ldt = LocalDateTime.parse(dateTime);
		this.dateTime = dateTime;
		if (ldt.isBefore(LocalDateTime.now()) || ldt.isBefore(LocalDateTime.now().plusMinutes(1))) {
			scheduleSending = false;
			return;
		}
		scheduleSending = true;
	}

	public boolean isScheduleSending() {
		return scheduleSending;
	}

	public String getDateTime() {
		return dateTime;
	}

	public LocalDateTime getDateTimeObj() {
		if (dateTime.equals("now")) {
			return LocalDateTime.now();
		}
		return LocalDateTime.parse(dateTime);
	}

	public void setScheduleSending(boolean scheduleSending) {
		this.scheduleSending = scheduleSending;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
}
