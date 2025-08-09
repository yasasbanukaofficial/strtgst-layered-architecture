package edu.yb.strtgst.controller.context;

import edu.yb.strtgst.controller.*;
import edu.yb.strtgst.controller.AssignmentPageController;

public class AppContext {
    private static AppContext appContext;
    private static AssignmentPageController assignmentPageController;
    private static AssignmentFormController assignmentFormController;
    private static SubjectFormController subjectFormController;
    private static IntroPageController introPageController;
    private static TaskFormController taskFormController;
    private static TaskPageController taskPageController;
    private static MainPageController mainPageController;
    private static SubjectPageController subjectPageController;
    private String username;

    private AppContext() {}

    public static AppContext getInstance() {
        return appContext == null ? appContext = new AppContext() : appContext;
    }

    public AssignmentPageController getAssignmentPageController() {
        if (assignmentPageController == null){
            assignmentPageController = new AssignmentPageController();
        }
        return assignmentPageController;
    }

    public TaskPageController getTaskPageController() {
        if (taskPageController == null){
            taskPageController = new TaskPageController();
        }
        return taskPageController;
    }

    public AssignmentFormController getAssignmentFormController() {
        if (assignmentFormController == null){
            assignmentFormController = new AssignmentFormController();
        }
        return assignmentFormController;
    }

    public TaskFormController getTaskFormController() {
        if (taskFormController == null){
            taskFormController = new TaskFormController();
        }
        return taskFormController;
    }

    public MainPageController getMainPageController() {
        if (mainPageController == null){
            mainPageController = new MainPageController();
        }
        return mainPageController;
    }

    public String getUsername() {
        return username;
    }

    public SubjectPageController getSubjectPageController() {
        return this.subjectPageController;
    }

    public SubjectFormController getSubjectFormController() {
        return this.subjectFormController;
    }

    public IntroPageController getIntroPageController() {
        return this.introPageController;
    }

    public void setAssignmentPageController(AssignmentPageController assignmentPageController) {
        this.assignmentPageController = assignmentPageController;
    }

    public void setAssignmentFormController(AssignmentFormController assignmentFormController) {
        this.assignmentFormController = assignmentFormController;
    }

    public void setTaskFormController(TaskFormController taskFormController) {
        this.taskFormController = taskFormController;
    }

    public void setTaskPageController(TaskPageController taskPageController) {
        this.taskPageController = taskPageController;
    }

    public void setMainPageController(MainPageController mainPageController) {
        this.mainPageController = mainPageController;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSubjectPageController(SubjectPageController subjectPageController) {
        this.subjectPageController = subjectPageController;
    }

    public void setSubjectFormController(SubjectFormController subjectFormController) {
        this.subjectFormController = subjectFormController;
    }

    public void setIntroPageController(IntroPageController introPageController) {
        this.introPageController = introPageController;
    }
}
