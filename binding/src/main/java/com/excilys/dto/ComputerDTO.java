package com.excilys.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.executable.ValidateOnExecution;

import org.springframework.format.annotation.DateTimeFormat;

import com.excilys.constants.commons.message.MessageValidationAndException;
import com.excilys.model.Computer;

@ValidateOnExecution
public class ComputerDTO {

    private Long id;

    @NotBlank(message = MessageValidationAndException.NAME_IS_REQUIRED)
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate introDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate disconDate;

    private Long companyID;

    private String companyName;

    /**
     * Create a DTO only with the requested DATA on the webPage.
     * @param c
     *            Computer to print
     */
    public ComputerDTO(Computer c) {
        if (c != null) {
            this.id = c.getId();
            this.name = c.getName();
            this.introDate = c.getIntroduced();
            this.disconDate = c.getDiscontinued();
            this.companyName = c.getCompany() == null ? null : c.getCompany().getName();
            this.companyID = c.getCompany() == null ? null : c.getCompany().getId();
        }
    }
    
    public ComputerDTO() {
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Long companyID) {
        this.companyID = companyID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ComputerDTO other = (ComputerDTO) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ComputerDTO [id=" + id + ", name=" + name + ", introDate=" + introDate + ", disconDate=" + disconDate
                + ", companyID=" + companyID + ", companyName=" + companyName + "]";
    }

}
