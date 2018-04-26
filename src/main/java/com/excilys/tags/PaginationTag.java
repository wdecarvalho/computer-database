package com.excilys.tags;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.jsp.tagext.SimpleTagSupport;

public class PaginationTag extends SimpleTagSupport {
    private static final String DASHBOARD_PAGE = "<a href='dashboard?page=";
    private static final String END_UL = "</ul>";
    private static final String NEXT_SPAN = " aria-label='Next'> <span aria-hidden='true'>&raquo;</span></a>";
    private static final String END_A_AND_LI = "</a></li>";
    private static final String PAGE_ITEM_ACTIVE = "<li class='page-item active'>";
    private static final String END_LI = "</li>";
    private static final String PREVIOUS_SPAN = " aria-label='Previous'> <span aria-hidden='true'>&laquo;</span></a>";
    private static final String UL_CLASS_PAGINATION = "<ul class='pagination'>";
    private static final String LI_CLASS_DISABLE_ITEM = "<li class='page-item disabled'>";
    private static final String LI_CLASS_PAGE_ITEM = "<li class='page-item'>";
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
        final StringBuilder sBuilder = new StringBuilder(UL_CLASS_PAGINATION);
        if (pageCourante - 1 >= 1) {
            sBuilder.append(LI_CLASS_PAGE_ITEM);
            sBuilder.append(constructUrl(pageCourante - 1));
        } else {
            sBuilder.append(LI_CLASS_DISABLE_ITEM);
            sBuilder.append(constructUrl(1));
        }
        sBuilder.append(PREVIOUS_SPAN);
        sBuilder.append(END_LI);
        int start = pageCourante - NB_PAGE, end = pageCourante + NB_PAGE;
        if (start < 1) {
            start = 1;
        }
        if (end > limit) {
            end = limit;
        }
        for (; start <= end; start++) {
            if (start == pageCourante) {
                sBuilder.append(PAGE_ITEM_ACTIVE);
            } else {
                sBuilder.append(LI_CLASS_PAGE_ITEM);
            }
            sBuilder.append(constructUrl(start)).append(">").append(start).append(END_A_AND_LI);
        }
        if (pageCourante + 1 > limit) {
            sBuilder.append(LI_CLASS_DISABLE_ITEM);
            sBuilder.append(constructUrl(limit));
        } else {
            sBuilder.append(LI_CLASS_PAGE_ITEM);
            sBuilder.append(constructUrl(pageCourante + 1));
        }
        sBuilder.append(NEXT_SPAN);
        sBuilder.append(END_LI);
        sBuilder.append(END_UL);
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
        final StringBuilder sBuilder = new StringBuilder(DASHBOARD_PAGE);
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
