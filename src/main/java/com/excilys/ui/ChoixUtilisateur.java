package main.java.com.excilys.ui;

import java.util.Arrays;
import java.util.Collection;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public enum ChoixUtilisateur {
	DELETE_COMPUTER ("6. Delete a computer ",6),
	UPDATE_COMPUTER ("5. Modify a computer ",5),
	ADD_COMPUTER ("4. Add a computer ",4),
	FIND_ONE_COMPUTER ("3. Show computer details ",3),
	LIST_COMPANIES ("2. List companies ",2),
	LIST_COMPUTERS ("1. List computers" ,1);
	
	private String name;
	
	private int indice;
	
	private ChoixUtilisateur(String s,int indice) {
		this.name = s;
		this.indice = indice;
	}
	
	public static ChoixUtilisateur getChoix(int indice) {
		return Arrays.stream(ChoixUtilisateur.values()).filter((c) -> c.indice == indice).findFirst().get();
	}
	
	@Override
	public String toString() {
		return name;
	}
}