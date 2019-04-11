import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.xpath.XPath;
import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Path("students/{studentId}/grades")
@XmlRootElement(name="grades")
public class GradeResource {


    DataBase dataBase = DataBase.getInstance();
    @XmlElement(name = "grade")
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
    public Response putGrade(Grade entity, @PathParam("studentId") int studentId, @PathParam("gradeId") int gradeId) {
        Grade grade = dataBase.getGrade(studentId, gradeId);
        if (grade == null) {
            return Response.status(404).build();
        }
        boolean modified = false;
        if (entity.getDate() != null) {
            grade.setDate(entity.getDate());
            modified = true;
        }
        if (entity.getSubject() != null) {
            grade.setSubject(entity.getSubject());
            modified = true;
        }
        if (entity.getValue() != null) {
            grade.setValue(entity.getValue());
            modified = true;
        }
        String str = "students/" + studentId + "/grades/" + gradeId;
        Response.ResponseBuilder res = Response.ok(entity).header("Location", str);
        return res.status(200).entity(entity).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})

    public Response postGrade(Grade entity, @PathParam("studentId") int studentId) {

        int subjectId = entity.getSubject().getId();
        System.out.println(subjectId);
        if (dataBase.getSubjects().get(subjectId) == null) {
            return Response.status(400).build();

        } else {

            Student student = dataBase.getStudents().get(studentId);

            if (student != null) {
                ArrayList<Grade> grades = student.getGrades();
                if (grades != null) {
                    entity.setId(entity.generateId());
                    entity.setSubject(dataBase.getSubjects().get(subjectId));
                    for (Grade gr : grades) {
                        if (gr.getId().equals(entity.getId())) {
                            return Response.status(400).build();
                        }
                    }
                    grades.add(entity);
                    String str = "students/" + studentId + "/grades/" + entity.getId();
                    Response.ResponseBuilder res = Response.ok(entity).header("Location", str);
                    return res.status(201).entity(entity).build();
                }
            }
            return Response.status(400).build();
        }
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




