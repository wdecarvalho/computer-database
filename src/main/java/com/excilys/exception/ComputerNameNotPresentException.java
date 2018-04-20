package com.excilys.exception;

public class ComputerNameNotPresentException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final String MESSAGE = "Le nom du computer est un champ obligatoire";
	
	public ComputerNameNotPresentException() {
		super(MESSAGE);
	}
}
