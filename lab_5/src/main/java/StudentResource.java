import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;


@Path("students")
@XmlRootElement(name="students")
public class StudentResource {

    DataBase dataBase = DataBase.getInstance();
    @XmlElement(name="student")
    Collection<Student> students;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getAll() {
        students = dataBase.getStudents().values();
        return Response.status(200).entity(this).build();
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getStudent(@PathParam("id") int id) {
        Student student = dataBase.getStudents().get(id);
        if (student == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(200).entity(student).build();
    }



    @PUT
    @Path("{id}")
    public Response putStudent(Student entity, @PathParam("id") int id) {
        Student student = dataBase.getStudents().get(id);
        if (student == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        boolean modified = false;
        if (entity.getName()!=null) {
            student.setName(entity.getName());
            modified = true;
        }
        if (entity.getSurname()!=null){
            student.setSurname(entity.getSurname());
            modified = true;
        }
        if (entity.getBirthDate()!=null){
            student.setBirthDate(entity.getBirthDate());
            modified = true;
        }
        return Response.status(200).entity(entity).build();
    }


    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postStudent(Student entity) {
        if (dataBase.getStudents().containsKey(entity.getIndex())) {
           entity.setIndex(entity.generateIndex());

//            System.out.println(entity.getName());
//            System.out.println(entity.getGrades());
//            System.out.println(entity.getIndex());
//            System.out.println(entity.getSurname());
//            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (entity.getName()!=null && entity.getSurname()!=null && entity.getBirthDate()!=null) {
            dataBase.addStudent(entity);
            return Response.status(201).entity(entity).build();
        }
        return Response.status(400).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteStudent(@PathParam("id") int id){
        if (!dataBase.getStudents().containsKey(id))
            return Response.status(Response.Status.NOT_FOUND).build();

        dataBase.getStudents().remove(id);
        return Response.status(200).build();
    }


}

//mozna wylaczyc xmlelement z kolekcji ocen


