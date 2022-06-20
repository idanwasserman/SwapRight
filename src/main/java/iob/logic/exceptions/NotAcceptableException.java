package iob.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class NotAcceptableException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4081961217955851763L;

	public NotAcceptableException() {
	}

	public NotAcceptableException(String message) {
		super(message);
	}

	public NotAcceptableException(Throwable cause) {
		super(cause);
	}

	public NotAcceptableException(String message, Throwable cause) {
		super(message, cause);
	}

}
