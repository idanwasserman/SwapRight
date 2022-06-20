package iob.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class EmptyFieldException extends RuntimeException {

	private static final long serialVersionUID = -5673170043066568594L;

	public EmptyFieldException() {
	}

	public EmptyFieldException(String message) {
		super(message);
	}

	public EmptyFieldException(Throwable cause) {
		super(cause);
	}

	public EmptyFieldException(String message, Throwable cause) {
		super(message, cause);
	}

}
