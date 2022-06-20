package iob.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class AccessDeniedException extends RuntimeException {

	private static final long serialVersionUID = -7702892343214704887L;

	public AccessDeniedException() {
	}

	public AccessDeniedException(String message) {
		super(message);
	}

	public AccessDeniedException(Throwable cause) {
		super(cause);
	}

	public AccessDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

}
