package com.excilys.tags;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.jsp.tagext.SimpleTagSupport;

public class PaginationTag extends SimpleTagSupport {
    private int pageCourante;
    private int limit;
    private static final int NB_PAGE = 2;

    /**
     * Crée la pagination de la page dashboard.
     * @throws IOException
     *             Si une IOException apparait
     */
    public void doTag() throws IOException {
        final Writer writer = getJspContext().getOut();
        final StringBuilder sBuilder = new StringBuilder("<ul class='pagination'>");
        sBuilder.append("<li>");
        sBuilder.append(constructUrl(pageCourante - 1 > 1 ? pageCourante - 1 : 1));
        sBuilder.append(" aria-label='Previous'> <span aria-hidden='true'>&laquo;</span></a>");
        sBuilder.append("</li>");
        int start = pageCourante - NB_PAGE, end = pageCourante + NB_PAGE;
        if (start < 1) {
            start = 1;
        }
        if (end > limit) {
            end = limit;
        }
        for (; start <= end; start++) {
            if (start == pageCourante) {
                sBuilder.append("<li class='page-item active'>");
            } else {
                sBuilder.append("<li>");
            }
            sBuilder.append(constructUrl(start)).append(">").append(start).append("</a></li>");
        }
        sBuilder.append("<li>");
        sBuilder.append(constructUrl(pageCourante + 1 >= limit ? limit : pageCourante + 1)).append(" aria-label='Next'> <span aria-hidden='true'>&raquo;</span></a>");
        sBuilder.append("</li>");
        sBuilder.append("</ul>");
        writer.write(sBuilder.toString());
        writer.flush();
    }

    /**
     * Creer l'URL pour chaque bouton de la pagination.
     * @param page
     *            Page a atteindre pour un bouton
     * @return String URL
     */
    public String constructUrl(int page) {
        final StringBuilder sBuilder = new StringBuilder("<a href='dashboard?page=");
        sBuilder.append(page).append("'");
        return sBuilder.toString();

    }

    public int getPageCourante() {
        return pageCourante;
    }

    public void setPageCourante(int pageCourante) {
        this.pageCourante = pageCourante;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

}
