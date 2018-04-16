package main.java.com.excilys.model;

import java.sql.Timestamp;
import java.time.LocalDate;

public class Computer {
	
	private Long id;
	private String name;
	private LocalDate introduced;
	private LocalDate discontinued;
	private Company company;
	
	public Computer() {
		// default
	}
	
	public Computer(String name) {
		this.name = name;
	}
	
	public Computer(String name,Timestamp introduced, Timestamp discon,Company company) {
		this.name = name;
		this.introduced = introduced == null ? null : introduced.toLocalDateTime().toLocalDate();
		this.discontinued = discontinued == null ? null : discon.toLocalDateTime().toLocalDate();
		this.company = company;
	}
	
	public Computer(Long id,String name,Timestamp introduced, Timestamp discon,Company company) {
		this(name,introduced,discon,company);
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getIntroduced() {
		return introduced;
	}

	public void setIntroduced(LocalDate introduced) {
		this.introduced = introduced;
	}

	public LocalDate getDiscontinued() {
		return discontinued;
	}

	public void setDiscontinued(LocalDate discontinued) {
		this.discontinued = discontinued;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
	
	
	

}
