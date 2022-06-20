package iob.logic.exceptions;

public class DeprecatedFunctionException extends RuntimeException {

	private static final long serialVersionUID = 4226165704489947334L;

	public DeprecatedFunctionException() {
	}

	public DeprecatedFunctionException(String message) {
		super(message);
	}

	public DeprecatedFunctionException(Throwable cause) {
		super(cause);
	}

	public DeprecatedFunctionException(String message, Throwable cause) {
		super(message, cause);
	}

}
