package com.excilys.service.computer;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.exception.ComputerException;
import com.excilys.exception.company.CompanyNotFoundException;
import com.excilys.exception.computer.ComputerNotDeletedException;
import com.excilys.exception.computer.ComputerNotFoundException;
import com.excilys.exception.date.DateTruncationException;
import com.excilys.model.Computer;
import com.excilys.service.ServiceCdb;

@Service
@Transactional
public interface ServiceCdbComputer extends ServiceCdb<Computer> {

    /**
     * Retourne les computer par pages et par recherche.
     * @param search
     *            Computer ou company name a recherché
     * @param pageAndNumberResult
     *            Numero de page courante et nombre de resultat a afficher
     * @return Page de computer
     */
    @Transactional(readOnly = true)
    Page<Computer> findByPagesSearch(String search, int... pageAndNumberResult);

    /**
     * Recupere le nombre d'ordinateur retournée par la recherche en base de
     * données.
     * @param search
     *            mot clé
     * @return Nombre de computer trouvés.
     */
    @Transactional(readOnly = true)
    Long getCountSearched(String search);

    /**
     * Supprime un ou plusieurs computers.
     * @param computersToDelete
     *            Liste de computers a supprimer
     * @return Si true tout a été supprimer, si false rien n'a été supprimé.
     * @throws ComputerNotDeletedException
     *             Si au moin un Computer n'a pas été supprimé.
     */
    @Transactional(rollbackFor = ComputerNotDeletedException.class)
    boolean deleteMulitple(Iterable<Long> computersToDelete) throws ComputerNotDeletedException;

    /**
     * Demande à la DAO d'inserer un computer en base de données et le valide avant
     * si necessaire.
     * @param c
     *            Computer
     * @param validation
     *            Vrai si demande de validation requise
     * @return ID crée
     * @throws CompanyNotFoundException
     *             Si la companie n'est pas trouvé
     * @throws DateTruncationException
     *             Si une erreur de date intervient
     * @throws ComputerException
     *             Regle de validation
     */
    @Transactional(rollbackFor = { CompanyNotFoundException.class, DateTruncationException.class,
            ComputerException.class })
    Long save(Computer c, boolean validation)
            throws CompanyNotFoundException, DateTruncationException, ComputerException;

    /**
     * Recupere les information d'un computer.
     * @param id
     *            ID du computer recherché
     * @return Computer
     * @throws ComputerNotFoundException
     *             Si le computer n'est pas présent
     */
    @Transactional(readOnly = true)
    Computer getComputerDaoDetails(Long id) throws ComputerNotFoundException;

    /**
     * Met a jour le computer passé en parametre.
     * @param c
     *            Computer
     * @param validation
     *            True necesite une validation manuelle
     * @return Computer mis a jour
     * @throws CompanyNotFoundException
     *             Si la companie n'existe pas
     * @throws DateTruncationException
     *             Si date < 1970
     * @throws ComputerException
     *             Regle de validation
     */
    @Transactional(rollbackFor = { CompanyNotFoundException.class, DateTruncationException.class,
            ComputerException.class })
    Computer update(Computer c, boolean validation)
            throws CompanyNotFoundException, DateTruncationException, ComputerException;

    /**
     * Supprime tout les computers ayant cette companie.
     * @param companyId
     *            ID de la companie
     * @throws ComputerNotDeletedException
     *             Si aucun ordinateur n'a été supprimé
     * @throws CompanyNotFoundException
     *             Si la companie n'existe pas
     */
    @Transactional(rollbackFor = { ComputerNotDeletedException.class, CompanyNotFoundException.class })
    void deleteByCompany(Long companyId) throws ComputerNotDeletedException, CompanyNotFoundException;
}
