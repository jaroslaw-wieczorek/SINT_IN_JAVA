import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


@Path("subjects")
@XmlRootElement(name="subjects")
public class SubjectResource {


    DataBase dataBase = DataBase.getInstance();
    @XmlElement(name="subject")
    Collection<Subject> subjects;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getAll() {
        subjects = dataBase.getSubjects().values();
        return Response.status(200).entity(this).build();
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getSubject(@PathParam("id") int id) {
        Subject subject = dataBase.getSubjects().get(id);
        if (subject == null){
            return Response.status(404).build();
        }
        return Response.status(200).entity(subject).build();
    }

    @PUT
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response putSubject(Subject entity, @PathParam("id") int id) {
        Subject subject = dataBase.getSubjects().get(id);
        if (subject == null){
            return Response.status(404).build();
            //return postSubject(entity);
        }
        boolean modified = false;
        if (entity.getName()!=null){
            subject.setName(entity.getName());
            modified = true;
        }
        if (entity.getProfessor()!=null){
            subject.setProfessor(entity.getProfessor());
            modified = true;
        }
        String str = "subjects/"+entity.getId()+"/";
        Response.ResponseBuilder res = Response.ok(entity).header("Location", str);
        return res.status(200).entity(entity).build();
    }

    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postSubject(Subject entity){
        if (dataBase.getSubjects().containsKey(entity.getId())) {
            entity.setId(entity.generateId());
            //return Response.status(403).build();
        }
        if (entity.getProfessor()!=null & entity.getName()!=null){
            dataBase.addSubject(entity);

            String str = "subjects/"+entity.getId()+"/";
            Response.ResponseBuilder res = Response.ok(entity).header("Location", str);
            return res.status(201).entity(entity).build();
        }
        return Response.status(400).build();
    }


    @DELETE
    @Path("{id}")
    public Response deleteSubject(@PathParam("id") int id){
        if (!dataBase.getSubjects().containsKey(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Map<Integer, Subject> subjects = dataBase.getSubjects();
        if (subjects != null){
            Subject subject = subjects.get(id);
            if (subject != null){
                dataBase.removeSubject(subject);
                return Response.status(200).build();
            }
        }
        return Response.status(400).build();
    }

}


