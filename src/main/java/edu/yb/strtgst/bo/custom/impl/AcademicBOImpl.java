package edu.yb.strtgst.bo.custom.impl;

import edu.yb.strtgst.bo.BOFactory;
import edu.yb.strtgst.bo.custom.AcademicBO;
import edu.yb.strtgst.bo.custom.SubjectBO;
import edu.yb.strtgst.dao.DAOFactory;
import edu.yb.strtgst.dao.custom.AcademicDAO;
import edu.yb.strtgst.dao.custom.impl.AcademicDAOImpl;
import edu.yb.strtgst.dto.AcademicDto;
import edu.yb.strtgst.dto.SubjectDto;
import edu.yb.strtgst.entity.Academic;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.IdLoader;

import java.sql.SQLException;
import java.util.ArrayList;

public class AcademicBOImpl implements AcademicBO {
    AcademicDAO academicDAO = (AcademicDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTYPES.ACADEMIC);
    SubjectBO subjectBO = (SubjectBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.SUBJECT);

    @Override
    public boolean addAcademic(AcademicDto academicDto) throws SQLException {
        return academicDAO.addEntity(new Academic(
                academicDto.getId(),
                academicDto.getTitle(),
                academicDto.getLocation(),
                academicDto.getIsFullDay(),
                academicDto.getFromDateTime(),
                academicDto.getToDateTime(),
                academicDto.getRepeatType()
        ));
    }

    @Override
    public boolean updateAcademic(AcademicDto academicDto) throws SQLException {
        return academicDAO.updateEntity(new Academic(
                academicDto.getId(),
                academicDto.getTitle(),
                academicDto.getLocation(),
                academicDto.getIsFullDay(),
                academicDto.getFromDateTime(),
                academicDto.getToDateTime(),
                academicDto.getRepeatType()
        ));
    }

    @Override
    public boolean deleteAcademic(String id) throws SQLException {
        return academicDAO.deleteEntity(id);
    }

    @Override
    public ArrayList<AcademicDto> getAllAcademics() throws SQLException {
        ArrayList<Academic> academics = academicDAO.getAll();
        ArrayList<AcademicDto> academicDtos = new ArrayList<>();
        for (Academic academic : academics) {
            academicDtos.add(new AcademicDto(
                    academic.getId(),
                    academic.getTitle(),
                    academic.getLocation(),
                    academic.getIsFullDay(),
                    academic.getFromDateTime(),
                    academic.getToDateTime(),
                    academic.getRepeatType()
            ));
        }
        return academicDtos;
    }

    @Override
    public AcademicDto getAcademic(String id) throws SQLException {
        Academic academic = academicDAO.getEntity(id);
        if (academic != null) {
            return new AcademicDto(
                    academic.getId(),
                    academic.getTitle(),
                    academic.getLocation(),
                    academic.getIsFullDay(),
                    academic.getFromDateTime(),
                    academic.getToDateTime(),
                    academic.getRepeatType()
            );
        }
        return null;
    }

    @Override
    public AcademicDto getRecentDetails(String tableName) throws SQLException {
        Academic academic = academicDAO.getRecentDetails(tableName);
        if (academic != null) {
            return new AcademicDto(
                    academic.getId(),
                    academic.getTitle(),
                    academic.getLocation(),
                    academic.getIsFullDay(),
                    academic.getFromDateTime(),
                    academic.getToDateTime(),
                    academic.getRepeatType()
            );
        }
        return null;
    }

    @Override
    public boolean syncEntryByAi(String query) throws SQLException {
        return academicDAO.syncEntryByAi(query);
    }

    @Override
    public ArrayList<SubjectDto> getAllSubjects() throws SQLException {
        return subjectBO.getAllSubjects();
    }

    @Override
    public String loadNextID(String tableName, String columnName) {
        try {
            return IdLoader.getNextID(tableName, columnName);
        } catch (SQLException e) {
            AlertUtil.setErrorAlert("Error when loading an Academic ID");
            e.printStackTrace();
        }
        return "AC001";
    }

    @Override
    public String getResponse(String instructions){
        return academicDAO.getResponse(instructions);
    }

    @Override
    public String buildSqlInsertPrompt(String userInput) {
        return academicDAO.buildSqlInsertPrompt(userInput);
    }

    @Override
    public String askAboutStudies(String userInput, StringBuilder previousMsg) {
        return academicDAO.askAboutStudies(userInput, previousMsg);
    }

    @Override
    public String reminderGenerator() {
        return academicDAO.reminderGenerator();
    }
}
