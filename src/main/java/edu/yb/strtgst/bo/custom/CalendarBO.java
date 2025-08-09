package edu.yb.strtgst.bo.custom;

import com.calendarfx.model.Entry;
import edu.yb.strtgst.bo.SuperBO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface CalendarBO extends SuperBO {
    public boolean syncEntryWithDatabase(Entry<?> entry) throws SQLException;

    public ArrayList<Entry<?>> getAllExamEntries() throws SQLException;

    public ArrayList<Entry<?>> getAllLectureEntries() throws SQLException;

    public ArrayList<Entry<?>> getAllEventEntries() throws SQLException;

    public ArrayList<Entry<?>> getAllStudySessionEntries() throws SQLException;

    public ArrayList<Entry<?>> getEntriesFromTable(String tableName) throws SQLException;

    public boolean deleteEntry(String tableName, String id) throws SQLException;

    public boolean syncEntryByAi(String query) throws SQLException;

    public String getAllFutureEntries(String tableName) throws SQLException;
}
