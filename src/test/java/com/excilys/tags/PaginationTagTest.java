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
public class PaginationTagTest {

    private static final String PAGE1 = "<a href='dashboard?page=1'";
    private static final String PAGE2 = "<a href='dashboard?page=2'";

    @Mock
    JspWriter writer;

    @Mock
    JspContext jspContext;

    @InjectMocks
    private PaginationTag paginationTag;

    /**
     * Construit le paginationTag comme si il recevait de la servlet les arguments.
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
    @DisplayName("Verifie que les url de la pagination sont bien construit et que doTag s'execute correctement")
    public void testPagination() throws IOException {
        paginationTag.setPageCourante(1);
        paginationTag.setLimit(4);
        paginationTag.setJspContext(jspContext);
        Mockito.when(jspContext.getOut()).thenReturn(writer);
        paginationTag.doTag();
        // Mockito.verify(jspContext).getOut();
        assertEquals(paginationTag.constructUrl(1), PAGE1);
        assertEquals(paginationTag.constructUrl(2), PAGE2);

        paginationTag.setPageCourante(3);
        paginationTag.setLimit(3);
        paginationTag.setJspContext(jspContext);
        Mockito.when(jspContext.getOut()).thenReturn(writer);
        paginationTag.doTag();
        Mockito.verify(jspContext, Mockito.times(2)).getOut();
    }
}