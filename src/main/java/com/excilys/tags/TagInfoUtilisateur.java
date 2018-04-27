package com.excilys.tags;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.jsp.tagext.SimpleTagSupport;

public class TagInfoUtilisateur extends SimpleTagSupport {
    private static final String DIV_CLASS_ALERT = "<div class='alert ";
    private static final String ALERT_SUCCESS = "alert-success";
    private static final String ALERT_INFO = "alert-info";
    private static final String ALERT_WARNING = "alert-warning";
    private static final String ALERT_DANGER = "alert-danger";
    private static final String END_DIV = "</div>";
    private static final String END_STRONG = "!</strong> ";
    private static final String ALERTE_DISMISIBLE = " alert-dismissible show'\n" + "            role=\"alert\">\n"
            + "            <button type=\"button\" class=\"close\" data-dismiss=\"alert\"\n"
            + "                aria-label=\"Close\">\n" + "                <span aria-hidden=\"true\">&times;</span>\n"
            + "            </button><strong>";
    private TypeAlerte typeAlerte;
    private String message;

    /**
     * Crée la pagination de la page dashboard.
     * @throws IOException
     *             Si une IOException apparait
     */
    public void doTag() throws IOException {
        final Writer writer = getJspContext().getOut();
        final StringBuilder sBuilder = new StringBuilder(DIV_CLASS_ALERT);
        switch (typeAlerte) {
        case ERROR:
            sBuilder.append(ALERT_DANGER);
            break;
        case WARNING:
            sBuilder.append(ALERT_WARNING);
            break;
        case INFO:
            sBuilder.append(ALERT_INFO);
            break;
        case SUCCESS:
            sBuilder.append(ALERT_SUCCESS);
            break;
        }
        sBuilder.append(ALERTE_DISMISIBLE);
        switch (typeAlerte) {
        case ERROR:
            sBuilder.append(TypeAlerte.ERROR.toString() + END_STRONG);
            break;
        case WARNING:
            sBuilder.append(TypeAlerte.WARNING.toString() + END_STRONG);
            break;
        case INFO:
            sBuilder.append(TypeAlerte.INFO.toString() + END_STRONG);
            break;
        case SUCCESS:
            sBuilder.append(TypeAlerte.SUCCESS.toString() + END_STRONG);
            break;
        }
        sBuilder.append(message);
        sBuilder.append(END_DIV);
        writer.write(sBuilder.toString());
        writer.flush();
    }

    public TypeAlerte getTypeAlerte() {
        return typeAlerte;
    }

    public void setTypeAlerte(TypeAlerte typeAlerte) {
        this.typeAlerte = typeAlerte;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
