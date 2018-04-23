package com.excilys.model;

public class Company {

    private Long id;
    private String name;

    /**
     * Constructeur de Company.
     * @param builder Builder (pattern)
     */
    private Company(Builder builder) {
        this.id = builder.iD;
        this.name = builder.name;
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

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Company)) {
            return false;
        }
        Company other = (Company) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Company [id=" + id + ", name=" + name + "]";
    }

    public static class Builder {
        private final Long iD;
        private String name;

        /**
         * Constructeur du Builder.
         * @param iD Id de la companie
         */
        public Builder(Long iD) {
            this.iD = iD;
        }

        /**
         * Permet de mettre le nom d'une companie.
         * @param name Nom de la compagnie
         * @return Builder
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Permet de creer la companie.
         * @return Company
         */
        public Company build() {
            return new Company(this);
        }
    }

}
