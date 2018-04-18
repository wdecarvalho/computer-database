package main.java.com.excilys.ui;

import java.util.Arrays;

public enum ChoixUtilisateur {
	CHOIX_USER ("Veuillez entrez le numéro de l'action souhaitez : ",0),
	MESSAGE_USER_COMPUTER ("Veuillez entrez les informations demandées ci-dessous (entrée pour passer) : ",0),
	NUMBER_COMPUTER ("Quel est le numero de l'ordinateur ? : [0-9] ",0),
	AJOUTER_COMPANIE_TO_COMPUTER ("Voulez vous ajouter une companie à l'ordinateur ? : [y/n] ",0),
	//-------------------------------------------------
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