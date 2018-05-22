package com.excilys.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.excilys.model.Company;

public class MapperResulSetToCompany implements RowMapper<Company> {

    @Override
    public Company mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return MapResulSet.resulSetToCompanyComplete(resultSet);

    }

}
