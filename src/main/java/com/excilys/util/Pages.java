package main.java.com.excilys.util;

import java.util.ArrayList;
import java.util.Collection;

public class Pages<T> {

	private Collection<T> entities;
	private int page_courante;
	private int page_max;
	private static int NUMBER_PER_PAGE_RESULT = 20;
	
	public Pages(int page_courante){
		this.page_courante = page_courante;
		entities = new ArrayList<>();
	}

	/**
	 * Calcule l'indice du premier résultat de la page
	 * @return
	 */
	private int firstResult() {
		return (this.page_courante-1)*NUMBER_PER_PAGE_RESULT;
	}

	public static int getNUMBER_PER_PAGE_RESULT() {
		return NUMBER_PER_PAGE_RESULT;
	}
	public int startResult() {
		if(page_courante > page_max) {
			return (page_max-1)*NUMBER_PER_PAGE_RESULT;
		}
		else {
			return firstResult();
		}
	}
	
	public Collection<T> getEntities() {
		return entities;
	}
	
	public void setPage_max(int page_max) {
		this.page_max = (int) Math.ceil((double)page_max/NUMBER_PER_PAGE_RESULT);
	}
	
	public int getPage_courante() {
		if(page_courante > page_max) {
			return page_max;
		}
		return page_courante;
	}
	
	public int getPage_max() {
		return page_max;
	}
}
