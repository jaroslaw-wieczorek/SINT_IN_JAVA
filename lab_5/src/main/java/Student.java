import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@XmlRootElement(name="student")
@XmlAccessorType(XmlAccessType.NONE)
public class Student {

    public static int lastIndex = 0;
    private static int indexdToSet = 0;
    public synchronized int generateIndex(){
        System.out.println("Generate student index" + lastIndex);
        indexdToSet = lastIndex;
        lastIndex += 1;
        return indexdToSet;
    }

    Student(){}
    Student( String name, String surname, Date birthDate){
        this.index = generateIndex();
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.grades = new ArrayList<>();
    }

    @XmlElement(name="index")
    private int index;
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    @XmlElement(name="name", required=true, nillable=false)
    private String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @XmlElement(name="surname")
    private String surname;
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    @XmlElement
    @XmlSchemaType(name="date")
    private Date birthDate;
    public Date getBirthDate(){ return birthDate; }
    public void setBirthDate(Date birthDate){ this.birthDate = birthDate; }


    @XmlElement
    @XmlSchemaType(name="grades")
    private ArrayList<Grade> grades;
    public ArrayList<Grade> getGrades() { return grades; }
    public void setGrades(ArrayList<Grade> grades) { this.grades = grades; }
    public void deleteGrades() { this.grades.clear(); }
    public void deleteGrade(int gradeId) { this.grades.remove(gradeId); }

//
//    public ArrayList<Grade> getSubjects() {
//
//        for (Grade gr: grades) {
//            Subject subject = (gr.getSubject().getId(), gr.getSubject());
//        }
//    }

}