package exception;

public class BeanException extends Exception {

	public BeanException() {
	}

	public BeanException(String arg0) {
		super(arg0);
	}

	public BeanException(Throwable arg0) {
		super(arg0);
	}

	public BeanException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public BeanException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
