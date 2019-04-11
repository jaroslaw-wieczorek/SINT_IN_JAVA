import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Path("students/{studentId}/grades")
@XmlRootElement(name="grades")
public class GradeResource {


    DataBase dataBase = DataBase.getInstance();
    @XmlElement(name="grade")
    List<Grade> grades;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getAllGrades(@PathParam("studentId") int studentId) {
        Student student = dataBase.getStudents().get(studentId);
        if (student != null) {
            grades = student.getGrades();
            return Response.status(200).entity(this).build();
        }
        return Response.status(404).build();
    }

    @GET
    @Path("{gradeId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getGrade(@PathParam("studentId") int studentId, @PathParam("gradeId") int gradeId) {
        Grade grade = dataBase.getGrade(studentId, gradeId);
        if (grade != null) {
            return Response.status(200).entity(grade).build();
        }
        return Response.status(404).build();
    }

    @PUT
    @Path("{gradeId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response putGrade(Grade entity, @PathParam("studentId") int studentId, @PathParam("gradeId") int gradeId){
        Grade grade = dataBase.getGrade(studentId, gradeId);
        if (grade == null){
            return Response.status(404).build();
        }
        boolean modified = false;
        if (entity.getDate()!=null){
            grade.setDate(entity.getDate());
            modified = true;
        }
        if (entity.getSubject()!=null){
            grade.setSubject(entity.getSubject());
            modified = true;
        }
        if (entity.getValue()!=null){
            grade.setValue(entity.getValue());
            modified = true;
        }
        return Response.status(200).entity(entity).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postGrade(Grade entity, @PathParam("studentId") int studentId){

//        Grade grade1 = new Grade(5f, new Date(), subject1);
//        ArrayList<Grade> gradesS1 = new ArrayList<Grade>();
//        gradesS1.add(grade1);
//        student1.setGrades(gradesS1);
        if (!dataBase.getSubjects().containsKey(entity.getSubject())
            & entity.getSubject()!=null
            & entity.getValue()!=null
            & entity.getDate()!=null) {
                entity.setId(entity.generateId());
                Student student = dataBase.getStudents().get(studentId);
                if (student != null) {
                    ArrayList<Grade> grades = student.getGrades();
                    if (grades != null) {
                        for (Grade gr: grades)
                        {
                            if (gr.getId() == entity.getId()) {
                                return Response.status(400).build();
                            }

                        }
                        student.getGrades().add(entity);
                        return Response.status(201).entity(entity).build();
                    }
                }
        }
        return Response.status(400).build();
    }

    @DELETE
    @Path("{gradeId}")
    public Response deleteGrade(@PathParam("studentId") int studentId, @PathParam("gradeId") int gradeId){
        if (dataBase.getGrade(studentId, gradeId) != null){
            dataBase.getStudents().get(studentId).getGrades().remove(gradeId);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }
}




