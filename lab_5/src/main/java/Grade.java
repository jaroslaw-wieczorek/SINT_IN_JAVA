import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Grade {

    public static int lastId = 0;
    private static int idToSet = 0;
    public synchronized int generateId(){
        System.out.println("Generate grade id" + lastId);
        idToSet = lastId;
        lastId += 1;
        return idToSet;
    }

    Grade(){}
    Grade(float value, Date date, Subject subject){
        this.id = generateId();
        this.value = value;
        this.date = date;
        this.subject = subject;
    }


    @XmlElement
    private Integer id;
    public Integer getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    private Float value;
    public Float getValue() { return value; }
    public void setValue(float value) { this.value = value; }

    @XmlElement
    private Date date;
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    @XmlElement
    private Subject subject;
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

}
