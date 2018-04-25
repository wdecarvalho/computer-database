package com.excilys.dto;

import java.time.LocalDate;

import com.excilys.model.Computer;

public class ComputerDTO {

    private String name;

    private LocalDate introDate;

    private LocalDate disconDate;

    private String companyName;

    /**
     * Create a DTO only with the requested DATA on the webPage.
     * @param c
     *            Computer to print
     */
    public ComputerDTO(Computer c) {
        this.name = c.getName();
        this.introDate = c.getIntroduced();
        this.disconDate = c.getDiscontinued();
        this.companyName = c.getCompany().getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getIntroDate() {
        return introDate;
    }

    public void setIntroDate(LocalDate introDate) {
        this.introDate = introDate;
    }

    public LocalDate getDisconDate() {
        return disconDate;
    }

    public void setDisconDate(LocalDate disconDate) {
        this.disconDate = disconDate;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

}
