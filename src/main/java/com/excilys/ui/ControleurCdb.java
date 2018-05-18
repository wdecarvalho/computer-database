package com.excilys.ui;

import static com.excilys.ui.ChoixUtilisateur.AJOUTER_COMPANIE_TO_COMPUTER;
import static com.excilys.ui.ChoixUtilisateur.CHOIX_USER;
import static com.excilys.ui.ChoixUtilisateur.MESSAGE_USER_COMPUTER;
import static com.excilys.ui.ChoixUtilisateur.NUMBER_COMPANY;
import static com.excilys.ui.ChoixUtilisateur.NUMBER_COMPUTER;
import static com.excilys.ui.ChoixUtilisateur.PAGE_OR_QUIT;
import static com.excilys.ui.FormEntry.COMPANY_ID;
import static com.excilys.ui.FormEntry.COMPUTER_NAME;
import static com.excilys.ui.FormEntry.CURRENT;
import static com.excilys.ui.FormEntry.DATE_DISCONTINUED;
import static com.excilys.ui.FormEntry.DATE_INTRODUCED;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import com.excilys.exception.ComputerException;
import com.excilys.exception.company.CompanyNotFoundException;
import com.excilys.exception.computer.ComputerNameNotPresentException;
import com.excilys.exception.computer.ComputerNotFoundException;
import com.excilys.exception.date.DateTruncationException;
import com.excilys.model.Company;
import com.excilys.model.Computer;
import com.excilys.service.ServiceCompany;
import com.excilys.service.ServiceComputer;
import com.excilys.util.Pages;

@Controller
public class ControleurCdb {

    private static final String AU_REVOIR = "Au revoir ! ";

    private static final String COMPUTER_NOT_SAVE = "L'ordinateur n'a pas pu être sauvegardé car la date discontinued est inferieur a la date introduced";

    private static final String COMPUTER_NAME_MANDATORY = "Le nom de l'ordinateur est obligatoire ";

    private static final String COMPANY_NOT_FOUND = "Cette companie n'existe pas : ";

    private static final String DATE_INCORECTE = "Date incorrecte veuillez recommencer :";

    private static final String ID_COMPUTER_NUMBER_ONLY = "L'ID de %s doit être composé uniquement de nombre [0-9] ";

    private static final String ID_COMPUTER_NUM_ONLY = "L'action n'a pas pu être réalisé car " + ID_COMPUTER_NUMBER_ONLY
            + "\n";

    private static final String PAGE_NUMBER_ONLY = "Le numero de page doit être composé uniquement de nombre [0-9] ";

    private static final String PAGE_NUM_ONLY = "L'action n'a pas pu être réalisé car " + PAGE_NUMBER_ONLY;

    private static final Logger LOGGER = LoggerFactory.getLogger(ControleurCdb.class);

    private static final String DATE_REGEX = "^((18|19|20|21)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])";

    private static final String DECORATE = "============== Gestion CDB ==============";

    private ServiceComputer serviceComputer;

    private ServiceCompany serviceCompany;

    private Scanner scanner;

    /**
     * Contructeur de ControleurCdb.
     */
    public ControleurCdb() {
        scanner = new Scanner(System.in);
    }

    /**
     * Contructeur de ControleurCdb.
     * @param context
     *            ApplicationContext
     */
    public ControleurCdb(final ApplicationContext context) {
        scanner = new Scanner(System.in);
        serviceComputer = (ServiceComputer) context.getBean("serviceComputer");
        serviceCompany = (ServiceCompany) context.getBean("serviceCompany");
    }

    /**
     * Le coeur de l'interface utilisateur Initiliase le service puis affiche le
     * menu a l'utilisateur Pour chaque choix, effectue une fonctionnalité
     * disponible dans la couche de service.
     */
    public void core() {
        boolean run = true;
        while (run) {
            menu();
            ChoixUtilisateur choixUtilisateur = null;
            try {
                choixUtilisateur = ChoixUtilisateur.getChoix(Integer.parseInt(scanner.nextLine()));
                if (choixUtilisateur == null) {
                    System.out.println("Choix incorrecte !\n");
                    continue;
                }
            } catch (NumberFormatException e) {
                continue;
            }
            run = executeChoixUtilisateur(choixUtilisateur);
        }
        System.out.println(AU_REVOIR);
    }

    /**
     * En fonction du choix de l'utilisateur execute la bonne fonctionnalité.
     * @param choixUtilisateur
     *            Choix possible
     * @return boolean Si false le programme s'arrete.
     */
    private boolean executeChoixUtilisateur(final ChoixUtilisateur choixUtilisateur) {
        boolean run = true;
        switch (choixUtilisateur) {
        case LIST_COMPUTERS:
            printListComputers();
            break;
        case LIST_COMPANIES:
            printListCompanys();
            break;
        case FIND_ONE_COMPUTER:
            findOneComputer();
            break;
        case ADD_COMPUTER:
            final Computer computer = creationComputer(null);
            insertComputer(computer);
            break;
        case UPDATE_COMPUTER:
            updateComputer();
            break;
        case DELETE_COMPUTER:
            deleteComputer();
            break;
        case DELETE_COMPANY:
            deleteCompany();
            break;
        case QUIT:
            run = false;
            break;
        default:
            break;
        }
        return run;
    }

    /**
     * Supprime le computer ayant l'ID renseignée par l'utilisateur On recupere
     * l'ordinateur ayant le bonne ID en base de donnée Enfin on supprime
     * l'ordinateur.
     */
    private void deleteComputer() {
        System.out.println(NUMBER_COMPUTER);
        try {
            final Long l = Long.valueOf(scanner.nextLine());
            final Computer computer3 = serviceComputer.getComputerDaoDetails(l);
            serviceComputer.deleteOne(computer3.getId());
        } catch (NumberFormatException e) {
            LOGGER.debug(String.format(ID_COMPUTER_NUMBER_ONLY, "computer"));
            System.out.printf(ID_COMPUTER_NUM_ONLY, "computer");
        } catch (ComputerNotFoundException e) {
            LOGGER.info(e.getMessage());
        }
    }

    /**
     * Supprime la company ainsi que les computeur raccroché.
     */
    private void deleteCompany() {
        System.out.println(NUMBER_COMPANY);
        try {
            final Long l = Long.valueOf(scanner.nextLine());
            if (!serviceCompany.deleteOne(l)) {
                if (serviceCompany.isExistCompany(l)) {
                    LOGGER.info(
                            "La company n'a pas pu être supprimé a cause d'une dépendence à un ou plusieurs computers");
                } else {
                    LOGGER.info(String.format("La company d'id %s n'existe pas", l));
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.debug(String.format(ID_COMPUTER_NUMBER_ONLY, "company"));
            System.out.printf(ID_COMPUTER_NUM_ONLY, "company");
        }
    }

    /**
     * Met a jour le computer ayant l'ID renseignée par l'utilisateur On appele le
     * formulaire pour renseigner les champs d'un computer Et on demande au service
     * de mettre a jour le computer.
     */
    private void updateComputer() {
        System.out.println(NUMBER_COMPUTER);
        try {
            final Long l = Long.valueOf(scanner.nextLine());
            final Computer current = serviceComputer.getComputerDaoDetails(l);
            final Computer computer2 = creationComputer(current);
            computer2.setId(l);
            serviceComputer.updateComputer(computer2);
        } catch (NumberFormatException e) {
            LOGGER.debug(String.format(ID_COMPUTER_NUMBER_ONLY, "computer"));
            System.out.printf(ID_COMPUTER_NUM_ONLY, "computer");
        } catch (ComputerException e) {
            LOGGER.info(e.getMessage());
        } catch (DateTruncationException e) {
            LOGGER.error(e.getMessage());
        } catch (CompanyNotFoundException e) {
            LOGGER.info(e.getMessage());
        }
    }

    /**
     * On recupere le computer ayant l'ID renseignée par l'utilisateur Et on
     * l'affiche a l'utilisateur.
     */
    private void findOneComputer() {
        System.out.println(NUMBER_COMPUTER);
        try {
            final Long l = Long.valueOf(scanner.nextLine());
            printComputer(l);
        } catch (NumberFormatException e) {
            LOGGER.debug(String.format(ID_COMPUTER_NUMBER_ONLY, "computer"));
            System.out.printf(ID_COMPUTER_NUM_ONLY, "computer");
        }
    }

    /**
     * Permet de renseignées les informations d'un ordinateur.
     * @param computer
     *            Ancien computer ou null si nouveau
     * @return Computer computer crée
     */
    private Computer creationComputer(final Computer computer) {
        String currentName = null;
        LocalDate currentDateIntro = null;
        LocalDate currentDateDiscon = null;
        Company currentCompany = null;
        if (computer != null) {
            currentName = computer.getName();
            currentDateIntro = computer.getIntroduced();
            currentDateDiscon = computer.getDiscontinued();
            currentCompany = computer.getCompany();
        }
        Company company;
        System.out.println(MESSAGE_USER_COMPUTER);

        final String computerName = formAndInputNameComputer(currentName);

        final String stringDateIntro = formAndInputDateComputer(currentDateIntro, DATE_INTRODUCED);

        final String stringDateDiscon = formAndInputDateComputer(currentDateDiscon, DATE_DISCONTINUED);

        company = askforAddCompanieToComputer(currentCompany);
        return new Computer.Builder(computerName)
                .introduced(stringDateIntro.isEmpty() ? null
                        : LocalDate.parse(stringDateIntro, DateTimeFormatter.ISO_LOCAL_DATE))
                .discontinued(stringDateDiscon.isEmpty() ? null
                        : LocalDate.parse(stringDateDiscon, DateTimeFormatter.ISO_LOCAL_DATE))
                .company(company).build();
    }

    /**
     * Demande a l'utilisateur de renseigner la date de l'orindateur et la recupere.
     * @param currentDate
     *            Date actuelle
     * @param dateMsg
     *            Le message a destination de l'user
     * @return String correspond a la LocalDate
     */
    private String formAndInputDateComputer(final LocalDate currentDate, final FormEntry dateMsg) {
        if (currentDate != null) {
            final StringBuilder sBuilder = new StringBuilder(dateMsg.toString());
            sBuilder.append(CURRENT).append(currentDate.toString()).append(" ");
            System.out.print(sBuilder.toString());
        } else {
            System.out.print(dateMsg);
        }
        String stringDate = scanner.nextLine();
        if (!stringDate.isEmpty()) {
            while (!stringDate.matches(DATE_REGEX)) {
                System.out.print(DATE_INCORECTE);
                stringDate = scanner.nextLine();
            }
        }
        return stringDate;
    }

    /**
     * Demande a l'utilisateur de renseigner le nom de l'ordinateur et le recupere.
     * @param currentName
     *            Nom actuelle du computer si il existe
     * @return Le nouveau nom de l'ordinateur
     */
    private String formAndInputNameComputer(final String currentName) {
        if (currentName != null) {
            final StringBuilder sBuilder = new StringBuilder(COMPUTER_NAME.toString());
            sBuilder.append(CURRENT).append(currentName).append(" ");
            System.out.print(sBuilder.toString());
        } else {
            System.out.print(COMPUTER_NAME);
        }

        String computerName = scanner.nextLine();
        while (computerName.isEmpty()) {
            System.out.println(COMPUTER_NAME_MANDATORY);
            computerName = scanner.nextLine();
        }
        return computerName;
    }

    /**
     * Formulaire de remplissage des données d'une companie.
     * @param companyCurrent
     *            Companie courante
     * @return Null ou Company
     */
    private Company askforAddCompanieToComputer(Company companyCurrent) {
        Company company = null;
        System.out.print(AJOUTER_COMPANIE_TO_COMPUTER);
        switch (scanner.nextLine()) {
        case "y":
            if (companyCurrent != null) {
                final StringBuilder sBuilder = new StringBuilder(COMPANY_ID.toString());
                sBuilder.append(CURRENT).append(companyCurrent.getId()).append(" ");
                System.out.print(sBuilder.toString());
            } else {
                System.out.print(COMPANY_ID);
            }

            try {
                Long companyId = Long.valueOf(scanner.nextLine());
                while (!serviceCompany.isExistCompany(companyId)) {
                    System.out.println(COMPANY_NOT_FOUND + companyId);
                    companyId = Long.valueOf(scanner.nextLine());
                }
                company = new Company.Builder(companyId).build();
            } catch (NumberFormatException e) {
                LOGGER.debug(String.format(ID_COMPUTER_NUMBER_ONLY, "company"));
                System.out.printf(ID_COMPUTER_NUM_ONLY, "company");
                company = null;
            }

            break;
        default:
            break;
        }
        return company;
    }

    /**
     * Demande à la DAO d'ajouter un computer à la base de donnée.
     * @param computer
     *            computer a inserer
     * @throws ComputerNameNotPresentException
     */
    private void insertComputer(final Computer computer) {
        try {
            if (serviceComputer.createComputer(computer).equals(-1L)) {
                System.out.println(COMPUTER_NOT_SAVE);
            }
        } catch (CompanyNotFoundException e) {
            LOGGER.info(e.getMessage());
        } catch (ComputerException e) {
            LOGGER.info(e.getMessage());
        } catch (DateTruncationException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Menu choix des fonctionnalité de l'utilisateur.
     */
    private void menu() {
        System.out.println(DECORATE);
        System.out.println(ChoixUtilisateur.LIST_COMPUTERS);
        System.out.println(ChoixUtilisateur.LIST_COMPANIES);
        System.out.println(ChoixUtilisateur.FIND_ONE_COMPUTER);
        System.out.println(ChoixUtilisateur.ADD_COMPUTER);
        System.out.println(ChoixUtilisateur.UPDATE_COMPUTER);
        System.out.println(ChoixUtilisateur.DELETE_COMPUTER);
        System.out.println(ChoixUtilisateur.DELETE_COMPANY);
        System.out.println(ChoixUtilisateur.QUIT);
        System.out.print(CHOIX_USER);
    }

    /**
     * Affichage de la liste des computers par pages.
     */
    private void printListComputers() {
        printListComputersByPage(1);
        boolean run = true;
        while (run) {
            System.out.print(PAGE_OR_QUIT);
            final String choix = scanner.nextLine();
            if (choix.equals("quit")) {
                run = false;
            } else {
                try {
                    int page = Integer.parseInt(choix);
                    printListComputersByPage(page);
                } catch (NumberFormatException e) {
                    LOGGER.debug(PAGE_NUMBER_ONLY);
                    System.out.println(PAGE_NUM_ONLY);
                }

            }
        }

    }

    /**
     * Affichage de la liste des computers.
     * @param page
     *            : Numero de page a afficher
     */
    private void printListComputersByPage(int page) {
        final Pages<Computer> pages = serviceComputer.findByPage(page);
        for (Computer computer : pages.getEntities()) {
            System.out.println(computer);
        }
        printPageInformation(pages);
    }

    /**
     * Affichage de la liste des companys par pages.
     */
    private void printListCompanys() {
        printListCompaniesByPage(1);
        boolean run = true;
        while (run) {
            System.out.print(PAGE_OR_QUIT);
            final String choix = scanner.nextLine();
            if (choix.equals("quit")) {
                run = false;
            } else {
                try {
                    int page = Integer.parseInt(choix);
                    printListCompaniesByPage(page);
                } catch (NumberFormatException e) {
                    LOGGER.debug(PAGE_NUMBER_ONLY);
                    System.out.println(PAGE_NUM_ONLY);
                }

            }
        }

    }

    /**
     * Affichage de la liste des companies.
     * @param page
     *            Numero de page a afficher
     */
    private void printListCompaniesByPage(int page) {
        final Pages<Company> pages = serviceCompany.findByPage(page);
        for (Company company : pages.getEntities()) {
            System.out.println(company);
        }
        printPageInformation(pages);
    }

    /**
     * Affichage des informations d'un computer.
     * @param id
     *            ID de l'ordinateur a afficher
     */
    private void printComputer(final Long id) {
        try {
            System.out.println(serviceComputer.getComputerDaoDetails(id));
        } catch (ComputerNotFoundException e) {
            LOGGER.info(e.getMessage());
        }
    }

    /**
     * Affiche des informations sur l'objet page.
     * @param pages
     *            Pagination
     */
    private void printPageInformation(final Pages<?> pages) {
        final StringBuilder sBuilder = new StringBuilder("Page actuelle : ");
        sBuilder.append(pages.getPageCourante()).append(" / ").append(pages.getPageMax());
        System.out.println(sBuilder.toString());
    }

}
