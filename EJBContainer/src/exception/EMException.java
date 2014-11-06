package exception;

public class EMException extends Exception {

	public EMException() {
	}

	public EMException(String arg0) {
		super(arg0);
	}

	public EMException(Throwable arg0) {
		super(arg0);
	}

	public EMException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public EMException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
