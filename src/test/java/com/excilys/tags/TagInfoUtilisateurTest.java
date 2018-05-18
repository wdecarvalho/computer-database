package com.excilys.tags;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspWriter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class TagInfoUtilisateurTest {

    private static final String SUCCESS = "Success";

    @Mock
    JspWriter writer;

    @Mock
    JspContext jspContext;

    @InjectMocks
    private TagInfoUtilisateur tagInfoUtilisateur;

    /**
     * Construit le taginfo comme si il recevait de la servlet les arguments.
     */
    @BeforeAll
    public void setUp() {
        MockitoAnnotations.initMocks(this);

    }

    /**
     * Verifie la construction des URL et que doTag s'execute correctement.
     * @throws IOException
     *             IOException
     */
    @Test
    @DisplayName("Verifie que le doTag s'excecute correctement")
    public void testTaginfo() throws IOException {
        tagInfoUtilisateur.setJspContext(jspContext);
        tagInfoUtilisateur.setTypeAlerte(TypeAlerte.SUCCESS);
        tagInfoUtilisateur.setMessage(SUCCESS);
        Mockito.when(jspContext.getOut()).thenReturn(writer);
        tagInfoUtilisateur.doTag();
        tagInfoUtilisateur.setTypeAlerte(TypeAlerte.ERROR);
        tagInfoUtilisateur.doTag();
        tagInfoUtilisateur.setTypeAlerte(TypeAlerte.INFO);
        tagInfoUtilisateur.doTag();
        tagInfoUtilisateur.setTypeAlerte(TypeAlerte.WARNING);
        tagInfoUtilisateur.doTag();
        Mockito.verify(jspContext, Mockito.times(4)).getOut();
        assertEquals(tagInfoUtilisateur.getTypeAlerte(), TypeAlerte.WARNING);
        assertEquals(tagInfoUtilisateur.getMessage(), SUCCESS);
    }

}
