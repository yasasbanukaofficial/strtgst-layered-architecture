package edu.yb.strtgst.controller;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.AgendaView;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.DetailedDayView;
import com.calendarfx.view.page.MonthPage;
import com.calendarfx.view.page.WeekPage;
import edu.yb.strtgst.bo.BOFactory;
import edu.yb.strtgst.bo.custom.AcademicBO;
import edu.yb.strtgst.util.CalendarUtil;
import edu.yb.strtgst.util.AlertUtil;
import edu.yb.strtgst.util.DateUtil;
import edu.yb.strtgst.util.PromptBuilder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CalendarPageController implements Initializable {
    public VBox ancTimeline;
    public VBox ancAgendaView;
    public VBox ancChatBot;
    public TextFlow txtChatFlow;
    public TextField txtEnterMsg;
    public StackPane btnSendMsg;
    public Label labelDate;
    private Calendar examCalendar = new Calendar("Exam");
    private Calendar lectureCalendar = new Calendar("Lecture");
    private Calendar eventsCalendar = new Calendar("Event");
    private Calendar studySessionCalendar = new Calendar("Study Session");
    private CalendarSource calendarSource = new CalendarSource("My Calendar");
    private final WeekPage weekView = new WeekPage();
    private final DetailedDayView dayView = new DetailedDayView();
    private final MonthPage monthView = new MonthPage();
    private final AgendaView agendaView = new AgendaView();

    private final CalendarUtil calendarUtil = new CalendarUtil();
    private static ArrayList<CalendarEvent> events = new ArrayList<>();
    private static ArrayList<Entry> entries = new ArrayList<>();
    private boolean isLoading = false;

    AcademicBO academicBO = (AcademicBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.ACADEMIC);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupCalendarViews();
        eventsInitializer();
        loadAllEntries();
        updateDateLabel();
    }

    public void navigateTo(VBox anchor, Node node){
        anchor.getChildren().clear();
        anchor.getChildren().add(node);
    }

    public void setupView(VBox anchor, DateControl view){
        anchor.getStylesheets().add(getClass().getResource("/view/styles/popOver.css").toExternalForm());
        double width = anchor.getPrefWidth() - 20.0;
        double height = anchor.getPrefHeight() - 20.0;
        view.setPrefSize(width, height);
        view.refreshData();
        view.requestLayout();
        UpdateThread.startThread(view);
        navigateTo(anchor, view);
    }

    public void showWeekView(ActionEvent actionEvent) {
        setupView(ancTimeline, weekView);
    }

    public void showDayView(ActionEvent actionEvent) {
        setupView(ancTimeline, dayView);
    }

    public void showMonthView(ActionEvent actionEvent) {
        setupView(ancTimeline, monthView);
    }

    public void showAgendaView(ActionEvent actionEvent) {setupView(ancAgendaView, agendaView);}

    private void eventsInitializer(){
        EventHandler<CalendarEvent> event = e -> handleEvent(e);
        examCalendar.addEventHandler(event);
        lectureCalendar.addEventHandler(event);
        eventsCalendar.addEventHandler(event);
        studySessionCalendar.addEventHandler(event);
    }

    private void handleEvent (CalendarEvent e) {
        if (isLoading) return;
        Entry<?> entry = e.getEntry();
        entries.add(entry);
        events.add(e);
    }

    public void addEntriesToDB() {
        for (Entry<?> entry : entries) {
            CalendarEvent matchingEvent = null;

            for (CalendarEvent event : events) {
                if (event.getEntry() == entry) {
                    matchingEvent = event;
                    break;
                }
            }

            try {
                if (entry.getCalendar() == null) {
                    if (matchingEvent != null && matchingEvent.getOldCalendar() != null) {
                        if (!calendarUtil.deleteEntry(matchingEvent.getOldCalendar().getName(), entry.getId())) {
                            AlertUtil.setErrorAlert("Error when deleting entry from the database");
                        }
                    } else {
                        AlertUtil.setErrorAlert("Could not determine old calendar for deleted entry: " + entry.getId());
                    }
                } else {
                    if (!calendarUtil.syncEntryWithDatabase(entry)) {
                        AlertUtil.setErrorAlert("Error when modifying an event to the calendar");
                    }
                }
            } catch (Exception ex) {
                AlertUtil.setErrorAlert("Error when syncing entry with the database");
                ex.printStackTrace();
            }
        }

        entries.clear();
        events.clear();
    }

    private void loadAllEntries() {
        isLoading = true;
        try {
            examCalendar.clear();
            lectureCalendar.clear();
            eventsCalendar.clear();
            studySessionCalendar.clear();

            loadEntriesForCalendar(examCalendar, calendarUtil.getAllExamEntries());
            loadEntriesForCalendar(lectureCalendar, calendarUtil.getAllLectureEntries());
            loadEntriesForCalendar(eventsCalendar, calendarUtil.getAllEventEntries());
            loadEntriesForCalendar(studySessionCalendar, calendarUtil.getAllStudySessionEntries());

            refreshViews();
        } catch (SQLException e) {
            AlertUtil.setErrorAlert("Error when loading all entries from the calendar");
            e.printStackTrace();
        } finally {
            isLoading = false;
        }
    }

    private void loadEntriesForCalendar(Calendar calendar, ArrayList<Entry<?>> entries) {
        for (Entry<?> entry : entries) {
            calendar.addEntry(entry);
        }
    }

    private void refreshViews() {
        weekView.refreshData();
        dayView.refreshData();
        monthView.refreshData();
    }

    private void setupCalendarViews() {
        showDayView(new ActionEvent());
        showAgendaView(new ActionEvent());
        setupCalendarStyles();
        calendarSource.getCalendars().addAll(lectureCalendar, eventsCalendar, examCalendar, studySessionCalendar);

        weekView.getCalendarSources().clear();
        weekView.getCalendarSources().add(calendarSource);

        dayView.getCalendarSources().clear();
        dayView.getCalendarSources().add(calendarSource);

        monthView.getCalendarSources().clear();
        monthView.getCalendarSources().add(calendarSource);

        agendaView.getCalendarSources().clear();
        agendaView.getCalendarSources().add(calendarSource);

        monthView.setShowDate(false);
        monthView.setShowNavigation(false);
        weekView.setShowDate(false);
    }

    private void setupCalendarStyles() {
        examCalendar.setStyle(Calendar.Style.STYLE5);
        lectureCalendar.setStyle(Calendar.Style.STYLE2);
        eventsCalendar.setStyle(Calendar.Style.STYLE4);
        studySessionCalendar.setStyle(Calendar.Style.STYLE6);
    }

    public void sendMessage(MouseEvent mouseEvent) {
        txtChatFlow.getChildren().clear();
        try {
            String userInput = txtEnterMsg.getText();
            if (userInput.trim().isEmpty()) {
                AlertUtil.setErrorAlert("Please enter a valid entry message to send");
                return;
            }

            String aiResponse = academicBO.getResponse(PromptBuilder.buildSqlInsertPrompt(userInput));

            boolean isValid = aiResponse != null && aiResponse.trim().toLowerCase().startsWith("insert into");

            String response;
            if (isValid) {
                boolean isSynced = calendarUtil.syncEntryByAi(aiResponse);
                response = isSynced ?
                        "Your event is successfully added. Add some more!" :
                        "Failed to add an event. Try with a stable internet connection.";
            } else {
                response = "Sorry, I couldn’t understand that. Please describe an event like: “I have a lecture on June 6 at 10 AM.”";
            }

            Text userTxt = new Text("User:      " + userInput + "\n");
            Text responseTxt = new Text("Chat:      " + response);
            txtChatFlow.getChildren().addAll(userTxt, responseTxt);

            txtEnterMsg.setText("");

            if (isValid) loadAllEntries();

        } catch (SQLException e) {
            AlertUtil.setErrorAlert("Error when sending the message: " + e.getMessage());
        }
    }

    private void updateDateLabel() {
        labelDate.setText(DateUtil.setDate());
    }
}

class UpdateThread{
    private static Thread updateTimeThread;
    private static volatile boolean running = false;
    private static DateControl currentView;
    private static CalendarPageController calendarPageController = new CalendarPageController();
    public static Thread startThread(DateControl view) {
        currentView = view;
        if (updateTimeThread == null){
            running = true;
            updateTimeThread = new Thread("Calendar: Update Time & Database"){
                @Override
                public void run() {
                    while (running){
                        Platform.runLater(() -> {
                            calendarPageController.addEntriesToDB();
                            if (currentView != null && currentView.getScene() != null){
                                currentView.setDate(LocalDate.now());
                                currentView.setTime(LocalTime.now());
                            }
                        });

                        try{
                            sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            updateTimeThread.setPriority(Thread.MIN_PRIORITY);
            updateTimeThread.setDaemon(true);
            updateTimeThread.start();
        }
        return updateTimeThread;
    }

    public static void stopThread() {
        calendarPageController.addEntriesToDB();
        running = false;
        if (updateTimeThread != null){
            updateTimeThread.interrupt();
            updateTimeThread = null;
        }
    }
}