import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataBase {
    private volatile static DataBase instance;

    private DataBase() {
    }

    public static DataBase getInstance() {
        if (instance == null) {
            synchronized (DataBase.class) {
                if (instance == null) {
                    instance = new DataBase();
                }
            }
        }
        return instance;
    }

    private Map<Integer, Student> students = new HashMap<>();
    private Map<Integer, Subject> subjects = new HashMap<>();

    public Map<Integer, Student> getStudents() {
        return this.students;
    }

    public void addStudent(Student student) {
        students.put(student.getIndex(), student);
    }
    public void removeStudent(Student student) {
        students.remove(student.getIndex());
    }

    public Map<Integer, Subject> getSubjects() {
        return this.subjects;
    }

    public void addSubject(Subject subject) {
        this.subjects.put(subject.getId(), subject);
    }

//    public  Subject getSubject(int studentId, int subjectId)
//    {
//        Student student = students.get(studentId);
//
//        if (student != null)
//            {
//    }

    public Grade getGrade(int studentId, int gradeId) {
        Student student = students.get(studentId);
        if (student != null) {

            ArrayList<Grade> grades = student.getGrades();
            if ((grades.size() >= 1 ) && (grades.size() > gradeId)) {
                Grade grade = grades.get(gradeId);
                System.out.println(grades);
                if (grade != null) {
                    return grade;
                }
            }
            return null;
        }
        return null;
    }
//    public void removeStudentGrades(int studentId) {
//        Map<Integer, Student> students = getStudents();
//        Student student = students.get(studentId);
//        for (Grade gr: student.getGrades())
//        {
//            student.deleteGrades();
//
//        }
//    }
}