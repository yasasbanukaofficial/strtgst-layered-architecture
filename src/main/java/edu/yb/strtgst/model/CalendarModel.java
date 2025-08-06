package edu.yb.strtgst.model;

import com.calendarfx.model.Entry;
import edu.yb.strtgst.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CalendarModel {
    private final String [] calendarTableNames = {"Exam", "Lecture", "Event", "study_session"};
    private final String [] calendarTableIdColumns = {"exam_id", "lec_id", "event_id", "ss_id"};

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

    public ArrayList<Entry<?>> getAllExamEntries() throws SQLException {
        return getEntriesFromTable("Exam");
    }

    public ArrayList<Entry<?>> getAllLectureEntries() throws SQLException {
        return getEntriesFromTable("Lecture");
    }

    public ArrayList<Entry<?>> getAllEventEntries() throws SQLException {
        return getEntriesFromTable("Event");
    }

    public ArrayList<Entry<?>> getAllStudySessionEntries() throws SQLException {
        return getEntriesFromTable("study_session");
    }

    private ArrayList<Entry<?>> getEntriesFromTable(String tableName) throws SQLException {
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

    public boolean syncEntryByAi(String query) throws SQLException {
        return CrudUtil.execute(query);
    }

    public String getAllFutureEntries(String tableName) throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT COUNT(*) FROM " + tableName + " WHERE to_date > CURRENT_DATE");
        while (rst.next()){
            return rst.getString(1);
        }
        return "0";
    }
}