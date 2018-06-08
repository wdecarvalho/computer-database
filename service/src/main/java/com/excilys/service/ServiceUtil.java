package com.excilys.service;

import org.springframework.data.domain.Page;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.excilys.model.Computer;

public class ServiceUtil {

    public static final int NB_PAGE = ServiceCdb.NB_PAGE;

    /**
     * Verifie que la page demandée existe et si elle n'existe pas donne la page la
     * plus cohérente par rapport à celle demandée.
     * @param nbPageMax
     *            Page maximum atteignable
     * @param pageRequired
     *            Page demandée
     * @return Page demandée ou possible
     */
    public static int verifyPageRequestedIsValidOrPutOne(final int pageRequired) {
        int pageRequested = pageRequired - 1;
        if (pageRequested < 0) {
            pageRequested = 0;
        }
        return pageRequested;
    }

    public static <T> Page<T> findObjectInDatabaseByPage(final PagingAndSortingRepository<T,Long> pAndSortingRepository,
            final int pageRequested, final int numberResult) {
        Page<T> pagecomputer = pAndSortingRepository.findAll(new QPageRequest(pageRequested, numberResult));
        if (pagecomputer.getTotalPages() > pageRequested) {
            return pagecomputer;
        } else {
            return pAndSortingRepository.findAll(new QPageRequest(
                    pagecomputer.getTotalPages() == 0 ? 0 : pagecomputer.getTotalPages() - 1, numberResult));
        }
    }
}
