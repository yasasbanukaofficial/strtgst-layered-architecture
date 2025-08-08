package edu.yb.strtgst.bo.custom;

import edu.yb.strtgst.bo.SuperBO;
import edu.yb.strtgst.dto.AcademicDto;
import edu.yb.strtgst.dto.SubjectDto;

import java.sql.SQLException;
import java.util.ArrayList;

public interface AcademicBO extends SuperBO {
    boolean addAcademic(AcademicDto academicDto) throws SQLException;
    boolean updateAcademic(AcademicDto academicDto) throws SQLException;
    boolean deleteAcademic(String id) throws SQLException;
    ArrayList<AcademicDto> getAllAcademics() throws SQLException;
    AcademicDto getAcademic(String id) throws SQLException;
    AcademicDto getRecentDetails(String tableName) throws SQLException;
    boolean syncEntryByAi(String query) throws SQLException;
    ArrayList<SubjectDto> getAllSubjects() throws SQLException;
    String loadNextID(String tableName, String columnName);
    public String getResponse(String instructions);
}
