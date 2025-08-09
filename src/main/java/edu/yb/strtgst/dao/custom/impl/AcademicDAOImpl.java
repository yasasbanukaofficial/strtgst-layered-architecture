package edu.yb.strtgst.dao.custom.impl;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import edu.yb.strtgst.dao.custom.AcademicDAO;
import edu.yb.strtgst.entity.Academic;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class AcademicDAOImpl implements AcademicDAO {

    private static final String GOOGLE_API_KEY = "AIzaSyAboDpPm77ZEmlnGyyRK-Ta518yv6e9p9Q";

    @Override
    public boolean addEntity(Academic entity) throws SQLException {
        return CrudUtil.execute(
                "INSERT INTO Academic VALUES (?, ?, ?, ?, ?, ?, ?)",
                entity.getId(),
                entity.getTitle(),
                entity.getLocation(),
                entity.getIsFullDay(),
                entity.getFromDateTime(),
                entity.getToDateTime(),
                entity.getRepeatType()
        );
    }

    @Override
    public Academic getEntity(String id) throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT * FROM Academic WHERE id = ?", id);
        if (rst.next()) {
            return new Academic(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getBoolean(4),
                    rst.getTimestamp(5),
                    rst.getTimestamp(6),
                    rst.getString(7)
            );
        }
        return null;
    }

    @Override
    public boolean updateEntity(Academic entity) throws SQLException {
        return CrudUtil.execute(
                "UPDATE Academic SET title = ?, location = ?, is_full_day = ?, from_date = ?, to_date = ?, repeat_type = ? WHERE id = ?",
                entity.getTitle(),
                entity.getLocation(),
                entity.getIsFullDay(),
                entity.getFromDateTime(),
                entity.getToDateTime(),
                entity.getRepeatType(),
                entity.getId()
        );
    }

    @Override
    public String getEntityIdByUsername(String name) throws SQLException {
        return "";
    }

    @Override
    public boolean deleteEntity(String id) throws SQLException {
        return CrudUtil.execute("DELETE FROM Academic WHERE id = ?", id);
    }

    @Override
    public String fetchExistingID(String name) throws SQLException {
        return "";
    }

    @Override
    public ArrayList<Academic> getAll() throws SQLException {
        ResultSet rst = CrudUtil.execute("SELECT * FROM Academic");

        ArrayList<Academic> academics = new ArrayList<>();
        while (rst.next()) {
            Academic academic = new Academic(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getBoolean(4),
                    rst.getTimestamp(5),
                    rst.getTimestamp(6),
                    rst.getString(7)
            );
            academics.add(academic);
        }

        return academics;
    }

    @Override
    public String loadNextID(String tableName, String columnName) {
        return "";
    }

    @Override
    public boolean syncEntryByAi(String query) throws SQLException {
        String[] statements = query.split(";");
        for (String stmt : statements) {
            if (!stmt.trim().isEmpty()) {
                boolean success = CrudUtil.execute(stmt.trim());
                if (!success) return false;
            }
        }
        return true;
    }

    @Override
    public Academic getRecentDetails(String tableName) throws SQLException {
        ResultSet rst = CrudUtil.execute(
                "SELECT * FROM " + tableName + " WHERE from_date >= CURRENT_DATE ORDER BY from_date ASC LIMIT 1"
        );
        if (rst.next()) {
            return new Academic(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getBoolean(4),
                    rst.getTimestamp(5),
                    rst.getTimestamp(6),
                    rst.getString(7)
            );
        }
        return null;
    }

    @Override
    public String getResponse(String instructions) {
        System.setProperty("GOOGLE_API_KEY", GOOGLE_API_KEY);

        try {
            Client client = new Client.Builder().apiKey(GOOGLE_API_KEY).build();
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.0-flash",
                    instructions,
                    null
            );
            return response.text();
        } catch (Exception e) {
            AlertUtil.setErrorAlert("Error when generating the text through AI " + e.getMessage());
            return "Error:  " + e.getMessage();
        }
    }

    @Override
    public String buildSqlInsertPrompt(String userInput){
        String instructions = """
                            You are an AI that only returns plain SQL INSERT statements — no code blocks, no labels, no explanations, no markdown formatting.
                            
                            Today's Date and time is """ + LocalDateTime.now() + """

                            There are 3 tables in the MySQL database:
                            
                            -- Table Lecture  
                            CREATE TABLE lecture (  
                                lec_id VARCHAR(50),  
                                title VARCHAR(250),  
                                location VARCHAR(100) DEFAULT 'SCHOOL',  
                                full_day BOOLEAN DEFAULT FALSE,  
                                from_date DATETIME,  
                                to_date DATETIME,  
                                repeat_type VARCHAR(50) DEFAULT 'None',  
                                PRIMARY KEY (lec_id)  
                            );
                            
                            -- Table Exam  
                            CREATE TABLE exam (  
                                exam_id VARCHAR(50),  
                                title VARCHAR(250),  
                                location VARCHAR(100) DEFAULT 'SCHOOL',  
                                full_day BOOLEAN DEFAULT FALSE,  
                                from_date DATETIME,  
                                to_date DATETIME,  
                                repeat_type VARCHAR(50) DEFAULT 'None',  
                                PRIMARY KEY (exam_id)  
                            );
                            
                            -- Table Event  
                            CREATE TABLE event (  
                                event_id VARCHAR(50),  
                                title VARCHAR(250),  
                                location VARCHAR(100) DEFAULT 'SCHOOL',  
                                full_day BOOLEAN DEFAULT FALSE,  
                                from_date DATETIME,  
                                to_date DATETIME,  
                                repeat_type VARCHAR(50) DEFAULT 'None',  
                                PRIMARY KEY (event_id)  
                            );
                            
                            -- Table StudySession
                            CREATE TABLE study_session (
                                ss_id VARCHAR(50),
                                title VARCHAR(250),
                                location VARCHAR(100) DEFAULT 'SCHOOL',
                                full_day BOOLEAN DEFAULT FALSE,
                                from_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                                to_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                                repeat_type VARCHAR(50) DEFAULT NULL,
                                PRIMARY KEY (ss_id)
                            );
                            
                            Instructions:
                            - Remember Ignore unclear or incomplete sentences (e.g., “i have a exam” — no action required).
                            - Remember Only return results for clear, actionable sentences.
                            - Remember Skip anything confusing, non-standard, or gibberish.
                            - Remember the queries you add will be retrieved by the project's model to apply it in CalendarFx's views
                            - Remember when generating id's always generate random id's everytime user input something
                            - Only generate SQL INSERT INTO statements.
                            - Do not wrap the output in triple backticks (```), do not prefix with “sql” or any labels.
                            - Do not return any comments or explanations.
                            - Do not return anything if the user input is casual or unrelated (e.g., “Hello”, “How are you”) or anything unrelated about events, exams, study session(s) or lectures or if user ask about something that needs help with.
                            - Convert expressions like “Monday at 9pm” to full `YYYY-MM-DD HH:MM:SS` datetime.
                            - Do NOT use SQL functions like CONCAT, just give the final datetime.
                            - Set `full_day = TRUE` if the user says "full day" or similar.
                            - Default location is `'SCHOOL'` for exams and lectures, `'Cafe'` for events if not mentioned.
                            - Always generate a random ID for each row (e.g., `'EXM123456'`, `'EVT987654'`, `'LEC456789'`, `'SS782451'`).
                            - Do not generate any statement if any of the details are missing because the database going to add the default values. Don't even add null to it.
                            - Do not pass null. If nay required value is missing just dont add that single data to the db since the db will add their default values.
                            - If user said something it will repeat or occur this way add it to the repeat_type column this way only for 
                              daily -> RRULE:FREQ=DAILY
                              weekly -> RRULE:FREQ=WEEKLY
                              monthly -> RRULE:FREQ=MONTHLY
                              yearly -> RRULE:FREQ=YEARLY
                              if there's nothing user mentioned about it put null only nothing else. 
                            - If the user didnt mention about what the exam, lecture, study session or the event is just put a the table name as the title.
                            - Always generate a new, unique random ID for each new row, even if details appear similar.
                            - Always generate a wonderful random study name for the title if the user didn't give anything for the title.
                            - Remember If the user didnt mention about the ending time always put the ending time as 1 hour past the starting time likewise if the user didnt mention about the starting time always put the starting time as the current time.
                            
                            Now generate a valid SQL INSERT statement based only on the following user input. Do not wrap it, label it, or explain it:
                            
                            """ + userInput;

        return getResponse(instructions);
    }

    @Override
    public String askAboutStudies(String userInput, StringBuilder previousMsg) {
        String instructions = "You are Strtgst Ai Helper Bot, a highly knowledgeable and friendly AI powered by Gemini. " +
                "Do not mention Google or Gemini unless asked how you work. If asked your name, say you are Strtgst Ai Helper Bot. " +

                "You help students and learners understand academic, scientific, general knowledge, and educational topics — from biology to pop culture references. " +
                "You always answer briefly and clearly in under 150 characters, avoiding repeated questions or filler phrases. " +

                "If the input is nonsense, jokes (e.g., 'blah blah'), random characters, or not related to learning or curiosity, reply: 'Didn't get the message, try something educative.' " +

                "If the user references a previous chat and the previous message is null or empty, ignore it. " +
                "Here is the previous message (StringBuilder in Java): " + previousMsg.toString() + " " +

                "Now answer the user's question as helpfully and briefly as possible, without repeating the question. " +
                "User's Question: " + userInput;
        return getResponse(instructions);
    }

    @Override
    public String reminderGenerator() {
        return """
                You are a reminder generator AI for an educational app aimed at students and teenagers.
                Your task is to generate a random educational reminder that is inspiring, relevant, and informative.
                The reminder must be between 50 and 70 characters long.
                Only return the reminder — no explanations, no formatting, no extra text.""";
    }
}
