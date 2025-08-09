package edu.yb.strtgst.bo.custom.impl;

import com.calendarfx.model.Entry;
import edu.yb.strtgst.bo.custom.CalendarBO;
import edu.yb.strtgst.dao.DAOFactory;
import edu.yb.strtgst.dao.custom.CalendarDAO;

import java.sql.SQLException;
import java.util.ArrayList;

public class CalendarBOImpl implements CalendarBO {
    CalendarDAO calendarDAO = (CalendarDAO) DAOFactory.getInstance().getDAO(DAOFactory.DAOTYPES.CALENDAR);
    @Override
    public boolean syncEntryWithDatabase(Entry<?> entry) throws SQLException {
        return calendarDAO.syncEntryWithDatabase(entry);
    }

    @Override
    public ArrayList<Entry<?>> getAllExamEntries() throws SQLException {
        return calendarDAO.getAllExamEntries();
    }

    @Override
    public ArrayList<Entry<?>> getAllLectureEntries() throws SQLException {
        return calendarDAO.getAllLectureEntries();
    }

    @Override
    public ArrayList<Entry<?>> getAllEventEntries() throws SQLException {
        return calendarDAO.getAllEventEntries();
    }

    @Override
    public ArrayList<Entry<?>> getAllStudySessionEntries() throws SQLException {
        return calendarDAO.getAllStudySessionEntries();
    }

    @Override
    public ArrayList<Entry<?>> getEntriesFromTable(String tableName) throws SQLException {
        return calendarDAO.getEntriesFromTable(tableName);
    }

    @Override
    public boolean deleteEntry(String tableName, String id) throws SQLException {
        return calendarDAO.deleteEntry(tableName, id);
    }

    @Override
    public boolean syncEntryByAi(String query) throws SQLException {
        return calendarDAO.syncEntryByAi(query);
    }

    @Override
    public String getAllFutureEntries(String tableName) throws SQLException {
        return calendarDAO.getAllFutureEntries(tableName);
    }
}
