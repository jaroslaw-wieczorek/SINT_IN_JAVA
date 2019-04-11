import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SINTRest {

    public static void main(String[] args){
            DataBase dataBase = DataBase.getInstance();
            createData(dataBase);


            URI baseUri = UriBuilder.fromUri("http://localhost/").port(8000).build();
            ResourceConfig config = new ResourceConfig(SubjectResource.class);
            config.register(StudentResource.class);
            config.register(GradeResource.class);

            Logger l = Logger.getLogger("org.glassfish.grizzly.http.server.HttpHandler");
            l.setLevel(Level.FINE);
            l.setUseParentHandlers(false);
            ConsoleHandler ch = new ConsoleHandler();
            ch.setLevel(Level.ALL);
            l.addHandler(ch);

            HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);

    }

    public static void createData(DataBase dataBase){

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Student student1 = new Student("Jaro", "Jaro", dateFormat.parse("16-02-1995"));
            Student student2 = new Student("Seba", "Seba", dateFormat.parse("26-11-1095"));
            Student student3 = new Student("Jan", "Kowalski",dateFormat.parse("14-06-1992"));
            dataBase.addStudent(student1);
            dataBase.addStudent(student2);
            dataBase.addStudent(student3);
            Subject subject1 = new Subject("Analiza Matematyczna", "Stefan Banach");
            Subject subject2 = new Subject("Algebra Liniowa", "Małgorzata Zygora");
            dataBase.addSubject(subject1);
            dataBase.addSubject(subject2);
            Grade grade1 = new Grade(5f, new Date(), subject1);
            ArrayList<Grade> gradesS1 = new ArrayList<Grade>();
            gradesS1.add(grade1);
            student1.setGrades(gradesS1);

        } catch (Exception e){
            e.printStackTrace();
            System.out.println("error while creating data");
        }

    }

}
