import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.excilys.ui.ControleurCdb;

public class Main {

    /**
     * Point d'entrée du programme pour la JVM : Lance la methode core.
     * @param args
     *            Argument entrée du programme
     */
    public static void main(String... args) {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(CliConfiguration.class);
        new ControleurCdb(context).core();
        context.close();
    }

}
