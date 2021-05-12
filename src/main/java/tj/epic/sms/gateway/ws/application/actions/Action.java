package tj.epic.sms.gateway.ws.application.actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

public abstract class Action {
	public ResponseEntity<JsonNode> respondWithData(int statusCode, Object data) {
		ActionPayload actionPayload = new ActionPayload(statusCode, data, null);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode json = mapper.valueToTree(actionPayload);
		return ResponseEntity.ok(json);
	}
}
