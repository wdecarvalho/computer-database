package com.excilys.dao;


public enum DaoType {
	COMPUTER_DAO ("computer_dao"),
	COMPANY_DAO ("company_dao");
	
	private String name;
	
	private DaoType(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
}
