package com.excilys.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.executable.ValidateOnExecution;

import com.excilys.constants.commons.message.MessageValidationAndException;

@ValidateOnExecution
@Entity
@Table(name = "computer")
public class Computer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = MessageValidationAndException.NAME_IS_REQUIRED)
    private String name;

    private LocalDate introduced;

    private LocalDate discontinued;

    @ManyToOne
    private Company company;

    /**
     * Contructeur de computer.
     * @param b
     *            Builder (pattern)
     */
    private Computer(Builder b) {
        id = b.id;
        name = b.name;
        introduced = b.introduced;
        discontinued = b.discontinued;
        company = b.company;
    }

    /**
     * Constructeur privée pour hibernate.
     */
    public Computer() {

    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
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
        if (!(obj instanceof Computer)) {
            return false;
        }
        Computer other = (Computer) obj;
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
        return "Computer [id=" + id + ", name=" + name + ", introduced=" + introduced + ", discontinued=" + discontinued
                + ", company=" + company + "]";
    }

    public static class Builder {
        private Long id;
        private final String name;
        private LocalDate introduced;
        private LocalDate discontinued;
        private Company company;

        /**
         * Constructeur de Builder.
         * @param name
         *            Nom du computer
         */
        public Builder(String name) {
            this.name = name;
        }

        /**
         * Permet de mettre un ID a un computer.
         * @param id
         *            ID du computer
         * @return Builder
         */
        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        /**
         * Permet de mettre une LocalDate a un computer.
         * @param introduced
         *            LocalDate
         * @return Builder
         */
        public Builder introduced(LocalDate introduced) {
            this.introduced = introduced;
            return this;
        }

        /**
         * Permet de mettre une LocalDate a un computer.
         * @param discontinued
         *            LocalDate
         * @return Builder
         */
        public Builder discontinued(LocalDate discontinued) {
            this.discontinued = discontinued;
            return this;
        }

        /**
         * Permet de mettre une companie dans un computer.
         * @param company
         *            Company
         * @return Builder
         */
        public Builder company(Company company) {
            this.company = company;
            return this;
        }

        /**
         * Permet de créer un computer.
         * @return Computer
         */
        public Computer build() {
            return new Computer(this);
        }
    }
}
