package com.excilys.exception;

public class ComputerNotFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private static final String MESSAGE = "This computer not exist : ";
	
	public ComputerNotFoundException(String msg) {
		super(MESSAGE+""+msg);
	}
	

}
