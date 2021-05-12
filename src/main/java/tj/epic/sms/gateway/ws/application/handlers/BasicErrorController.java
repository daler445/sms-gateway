package tj.epic.sms.gateway.ws.application.handlers;

import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
public class BasicErrorController extends AbstractErrorController {
	public BasicErrorController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers) {
		super(errorAttributes, errorViewResolvers);
	}

	@Override
	public String getErrorPath() {
		return null;
	}

	@RequestMapping
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
		Map<String, Object> body = new HashMap<>();
		HttpStatus status = getStatus(request);
		return new ResponseEntity<>(body, status);
	}
}
