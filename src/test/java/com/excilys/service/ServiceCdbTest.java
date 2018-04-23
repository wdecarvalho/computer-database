package com.excilys.service;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.excilys.dao.CompanyDao;
import com.excilys.dao.ComputerDao;
import com.excilys.dao.ComputerDaoTest;
import com.excilys.exception.ComputerNotFoundException;
import com.excilys.extensions.MockitoExtension;
import com.excilys.model.Computer;

import javafx.beans.binding.When;
import sun.print.resources.serviceui;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ServiceCdbTest {

    private ServiceCdb servicecdb;

    @Mock
    private ComputerDao computerDao;

    @Mock
    private CompanyDao companyDao;

    /**
     * Set up la classe de test en initialisant la couche de service.
     */
    @BeforeAll
    public void setUp() {
        Mockito.when(computerDao.find(1L))
        .thenReturn(Optional.of(new Computer.Builder("PC_NAME").build()));
    }

    /**
     * Recupere les infos d'un ordinateur.
     * @throws ComputerNotFoundException  Si on ne trouve pas de computer
     */
    @Test
    public void getComputerDetailsTest() throws ComputerNotFoundException {
        servicecdb.getComputerDaoDetails(1L);
    }
}
