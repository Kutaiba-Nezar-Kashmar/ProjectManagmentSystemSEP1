package sample.View;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import sample.Model.*;
import sample.util.ProjectFile;

import java.io.IOException;
import java.util.ArrayList;

public class ScrumMasterController
{

  @FXML private ComboBox<String> requirementStatus;
  @FXML private ComboBox<String> taskPriority;
  @FXML private ComboBox<TeamMember> responsibleTeamMember;
  @FXML private Button cancel;
  @FXML private Button approveRequirement;
  @FXML private Button logOut;
  @FXML private TableView<Requirement> requirementTable;
  @FXML private TableView<Task> taskTable;
  @FXML private TextField inputTaskName;
  @FXML private TextField inputTaskID;
  @FXML private TextField inputDeadlineDay;
  @FXML private TextField inputDeadlineMonth;
  @FXML private TextField inputDeadlineYear;
  @FXML private TextField estimatedCompletionTime;
  @FXML private ComboBox<Project> comboBoxProjects;
  @FXML private ComboBox<String> comboBoxRequirementStatus;
  @FXML private ComboBox<String> comboBoxTaskStatus;
  @FXML private ComboBox<Requirement> comboBoxRequirement;

  private ManagementSystemModel model;
  private ViewHandler viewHandler;
  private Region root;
  private Requirement chosenRequirement;
  private Project chosenProject;


  public void setModel( ManagementSystemModel model)
  {
    this.model = model;
  }

  public void setViewHandler(ViewHandler viewHandler)
  {
    this.viewHandler = viewHandler;
  }

  public void init(ViewHandler viewHandler, ManagementSystemModel model, Region root)
      throws IOException, ClassNotFoundException
  {
    this.viewHandler = viewHandler;
    this.model = model;
    this.root = root;
    initializeComboBoxes(); //Populates the comboBoxes on load;
  }

  public Region getRoot()
  {
    return root;
  }


  public void setCancel()
  {
    Stage stage = (Stage) cancel.getScene().getWindow();
    stage.close();
  }



  public void addTask() throws IOException, ClassNotFoundException
  {
    ProjectList loadedList = model.readProjectList("ProjectList.bin");

    //Makes sure the list we are editing are the most current saved list.

    model.setProjectList(loadedList);
    chosenRequirement = comboBoxRequirement.getValue();

    Task taskToAdd = new Task(inputTaskName.getText(), model.getProjectList().getIdGenerator().generateTaskID(), "Ikke p??begyndt"
    , responsibleTeamMember.getValue(), taskPriority.getValue(), new MyDate(Integer.parseInt(inputDeadlineDay.getText()),
        Integer.parseInt(inputDeadlineMonth.getText()),Integer.parseInt(inputDeadlineYear.getText())),
        Double.parseDouble(estimatedCompletionTime.getText()));
    model.getProjectList().getProject(chosenProject.getProjectName()).
        getRequirementList().getRequirement(chosenRequirement.getRequirementID()).getTaskList().addTask(taskToAdd);
    model.saveProjectList();
    ProjectFile.save(model.getProjectList());

    //Clears textfields after adding task.
    inputTaskName.clear();
    inputDeadlineDay.clear();
    inputDeadlineMonth.clear();
    inputDeadlineYear.clear();
    estimatedCompletionTime.clear();

    //Refreshes the tableview such the new changes are displayed
    populateTableViewTask();
  }


  public void initializeComboBoxes() throws IOException, ClassNotFoundException
  {
    ArrayList<Project> systemProjects = model.readProjectList("ProjectList.bin").getAllProjects();
    comboBoxProjects.getItems().addAll(systemProjects);

    comboBoxRequirementStatus.getItems().addAll(
        "Ikke p??begyndt", "P??begyndt", "Afsluttet"
    );




    taskPriority.getItems().addAll(
        "Lav", "Normal", "H??j", "Kritisk"
    );

    comboBoxTaskStatus.getItems().addAll(
        "Ikke p??begyndt", "P??begyndt", "Afsluttet"
    );

  }

  public void initializeResponsibleMemberComboBox()
  {
    ArrayList<TeamMember> systemEmployees = model.getProjectList().getProject(comboBoxProjects.getValue().getProjectName()).getEmployeeList().getAllEmployees();
    responsibleTeamMember.getItems().addAll(systemEmployees);
  }

  public void populateTableViewRequirement() throws IOException, ClassNotFoundException
  {
    /*
     * Method populates the tableView with all requirements
     * */
    setChosenProject();

    //Resets table to prevent adding new columns on refresh

    requirementTable.getItems().clear();
    requirementTable.getColumns().clear();

    //Name column

    TableColumn<Requirement,String> nameColumn = new TableColumn<>("Navn");
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("requirementName"));

    //ID column

    TableColumn<Requirement,Integer> IDColumn = new TableColumn<>("ID");
    IDColumn.setCellValueFactory(new PropertyValueFactory<>("requirementID"));

    //responsible TeamMember column

    TableColumn<Requirement,TeamMember> responsibleMemberColumn = new TableColumn<>("Ansvarlig Teammedlem");
    responsibleMemberColumn.setCellValueFactory(new PropertyValueFactory<>("responsibleTeamMember"));

    //Status column

    TableColumn<Requirement,String> statusColumn = new TableColumn<>("Status");
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("requirementStatus"));

    //Priority column

    TableColumn<Requirement,String> priorityColumn = new TableColumn<>("Prioritet");
    priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));

    //Time spent column

    TableColumn<Requirement,Integer> timeSpentColumn = new TableColumn<>("Tid Brugt");
    timeSpentColumn.setCellValueFactory(new PropertyValueFactory<>("timeSpendInHours"));

    //Deadline column

    TableColumn<Requirement,MyDate> deadlineColumn = new TableColumn<>("Deadline");
    deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("requirementDeadline"));

    //Creation Date column

    TableColumn<Requirement,MyDate> creationDateColumn = new TableColumn<>("Oprettelsesdato");
    creationDateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate"));

    //Estimated completion time column

    TableColumn<Requirement,Double> estimatedCompletionTimeColumn = new TableColumn<>("Estimeret afslutningstid");
    estimatedCompletionTimeColumn.setCellValueFactory(new PropertyValueFactory<>("estimatedCompletionTimeInHours"));

    //User Story column

    TableColumn<Requirement, String> userStoryColumn = new TableColumn<>("User Story");
    userStoryColumn.setCellValueFactory(new PropertyValueFactory<>("userStory"));



    requirementTable.setItems(getAllRequirements());
    requirementTable.getColumns().addAll(nameColumn, IDColumn,responsibleMemberColumn,statusColumn,
        priorityColumn, timeSpentColumn, deadlineColumn, creationDateColumn, estimatedCompletionTimeColumn, userStoryColumn);
    initializeResponsibleMemberComboBox();
    initializeComboBoxRequirement();

  }

  public void populateTableViewTask() throws IOException, ClassNotFoundException
  {
    setChosenProject();
    chosenRequirement = comboBoxRequirement.getValue();

    //Resets TableView to prevent adding new columns on refresh

    taskTable.getColumns().clear();
    taskTable.getItems().clear();

    //Name column

    TableColumn<Task,String> nameColumn = new TableColumn<>("Navn");
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

    //ID column

    TableColumn<Task,Integer> IDColumn = new TableColumn<>("ID");
    IDColumn.setCellValueFactory(new PropertyValueFactory<>("taskID"));

    //responsible TeamMember column

    TableColumn<Task,TeamMember> responsibleMemberColumn = new TableColumn<>("Ansvarlig Teammedlem");
    responsibleMemberColumn.setCellValueFactory(new PropertyValueFactory<>("responsibleTeamMember"));

    //Status column

    TableColumn<Task,String> statusColumn = new TableColumn<>("Status");
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

    //Priority column

    TableColumn<Task,String> priorityColumn = new TableColumn<>("Prioritet");
    priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));

    //Time spent column

    TableColumn<Task,Integer> timeSpentColumn = new TableColumn<>("Tid Brugt");
    timeSpentColumn.setCellValueFactory(new PropertyValueFactory<>("timeSpendInHours"));

    //Deadline column

    TableColumn<Task,MyDate> deadlineColumn = new TableColumn<>("Deadline");
    deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));

    //Creation Date column

    TableColumn<Task,MyDate> creationDateColumn = new TableColumn<>("Oprettelsesdato");
    creationDateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate"));

    //Estimated completion time column

    TableColumn<Task,Double> estimatedCompletionTimeColumn = new TableColumn<>("Estimeret afslutningstid");
    estimatedCompletionTimeColumn.setCellValueFactory(new PropertyValueFactory<>("estimatedCompletionTimeInHours"));

    taskTable.setItems(getAllTasks());
    taskTable.getColumns().addAll(nameColumn, IDColumn,responsibleMemberColumn,statusColumn,
        priorityColumn, timeSpentColumn, deadlineColumn, creationDateColumn, estimatedCompletionTimeColumn);
  }


  public ObservableList<Requirement> getAllRequirements()
      throws IOException, ClassNotFoundException
  {
    /*
     * Method used for loading Requirements to the tableView.
     * */

    ObservableList<Requirement> requirements = FXCollections.observableArrayList();

    //Reading projectList file and getting requirements of chosen project

    ArrayList<Requirement> projectRequirements = model.readProjectList("ProjectList.bin")
        .getProject(chosenProject.getProjectName()).getRequirementList().getAllRequirements();

    //Adding all requirements to observable list.

    requirements.addAll(projectRequirements);
    return requirements;
  }

  public ObservableList<Task> getAllTasks()
      throws IOException, ClassNotFoundException
  {
    /*
     * Method used for loading Requirements to the tableView.
     * */

    ObservableList<Task> tasks = FXCollections.observableArrayList();

    //Reading projectList file and getting requirements of chosen project

    ArrayList<Task> projectTasks = model.readProjectList("ProjectList.bin")
        .getProject(chosenProject.getProjectName()).getRequirementList().getRequirement(comboBoxRequirement.getValue().getRequirementID()).getTaskList().getAllTasks();

    //Adding all requirements to observable list.

    tasks.addAll(projectTasks);
    return tasks;
  }




  public void setRequirementStatus() throws IOException, ClassNotFoundException
  {
    chosenProject.getRequirementList().getRequirement(requirementTable.getSelectionModel()
        .getSelectedItem().getRequirementID()).setRequirementStatus(comboBoxRequirementStatus.getValue());
    model.saveProjectList();
    ProjectFile.save(model.getProjectList());
    //Refreshes the tableview such the new changes are displayed
    populateTableViewRequirement();
  }


  public void setChosenProject() throws IOException, ClassNotFoundException
  {
    ProjectList loadedList = model.readProjectList("ProjectList.bin");
    model.setProjectList(loadedList); //Makes sure the projectList for model is the saved version.
    chosenProject = model.getProjectList().getProject(comboBoxProjects.getValue().getProjectName());
  }

  public void setTaskStatus() throws IOException, ClassNotFoundException
  {
    Task chosenTask = taskTable.getSelectionModel().getSelectedItem();

    model.getProjectList().getProject(chosenProject.getProjectName()).getRequirementList().
        getRequirement(chosenRequirement.getRequirementID()).getTaskList().getTask(chosenTask.getTaskID())
        .setStatus(comboBoxTaskStatus.getValue());
    model.saveProjectList();
    ProjectFile.save(model.getProjectList());
    //Refreshes the tableview such the new changes are displayed
    populateTableViewTask();
  }

  public void deleteTask() throws IOException, ClassNotFoundException
  {
    Task chosenTask = taskTable.getSelectionModel().getSelectedItem();
    model.getProjectList().getProject(chosenProject.getProjectName()).getRequirementList().
        getRequirement(chosenRequirement.getRequirementID()).getTaskList().removeTask(chosenTask.getTaskID());
    model.saveProjectList();
    ProjectFile.save(model.getProjectList());
    //Refreshes the tableview such the new changes are displayed
    populateTableViewTask();
  }

  public void initializeComboBoxRequirement()
      throws IOException, ClassNotFoundException
  {
    ProjectList loadedList = model.readProjectList("ProjectList.bin");
    model.setProjectList(loadedList);
    ArrayList<Requirement> projectRequirements = model.getProjectList().getProject(chosenProject.getProjectName())
        .getRequirementList().getAllRequirements();
    comboBoxRequirement.getItems().addAll(projectRequirements);
  }

  public void logOut()
  {
    viewHandler.openView("main");
  }

}
