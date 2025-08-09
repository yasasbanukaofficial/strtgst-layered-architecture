package edu.yb.strtgst.bo;

import edu.yb.strtgst.bo.custom.impl.*;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory(){}

    public static BOFactory getInstance(){
        return (boFactory == null) ? boFactory = new BOFactory() : boFactory;
    }

    public enum BOTypes {
        STUDENT,
        SUBJECT,
        TASK,
        ASSIGNMENT,
        ACADEMIC,
        CALENDAR
    }

    public SuperBO getBO(BOTypes types){
        switch (types){
            case STUDENT:
                return new StudentBOImpl();
            case SUBJECT:
                return new SubjectBOImpl();
            case ASSIGNMENT:
                return new AssignmentBOImpl();
            case ACADEMIC:
                return new AcademicBOImpl();
            case TASK:
                return new TaskBOImpl();
            case CALENDAR:
                return new CalendarBOImpl();
            default:
                return null;
        }
    }
}
