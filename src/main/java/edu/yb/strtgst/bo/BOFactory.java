package edu.yb.strtgst.bo;

import edu.yb.strtgst.bo.custom.StudentBO;
import edu.yb.strtgst.bo.custom.impl.AcademicBOImpl;
import edu.yb.strtgst.bo.custom.impl.AssignmentBOImpl;
import edu.yb.strtgst.bo.custom.impl.StudentBOImpl;
import edu.yb.strtgst.bo.custom.impl.SubjectBOImpl;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory(){}

    public static BOFactory getInstance(){
        return (boFactory == null) ? boFactory = new BOFactory() : boFactory;
    }

    public enum BOType{
        STUDENT,
        STUDENT_STUDY_SESSIONS,
        SUBJECT,
        SUBJECT_STUDY_SESSIONS,
        TASK,
        GRADE,
        ASSIGNMENT,
        ACADEMIC
    }

    public SuperBO getBO(BOType types){
        switch (types){
            case STUDENT:
                return new StudentBOImpl();
            case SUBJECT:
                return new SubjectBOImpl();
            case ASSIGNMENT:
                return new AssignmentBOImpl();
            case ACADEMIC:
                return new AcademicBOImpl();
            default:
                return null;
        }
    }
}
