package main.java.com.excilys.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.com.excilys.exception.ComputerNotFoundException;
import main.java.com.excilys.exception.DaoNotInitializeException;
import main.java.com.excilys.model.Company;
import main.java.com.excilys.model.Computer;

import static main.java.com.excilys.ui.ChoixUtilisateur.*;
import static main.java.com.excilys.ui.FormEntry.*;
import main.java.com.excilys.service.ServiceCdb;
import main.java.com.excilys.util.Pages;

public class ControleurCdb {

	private static final String COMPUTER_NOT_SAVE = "L'ordinateur n'a pas pu être sauvegardé car la date discontinued est inferieur a la date introduced";

	private static final String COMPUTER_NAME_MANDATORY = "Le nom de l'ordinateur est obligatoire ";

	private static final String COMPANY_NOT_FOUND = "Cette companie n'existe pas : ";

	private static final String DATE_INCORECTE = "Date incorrecte veuillez recommencer :";
	
	private static final String ID_COMPUTER_NUMBER_ONLY = "L'ID de l'ordinateur doit être composé uniquement de nombre [0-9] ";
	
	private static final String ID_COMPUTER_NUM_ONLY = "L'action n'a pas pu être réalisé car "+ID_COMPUTER_NUMBER_ONLY;
	
	private static final String PAGE_NUMBER_ONLY = "Le numero de page doit être composé uniquement de nombre [0-9] ";
	
	private static final String PAGE_NUM_ONLY = "L'action n'a pas pu être réalisé car "+PAGE_NUMBER_ONLY;

	private static final Logger LOGGER = LoggerFactory.getLogger(ControleurCdb.class);

	private static final String DATE_REGEX = "^((18|19|20|21)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])";

	private static final String DECORATE = "============== Gestion CDB ==============";

	private ServiceCdb serviceCdb;

	private Scanner scanner;

	public ControleurCdb() {
		scanner = new Scanner(System.in);
	}

	/**
	 * Le coeur de l'interface utilisateur 
	 * Initiliase le service puis affiche le menu a l'utilisateur
	 * Pour chaque choix, effectue une fonctionnalité disponible dans la couche de service
	 */
	public void core() {
		try {
			serviceCdb = new ServiceCdb();
		} catch (DaoNotInitializeException e1) { 
			LOGGER.error(e1.getMessage());
			return;
		}
		boolean run = true;
		while(run) {
			menu();
			ChoixUtilisateur choixUtilisateur = null;
			try {
				choixUtilisateur =  ChoixUtilisateur.getChoix(Integer.parseInt(scanner.nextLine()));
			}
			catch(NumberFormatException e) {
				e.getMessage();
				continue;
			}
			switch (choixUtilisateur) {
			case LIST_COMPUTERS :
				printListComputers();
				break;
			case LIST_COMPANIES :
				printListCompanys();
				break;
			case FIND_ONE_COMPUTER :
				findOneComputer();
				break;
			case ADD_COMPUTER :
				final Computer computer = creation_computer(null);
				insert_computer(computer);
				break;
			case UPDATE_COMPUTER :
				update_computer();
				break;
			case DELETE_COMPUTER :
				delete_computer();	
				break;
			default:
				break;
			}
		}
	}
	/**
	 * Supprime le computer ayant l'ID renseignée par l'utilisateur
	 * On recupere l'ordinateur ayant le bonne ID en base de donnée
	 * Enfin on supprime l'ordinateur
	 * 
	 */
	private void delete_computer() {
		System.out.println(NUMBER_COMPUTER);
		try {
			final Long l = Long.valueOf(scanner.nextLine());
			final Computer computer3 = serviceCdb.getComputerDaoDetails(l);
			serviceCdb.deleteComputer(computer3);
		} catch(NumberFormatException e) {
			LOGGER.debug(ID_COMPUTER_NUMBER_ONLY);
			System.out.println(ID_COMPUTER_NUM_ONLY);
		} catch (ComputerNotFoundException e) {
			LOGGER.info(e.getMessage());
		}
	}
	/**
	 * Met a jour le computer ayant l'ID renseignée par l'utilisateur
	 * On appele le formulaire pour renseigner les champs d'un computer
	 * Et on demande au service de mettre a jour le computer
	 */
	private void update_computer() {
		System.out.println(NUMBER_COMPUTER);
		try {
			final Long l = Long.valueOf(scanner.nextLine());
			final Computer current = serviceCdb.getComputerDaoDetails(l);
			final Computer computer2 = creation_computer(current);
			computer2.setId(l);
			if(!serviceCdb.updateComputer(computer2)) {
				System.out.println(COMPUTER_NOT_SAVE);
			}
		
		} catch(NumberFormatException e) {
			LOGGER.debug(ID_COMPUTER_NUMBER_ONLY);
			System.out.println(ID_COMPUTER_NUM_ONLY);
	
		} catch (ComputerNotFoundException e) {
			LOGGER.info(e.getMessage());
		}
	}
	/**
	 * On recupere le computer ayant l'ID renseignée par l'utilisateur
	 * Et on l'affiche a l'utilisateur
	 */
	private void findOneComputer() {
		System.out.println(NUMBER_COMPUTER);
		try {
			final Long l = Long.valueOf(scanner.nextLine());
			printComputer(l);
		}
		catch(NumberFormatException e) {
			LOGGER.debug(ID_COMPUTER_NUMBER_ONLY);
			System.out.println(ID_COMPUTER_NUM_ONLY);
		}
	}
	/**
	 * Permet de renseignées les informations d'un ordinateur
	 */
	private Computer creation_computer(Computer computer) {
		String current_name = null;
		LocalDate current_date_intro = null;
		LocalDate current_date_discon = null;
		Company current_company = null;
		if(computer != null) {
			current_name = computer.getName();
			current_date_intro = computer.getIntroduced();
			current_date_discon = computer.getDiscontinued();
			current_company = computer.getCompany();
		}
		Company company;
		System.out.println(MESSAGE_USER_COMPUTER);
		
		if(current_name != null) {
			final StringBuilder sBuilder = new StringBuilder(COMPUTER_NAME.toString());
			sBuilder.append("[current :] ").append(current_name).append(" ");
			System.out.print(sBuilder.toString());
		}
		else {
			System.out.print(COMPUTER_NAME);
		}
		
		String computer_name = scanner.nextLine();
		while(computer_name.isEmpty()) {
			System.out.println(COMPUTER_NAME_MANDATORY);
			computer_name = scanner.nextLine();
		}
		
		if(current_date_intro != null) {
			final StringBuilder sBuilder = new StringBuilder(DATE_INTRODUCED.toString());
			sBuilder.append("[current :] ").append(current_date_intro.toString()).append(" ");
			System.out.print(sBuilder.toString());
		}
		else {
			System.out.print(DATE_INTRODUCED);
		}
		String string_date_intro = scanner.nextLine();
		if(!string_date_intro.isEmpty()) {
			while(!string_date_intro.matches(DATE_REGEX)) {
				System.out.print(DATE_INCORECTE);
				string_date_intro = scanner.nextLine();
			}
		}
		if(current_date_discon != null) {
			final StringBuilder sBuilder = new StringBuilder(DATE_DISCONTINUED.toString());
			sBuilder.append("[current :] ").append(current_date_discon.toString()).append(" ");
			System.out.print(sBuilder.toString());
		}
		else {
			System.out.print(DATE_DISCONTINUED);
		}
		String string_date_discon = scanner.nextLine();
		if(!string_date_discon.isEmpty()) {
			while(!string_date_discon.matches(DATE_REGEX)) {
				System.out.print(DATE_INCORECTE);
				string_date_discon = scanner.nextLine();
			}
		}
		company = askforAddCompanieToComputer(current_company);

		return new Computer(computer_name.length() == 0 ? null : computer_name,
				string_date_intro.isEmpty() ? null : LocalDate.parse(string_date_intro, DateTimeFormatter.ISO_LOCAL_DATE),
				string_date_discon.isEmpty() ? null : LocalDate.parse(string_date_discon, DateTimeFormatter.ISO_LOCAL_DATE), 
				company);
	}

	/**
	 * Formulaire de remplissage des données d'une companie
	 * @param company
	 * @return Null ou Company
	 */
	private Company askforAddCompanieToComputer(Company company_current) {
		Company company = null;
		System.out.print(AJOUTER_COMPANIE_TO_COMPUTER);
		switch (scanner.nextLine()) {
		case "y":
			company = new Company();
			if(company_current != null) {
				final StringBuilder sBuilder = new StringBuilder(COMPANY_ID.toString());
				sBuilder.append("[current :] ").append(company_current.getId()).append(" ");
				System.out.print(sBuilder.toString());
			}
			else {
				System.out.print(COMPANY_ID);
			}
			
			try {
				Long company_id = Long.valueOf(scanner.nextLine());
				while(!serviceCdb.isExistCompany(company_id)) {
					System.out.println(COMPANY_NOT_FOUND+company_id);
					company_id = Long.valueOf(scanner.nextLine());
				}
				company.setId(company_id);
			}
			catch(NumberFormatException e) {
				LOGGER.debug(ID_COMPUTER_NUMBER_ONLY);
				System.out.println(ID_COMPUTER_NUM_ONLY);
				company = null;
			}
			
			break;
		default:
			break;
		}
		return company;
	}

	/**
	 * Demande à la DAO d'ajouter un computer à la base de donnée
	 * @param computer
	 */
	private void insert_computer(final Computer computer) {
		if(!serviceCdb.createComputer(computer)) {
			System.out.println(COMPUTER_NOT_SAVE);
		}
	}

	/**
	 * Menu choix des fonctionnalité de l'utilisateur
	 */
	private void menu() {
		System.out.println(DECORATE);
		System.out.println(ChoixUtilisateur.LIST_COMPUTERS);
		System.out.println(ChoixUtilisateur.LIST_COMPANIES);
		System.out.println(ChoixUtilisateur.FIND_ONE_COMPUTER);
		System.out.println(ChoixUtilisateur.ADD_COMPUTER);
		System.out.println(ChoixUtilisateur.UPDATE_COMPUTER);
		System.out.println(ChoixUtilisateur.DELETE_COMPUTER);
		System.out.print(CHOIX_USER);
	}

	/**
	 * Affichage de la liste des computers par pages
	 */
	private void printListComputers() {
		printListComputersByPage(1);
		boolean run = true;
		while(run) {
			System.out.print(PAGE_OR_QUIT);
			final String choix = scanner.nextLine();
			if(choix.equals("quit")) {
				run = false;
			}
			else {
				try {
					int page = Integer.parseInt(choix);
					printListComputersByPage(page);
				}
				catch(NumberFormatException e) {
					LOGGER.debug(PAGE_NUMBER_ONLY);
					System.out.println(PAGE_NUM_ONLY);
				}
				
				
			}
		}
		
	}
	
	/**
	 * Affichage de la liste des computers
	 */
	private void printListComputersByPage(int page) {
		final Pages<Computer> pages = serviceCdb.findByPagesComputer(page);
		for(Computer computer : pages.getEntities()) {
			System.out.println(computer);
		}
		printPageInformation(pages);
	}

	/**
	 * Affichage de la liste des companys par pages
	 */
	private void printListCompanys() {
		printListCompaniesByPage(1);
		boolean run = true;
		while(run) {
			System.out.print(PAGE_OR_QUIT);
			final String choix = scanner.nextLine();
			if(choix.equals("quit")) {
				run = false;
			}
			else {
				try {
					int page = Integer.parseInt(choix);
					printListCompaniesByPage(page);
				}
				catch(NumberFormatException e) {
					LOGGER.debug(PAGE_NUMBER_ONLY);
					System.out.println(PAGE_NUM_ONLY);
				}
				
				
			}
		}
		
	}
	
	/**
	 * Affichage de la liste des companies
	 */
	private void printListCompaniesByPage(int page) {
		final Pages<Company> pages = serviceCdb.findByPagesCompany(page);
		for(Company company : pages.getEntities()) {
			System.out.println(company);
		}
		printPageInformation(pages);
	}

	/**
	 * Affichage des informations d'un computer
	 * @param id ID de l'ordinateur a afficher
	 */
	private void printComputer(final Long id) {
		try {
			System.out.println(serviceCdb.getComputerDaoDetails(id));
		} catch (ComputerNotFoundException e) {
			LOGGER.info(e.getMessage());
		}
	}
	
	/**
	 * Affiche des informations sur l'objet page
	 * @param pages Pagination
	 */
	private void printPageInformation(final Pages<?> pages) {
		final StringBuilder sBuilder = new StringBuilder("Page actuelle : ");
		sBuilder.append(pages.getPage_courante()).append(" / ").append(pages.getPage_max());
		System.out.println(sBuilder.toString());
	}	


}