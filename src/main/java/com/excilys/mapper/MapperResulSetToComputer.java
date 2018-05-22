package com.excilys.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.excilys.model.Company;
import com.excilys.model.Computer;

public class MapperResulSetToComputer implements RowMapper<Computer> {

    @Override
    public Computer mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Company company = null;
        if (resultSet.getString("company.id") != null) {
            company = MapResulSet.resulSetToCompanyOfComputer(resultSet);
        }
        return MapResulSet.resulsetToComputer(resultSet, company);

    }

}
