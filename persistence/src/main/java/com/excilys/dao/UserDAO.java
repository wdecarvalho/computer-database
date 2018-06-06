package com.excilys.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.excilys.model.User;

@Repository
public interface UserDAO extends CrudRepository<User, Long> {
    
    Optional<User> findByUsername(final String username);

}
