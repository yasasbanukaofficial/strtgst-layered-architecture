package edu.yb.strtgst.bo;

import edu.yb.strtgst.bo.custom.StudentBO;
import edu.yb.strtgst.bo.custom.impl.StudentBOImpl;

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
        ASSIGNMENT
    }

    public SuperBO getBO(BOType types){
        switch (types){
            case STUDENT:
                return new StudentBOImpl();
            default:
                return null;
        }
    }
}
