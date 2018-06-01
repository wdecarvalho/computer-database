package com.excilys.ui;

import static com.excilys.ui.ChoixUtilisateur.AJOUTER_COMPANIE_TO_COMPUTER;
import static com.excilys.ui.ChoixUtilisateur.CHOIX_USER;
import static com.excilys.ui.ChoixUtilisateur.DATE_INCORRECTE;
import static com.excilys.ui.ChoixUtilisateur.MESSAGE_USER_COMPUTER;
import static com.excilys.ui.ChoixUtilisateur.NAME_REQUIRED;
import static com.excilys.ui.ChoixUtilisateur.NUMBER_COMPANY;
import static com.excilys.ui.ChoixUtilisateur.NUMBER_COMPUTER;
import static com.excilys.ui.ChoixUtilisateur.PAGE_OR_QUIT;
import static com.excilys.ui.FormEntry.COMPANY_ID;
import static com.excilys.ui.FormEntry.COMPUTER_NAME;
import static com.excilys.ui.FormEntry.CURRENT;
import static com.excilys.ui.FormEntry.DATE_DISCONTINUED;
import static com.excilys.ui.FormEntry.DATE_INTRODUCED;
import static com.excilys.ui.MessageInfoCli.AU_REVOIR;
import static com.excilys.ui.MessageInfoCli.CHOIX_INCORRECTE;
import static com.excilys.ui.MessageInfoCli.COMPANY_NOT_FOUND;
import static com.excilys.ui.MessageInfoCli.DELETE_ERROR_COMPANY;
import static com.excilys.ui.MessageInfoCli.DELETE_SUCCESSFULL;
import static com.excilys.ui.MessageInfoCli.ID_NUMBER_ONLY;
import static com.excilys.ui.MessageInfoCli.PAGE_NUMBER_ONLY;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.excilys.exception.ComputerException;
import com.excilys.exception.company.CompanyNotFoundException;
import com.excilys.exception.computer.ComputerNameNotPresentException;
import com.excilys.exception.computer.ComputerNotDeletedException;
import com.excilys.exception.computer.ComputerNotFoundException;
import com.excilys.exception.date.DateTruncationException;
import com.excilys.model.Company;
import com.excilys.model.Computer;
import com.excilys.service.company.ServiceCdbCompany;
import com.excilys.service.computer.ServiceCdbComputer;

@Component
public class ControleurCdb {

    private static final String LA_COMPANIE = "La companie";

    private static final String LE_COMPUTER = "Le computer";

    private static final String COMPANY = "company";

    private static final String COMPUTER = "computer";

    private static final String SERVICE_COMPANY = "serviceCompany";

    private static final String SERVICE_COMPUTER = "serviceComputer";

    private static final Logger LOGGER = LoggerFactory.getLogger(ControleurCdb.class);

    private static final String DATE_REGEX = "^((18|19|20|21)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])";

    private static final String DECORATE = "============== Gestion CDB ==============";

    private ServiceCdbComputer serviceComputer;

    private ServiceCdbCompany serviceCompany;

    private Scanner scanner;

    private static ApplicationContext context;

    /**
     * Contructeur de ControleurCdb.
     */
    public ControleurCdb() {
        scanner = new Scanner(System.in);
    }

    /**
     * Contructeur de ControleurCdb.
     * @param applicationContext
     *            ApplicationContext
     */
    public ControleurCdb(final ApplicationContext applicationContext) {
        scanner = new Scanner(System.in);
        context = applicationContext;
        serviceComputer = (ServiceCdbComputer) context.getBean(SERVICE_COMPUTER);
        serviceCompany = (ServiceCdbCompany) context.getBean(SERVICE_COMPANY);
    }

    public static ApplicationContext getApplicationContext() {
        return context;
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
                    System.out.println(CHOIX_INCORRECTE.toString());
                    continue;
                }
            } catch (NumberFormatException e) {
                continue;
            }
            run = executeChoixUtilisateur(choixUtilisateur);
        }
        System.out.println(AU_REVOIR.toString());
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
            LOGGER.info(String.format(DELETE_SUCCESSFULL.toString(), LE_COMPUTER, l));
        } catch (NumberFormatException e) {
            LOGGER.info(String.format(ID_NUMBER_ONLY.toString(), COMPUTER));
        } catch (ComputerException | CompanyNotFoundException e) {
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
            serviceCompany.deleteOne(l);
            LOGGER.info(String.format(DELETE_SUCCESSFULL.toString(), LA_COMPANIE, l));
        } catch (NumberFormatException e) {
            LOGGER.info(String.format(ID_NUMBER_ONLY.toString(), COMPANY));
        } catch (ComputerNotDeletedException | CompanyNotFoundException e) {
            LOGGER.info(DELETE_ERROR_COMPANY.toString());
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
            serviceComputer.update(computer2, true);
        } catch (NumberFormatException e) {
            LOGGER.info(String.format(ID_NUMBER_ONLY.toString(), COMPUTER));
        } catch (ComputerException | CompanyNotFoundException e) {
            LOGGER.info(e.getMessage());
        } catch (DateTruncationException e) {
            LOGGER.error(e.getMessage());
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
            LOGGER.info(String.format(ID_NUMBER_ONLY.toString(), COMPUTER));
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
        LocalDate dateIntro = null, dateDiscon = null;
        try {
            if (!stringDateIntro.isEmpty()) {
                dateIntro = LocalDate.parse(stringDateIntro, DateTimeFormatter.ISO_LOCAL_DATE);
            }
        } catch (DateTimeParseException e) {
            LOGGER.warn(new DateTruncationException().getMessage());
        }
        try {
            if (!stringDateDiscon.isEmpty()) {
                dateDiscon = LocalDate.parse(stringDateDiscon, DateTimeFormatter.ISO_LOCAL_DATE);
            }
        } catch (DateTimeParseException e) {
            LOGGER.warn(new DateTruncationException().getMessage());
        }
        return new Computer.Builder(computerName).introduced(dateIntro).discontinued(dateDiscon).company(company)
                .build();
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
                System.out.print(DATE_INCORRECTE.toString());
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
            System.out.println(NAME_REQUIRED.toString());
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
                while (!serviceCompany.isExists(companyId)) {
                    System.out.println(COMPANY_NOT_FOUND.toString() + companyId);
                    companyId = Long.valueOf(scanner.nextLine());
                }
                company = new Company.Builder(companyId).build();
            } catch (NumberFormatException e) {
                LOGGER.info(String.format(ID_NUMBER_ONLY.toString(), COMPANY));
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
            serviceComputer.save(computer, true);
        } catch (CompanyNotFoundException | ComputerException | DateTruncationException e) {
            LOGGER.info(e.getMessage());
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
                    LOGGER.info(PAGE_NUMBER_ONLY.toString());
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
        final Page<Computer> pages = serviceComputer.findByPage(page);
        for (Computer computer : pages.getContent()) {
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
                    LOGGER.info(PAGE_NUMBER_ONLY.toString());
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
        final Page<Company> pages = serviceCompany.findByPage(page);
        for (Company company : pages.getContent()) {
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
    private void printPageInformation(final Page<?> pages) {
        final StringBuilder sBuilder = new StringBuilder("Page actuelle : ");
        sBuilder.append(pages.getNumber() + 1).append(" / ").append(pages.getTotalPages());
        System.out.println(sBuilder.toString());
    }

}
