package main.java.com.excilys.exception;

public class DaoNotInitializeException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE = "Aucune DAO n'a été initialisé";
	
	public DaoNotInitializeException(String msg) {
		super(MESSAGE);
	}

}
