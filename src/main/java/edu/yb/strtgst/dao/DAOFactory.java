package edu.yb.strtgst.dao;

import edu.yb.strtgst.dao.custom.impl.*;

public class DAOFactory {
    private static DAOFactory daoFactory;
    private DAOFactory() {}
    public static DAOFactory getInstance() {
        return (daoFactory == null) ? daoFactory = new DAOFactory() : daoFactory;
    }
    public enum DAOTYPES{
        ACADEMIC, TASK, SUBJECT, STUDENT, ASSIGNMENT;
    }
    public SuperDAO getDAO(DAOTYPES daoTypes){
        switch (daoTypes){
            case ACADEMIC:
                return new AcademicDAOImpl();
            case ASSIGNMENT:
                return new AssignmentDAOImpl();
            case TASK:
                return new TaskDAOImpl();
            case SUBJECT:
                return new SubjectDAOImpl();
            case STUDENT:
                return new StudentDAOImpl();
            default:
                return null;
        }
    }
}
