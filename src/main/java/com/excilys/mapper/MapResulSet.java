package com.excilys.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.excilys.model.Company;
import com.excilys.model.Computer;

public abstract class MapResulSet {

    /**
     * Constructor.
     */
    private MapResulSet() {
    }

    /**
     * Creer un optional de company à partir de la methode de création de company.
     * @param resultSet
     *            ResulSet
     * @return Optional<Company>
     * @throws SQLException
     *             Si on arrive pas a acceder aux attributs de company
     */
    public static Optional<Company> resulSetToOptionalCompanyComplete(final ResultSet resultSet) throws SQLException {
        return Optional.ofNullable(resulSetToCompanyComplete(resultSet));
    }

    /**
     * Creer une company a partir du resulSet.
     * @param resultSet
     *            ResulSet
     * @return Company compant
     * @throws SQLException
     *             Si on arrive pas à recuperer les attributs
     */
    public static Company resulSetToCompanyComplete(final ResultSet resultSet) throws SQLException {
        return new Company.Builder(resultSet.getLong("id")).name(resultSet.getString("name")).build();
    }

    /**
     * Creer une company d'un computer.
     * @param resultSet
     *            ResulSet
     * @return Company
     * @throws SQLException
     *             Si on arrive pas à acceder aux attributs de company.
     */
    public static Company resulSetToCompanyOfComputer(final ResultSet resultSet) throws SQLException {
        return new Company.Builder(resultSet.getLong("company.id")).name(resultSet.getString("company.name")).build();
    }

    /**
     * Creer un optional de computer a partir du resulset.
     * @param resultSet
     *            ResulSet
     * @param company
     *            Company
     * @return Optional<Computer>
     * @throws SQLException
     *             Si on arrive pas à acceder aux attributs
     */
    public static Optional<Computer> resulsetToOptionalComputer(final ResultSet resultSet, final Company company)
            throws SQLException {
        final String computerName = resultSet.getString("computer.name");
        final Long computerId = resultSet.getLong("computer.id");
        return Optional.ofNullable(new Computer.Builder(computerName).id(computerId)
                .introduced(MapUtil.convertTimeStampToLocal(resultSet.getTimestamp("computer.introduced")))
                .discontinued(MapUtil.convertTimeStampToLocal(resultSet.getTimestamp("computer.discontinued")))
                .company(company).build());
    }

    /**
     * Creer un omputer a partir du resulset.
     * @param resultSet
     *            ResulSet
     * @param company
     *            Company
     * @return Optional<Computer>
     * @throws SQLException
     *             Si on arrive pas à acceder aux attributs
     */
    public static Computer resulsetToComputer(final ResultSet resultSet, final Company company) throws SQLException {
        final String computerName = resultSet.getString("computer.name");
        final Long computerId = resultSet.getLong("computer.id");
        return new Computer.Builder(computerName).id(computerId)
                .introduced(MapUtil.convertTimeStampToLocal(resultSet.getTimestamp("computer.introduced")))
                .discontinued(MapUtil.convertTimeStampToLocal(resultSet.getTimestamp("computer.discontinued")))
                .company(company).build();
    }

}
