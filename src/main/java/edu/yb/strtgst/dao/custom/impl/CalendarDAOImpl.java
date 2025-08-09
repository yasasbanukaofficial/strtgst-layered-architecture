package edu.yb.strtgst.dao.custom.impl;

import com.calendarfx.model.Entry;
import edu.yb.strtgst.dao.custom.CalendarDAO;
import edu.yb.strtgst.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CalendarDAOImpl implements CalendarDAO {
    private final String [] calendarTableNames = {"Exam", "Lecture", "Event", "study_session"};
    private final String [] calendarTableIdColumns = {"exam_id", "lec_id", "event_id", "ss_id"};

    @Override
    public boolean syncEntryWithDatabase(Entry<?> entry) throws SQLException {
        boolean foundInOldTable = false;
        if (entry == null || entry.getCalendar() == null) {
            return false;
        }

        String calendarName = entry.getCalendar().getName();
        String entryId = entry.getId();

        for (int i = 0; i < calendarTableNames.length; i++) {
            String tableName = calendarTableNames[i];
            String idColumn = calendarTableIdColumns[i];

            ResultSet rst = CrudUtil.execute(
                    "SELECT 1 FROM " + tableName + " WHERE " + idColumn + " = ?", entryId
            );

            if (rst.next()) {
                if (calendarName.equalsIgnoreCase(tableName)) {
                    CrudUtil.execute(
                            "UPDATE " + tableName + " SET title = ?, location = ?, full_day = ?, from_date = ?, to_date = ?, repeat_type = ? WHERE " + idColumn + " = ?",
                            entry.getTitle(),
                            entry.getLocation(),
                            entry.isFullDay(),
                            entry.getStartAsLocalDateTime(),
                            entry.getEndAsLocalDateTime(),
                            entry.getRecurrenceRule(),
                            entryId
                    );
                    foundInOldTable = true;
                } else {
                    CrudUtil.execute("DELETE FROM " + tableName + " WHERE " + idColumn + " = ?", entryId);
                }
            }
        }

        if (!foundInOldTable) {
            for (int i = 0; i < calendarTableNames.length; i++) {
                // Check if we need to insert data based on the calendar name
                if (calendarName.equalsIgnoreCase(calendarTableNames[i]) ||
                        (calendarName.equalsIgnoreCase("Study Session") && calendarTableNames[i].equalsIgnoreCase("study_session"))) {

                    CrudUtil.execute(
                            "INSERT INTO " + calendarTableNames[i] + " VALUES (?, ?, ?, ?, ?, ?, ?)",
                            entryId,
                            entry.getTitle(),
                            entry.getLocation(),
                            entry.isFullDay(),
                            entry.getStartAsLocalDateTime(),
                            entry.getEndAsLocalDateTime(),
                            entry.getRecurrenceRule()
                    );
                    return true;
                }
            }
        }

        return true;
    }

    @Override
    public ArrayList<Entry<?>> getAllExamEntries() throws SQLException {
        return getEntriesFromTable("Exam");
    }

    @Override
    public ArrayList<Entry<?>> getAllLectureEntries() throws SQLException {
        return getEntriesFromTable("Lecture");
    }

    @Override
    public ArrayList<Entry<?>> getAllEventEntries() throws SQLException {
        return getEntriesFromTable("Event");
    }

    @Override
    public ArrayList<Entry<?>> getAllStudySessionEntries() throws SQLException {
        return getEntriesFromTable("study_session");
    }

    @Override
    public ArrayList<Entry<?>> getEntriesFromTable(String tableName) throws SQLException {
        ArrayList<Entry<?>> entries = new ArrayList<>();
        try (ResultSet rst = CrudUtil.execute("SELECT * FROM " + tableName)) {
            while (rst.next()) {
                Entry<?> entry = new Entry<>();
                entry.setId(rst.getString(1));
                entry.setTitle(rst.getString(2));
                entry.setLocation(rst.getString(3));
                entry.setFullDay(rst.getBoolean(4));
                entry.setInterval(
                        rst.getTimestamp(5).toLocalDateTime(),
                        rst.getTimestamp(6).toLocalDateTime()
                );
                if (rst.getString(7) == null || rst.getString(7).isEmpty()) {
                    entry.setRecurrenceRule(null);
                } else {
                    entry.setRecurrenceRule(rst.getString(7));
                }
                entries.add(entry);
            }
        }
        return entries;
    }

    @Override
    public boolean deleteEntry(String tableName, String id) throws SQLException {
        String idColumn;
        String actualTableName = tableName;

        if (tableName.equalsIgnoreCase("Exam")) {
            idColumn = "exam_id";
        } else if (tableName.equalsIgnoreCase("Lecture")) {
            idColumn = "lec_id";
        } else if (tableName.equalsIgnoreCase("Event")) {
            idColumn = "event_id";
        } else if (tableName.equalsIgnoreCase("Study Session")) {
            idColumn = "ss_id";
            actualTableName = "study_session";
        } else {
            idColumn = "id";
        }
        return CrudUtil.execute("DELETE FROM " + actualTableName +" WHERE " + idColumn + " = ?", id);
    }

    @Override
    public boolean syncEntryByAi(String query) throws SQLException {
        return CrudUtil.execute(query);
    }

    @Override
    public String getAllFutureEntries(String tableName) throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT COUNT(*) FROM " + tableName + " WHERE to_date > CURRENT_DATE");
        while (rst.next()){
            return rst.getString(1);
        }
        return "0";
    }

    @Override
    public boolean addEntity(Object entity) throws SQLException {
        return false;
    }

    @Override
    public Object getEntity(String name) throws SQLException {
        return null;
    }

    @Override
    public boolean updateEntity(Object entity) throws SQLException {
        return false;
    }

    @Override
    public String getEntityIdByUsername(String name) throws SQLException {
        return "";
    }

    @Override
    public boolean deleteEntity(String id) throws SQLException {
        return false;
    }

    @Override
    public String fetchExistingID(String name) throws SQLException {
        return "";
    }

    @Override
    public ArrayList getAll() throws SQLException {
        return null;
    }

    @Override
    public String loadNextID(String tableName, String columnName) {
        return "";
    }
}
