package main.java.com.excilys.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.com.excilys.exception.ComputerNotFoundException;
import main.java.com.excilys.model.Company;
import main.java.com.excilys.model.Computer;

import main.java.com.excilys.service.ServiceCdb;

public class ControleurCdb {

	private static final String ID_COMPUTER_NUMBER_ONLY = "L'ID de l'ordinateur doit être composé uniquement de nombre [0-9] ";
	
	private static final String ID_COMPUTER_NUM_ONLY = "L'action n'a pas pu être réalisé car un nombre n'a pas été renseignée";

	private static final Logger LOGGER = LoggerFactory.getLogger(ControleurCdb.class);

	private static final String NUMBER_COMPUTER = "Quel est le numero de l'ordinateur ? : ";

	private static final String DATE_DISCONTINUED = "[date discontinued] : ";

	private static final String DATE_INTRODUCED = "[date introduced] :";

	private static final String COMPUTER_NAME = "[name] : ";

	private static final String MESSAGE_USER_COMPUTER = "Veuillez entrez les informations demandées ci-dessous (entrée pour passer) : ";

	private static final String DATE_INCORECTE = "Date incorrecte veuillez recommencer :";

	private static final String DATE_REGEX = "^((18|19|20|21)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])";

	private static final String DECORATE = "============== Gestion CDB ==============";

	private static final String CHOIX_USER = "Veuillez entrez le numéro de l'action souhaitez : ";

	private ServiceCdb serviceCdb = new ServiceCdb();

	private Scanner scanner;

	public ControleurCdb() {
		scanner = new Scanner(System.in);
	}

	/**
	 * Le coeur de l'interface utilisateur
	 */
	public void core() {
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
				printListCompanies();
				break;
			case FIND_ONE_COMPUTER :
				findOneComputer();
				break;
			case ADD_COMPUTER :
				final Computer computer = creation_computer();
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

	private void update_computer() {
		System.out.println(NUMBER_COMPUTER);
		try {
			final Long l = Long.valueOf(scanner.nextLine());
			final Computer computer2 = creation_computer();
			computer2.setId(l);
			// FIXME ID pas présent dans computer
			serviceCdb.updateComputer(computer2);
		}
		catch(NumberFormatException e) {
			LOGGER.debug(ID_COMPUTER_NUMBER_ONLY);
			System.out.println(ID_COMPUTER_NUM_ONLY);
	
		}
	}

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
	 * Permet de creer un computer à l'aide des choix et des informatiosn renseignées par l'utilisateur
	 */
	private Computer creation_computer() {
		Company company = null;
		System.out.println(MESSAGE_USER_COMPUTER);
		System.out.print(COMPUTER_NAME);
		final String computer_name = scanner.nextLine();

		System.out.print(DATE_INTRODUCED);
		String string_date_intro = scanner.nextLine();
		if(!string_date_intro.isEmpty()) {
			while(!string_date_intro.matches(DATE_REGEX)) {
				System.out.print(DATE_INCORECTE);
				string_date_intro = scanner.nextLine();
			}
		}
		
		System.out.print(DATE_DISCONTINUED);
		String string_date_discon = scanner.nextLine();
		if(!string_date_discon.isEmpty()) {
			while(!string_date_discon.matches(DATE_REGEX)) {
				System.out.print(DATE_INCORECTE);
				string_date_discon = scanner.nextLine();
			}
		}
		System.out.print("Voulez vous ajouter une companie à l'ordinateur ? : [y/n] ");
		switch (scanner.nextLine()) {
		case "y":
			company = new Company();
			System.out.print(COMPUTER_NAME);
			try {
				final Long company_id = Long.valueOf(scanner.nextLine());
				company.setId(company_id);
			}
			catch(NumberFormatException e) {
				LOGGER.debug(ID_COMPUTER_NUMBER_ONLY);
				System.out.println(ID_COMPUTER_NUM_ONLY);
			}
			
			break;
		default:
			break;
		}

		return new Computer(computer_name.length() == 0 ? null : computer_name,
				string_date_intro.isEmpty() ? null : LocalDate.parse(string_date_intro, DateTimeFormatter.ISO_LOCAL_DATE),
				string_date_discon.isEmpty() ? null : LocalDate.parse(string_date_discon, DateTimeFormatter.ISO_LOCAL_DATE), 
				company);
	}

	/**
	 * Demande à la DAO d'ajouter un computer à la base de donnée
	 * @param computer
	 */
	private void insert_computer(final Computer computer) {
		serviceCdb.createComputer(computer);
	}

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

	private void printListComputers() {
		for(Computer computer : serviceCdb.getListComputers()) {
			System.out.println(computer);
		}
	}

	private void printListCompanies() {
		for(Company companies : serviceCdb.getListCompanies()) {
			System.out.println(companies);
		}
	}

	private void printComputer(final Long id) {
		try {
			System.out.println(serviceCdb.getComputerDaoDetails(id));
		} catch (ComputerNotFoundException e) {
			LOGGER.info(e.getMessage());
		}
	}


}
