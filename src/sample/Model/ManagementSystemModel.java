package sample.Model;

import sample.util.IDGenerator;

import java.io.IOException;
import java.util.ArrayList;

public class ManagementSystemModel
{

  private String userRole;

  private String currentProject;

  private ProjectList projectList;

  private EmployeeList employeeList;

  private ProjectFile projectFile;



  public ManagementSystemModel() {
    projectList = new ProjectList();
    employeeList = new EmployeeList();
    currentProject = ""; //Ved start er der ingen projekter valgt endnu.
    projectFile = new ProjectFile("Hello");
  }

  public void setUser(String userRole) {
    this.userRole = userRole;
  }

  public void createProject(String name, int projectID, MyDate deadLine) {
    projectList.addProject(new Project(name, projectList.getIdGenerator().generateProjectID(), deadLine));
  }

  public void createRequirement(String name, int requirementID, TeamMember responsibleTeamMember,
      String status, String priority, MyDate deadline) {
    projectList.getProject(currentProject).getRequirementList().addRequirement(new Requirement(name, projectList.getIdGenerator().generateRequirementID(), responsibleTeamMember, status,priority,deadline));
  }

  public void createTask(int requirementID, String name, int taskID, String status, TeamMember responsibleTeamMember, String priority, MyDate deadline) {
    projectList.getProject(currentProject).getRequirementList().getRequirement(requirementID)
        .getTaskList().addTask(new Task(name, projectList.getIdGenerator().generateTaskID(),status,responsibleTeamMember, priority, deadline));
  }

  public void removeProject(String name) {
    projectList.removeProject(name);
  }

  public void removeRequirement(int requirementID) {
    projectList.getProject(currentProject).getRequirementList().removeRequirement(requirementID);
  }

  public void removeTask(int requirementID, int taskID) {
    projectList.getProject(currentProject).getRequirementList().getRequirement(requirementID).getTaskList()
        .removeTask(taskID);
  }

  public void assignTeamMemberToProject(String projectName, String teamMemberName) {
    TeamMember teamMemberToAdd = null;
    teamMemberToAdd = employeeList.getEmployee(teamMemberName);

    projectList.getProject(projectName).getEmployeeList().addEmployee(teamMemberToAdd);
  }

  public String getUser()
  {
    return userRole;
  }

  public String getCurrentProject()
  {
    return currentProject;
  }

  public void setCurrentProject(String projectName)
  {
    currentProject = projectName;
  }

  public void saveProjectList() throws IOException
  {
    projectFile.writeProjectListFile("ProjectList.bin",projectList);
  }

  public ProjectList readProjectList(String fileName)
      throws IOException, ClassNotFoundException
  {
    return projectFile.readProjectListFile(fileName);

  }

  
  /*TODO: Michael implements the 8 first methods.*/

  public void assignTeamMemberToRequirement(int requirementID, TeamMember teamMember)
  {
    projectList.getProject(currentProject).getRequirementList()
        .getRequirement(requirementID).setResponsibleTeamMember(teamMember);
  }

  public void assignTeamMemberToTask(int taskID, TeamMember teamMember, int reqID) {
    projectList.getProject(currentProject).getRequirementList()
        .getRequirement(reqID).getTaskList().getTask(taskID)
        .getAllTaskEmployees().add(teamMember.copy());

  }

  public void assignTeamMemberRole(String name, String userRole) {
    projectList.getProject(currentProject).getEmployeeList().getEmployee(name).setRole(userRole);
  }

  public void removeTeamMemberFromProject(String projectName, int teamMemberID) {
    projectList.getProject(projectName).getEmployeeList().removeEmployee(teamMemberID);
  }

  public void removeTeamMemberFromRequirement(int requirementID, int teamMemberID) {
    projectList.getProject(currentProject).getRequirementList()
        .getRequirement(requirementID).getEmployees().removeEmployee(teamMemberID);
  }

  public void removeTeamMemberFromTask(int taskID, int reqID, int teamMemberID) {
    projectList.getProject(currentProject).getRequirementList()
        .getRequirement(reqID).getTaskList().getTask(taskID)
        .getAllTaskEmployees().remove(teamMemberID);
  }

  public ProjectList getProjectList() {
    return projectList;
  }

  public EmployeeList getEmployeeList() {
    return employeeList;
  }

  /*TODO: Kasper implements the next 8 methods*/

 /* public Project getProjectInfoByEmployeeName(String name,) {



    return null;

  }*/

  public Project getProjectInfo(String projectName) {

    return projectList.getProject(projectName);
  }

  /*
  public float getProjectProgress(String projectName) {
    return (projectList.getProject(projectName).getRequirementList().getNumberOfRequirements()) / (projectList.getProject(projectName).getRequirementList().getNumberOfApprovedRequirement()) * 100;
  }

    public ArrayList<Project> loadProjectList()
        throws IOException, ClassNotFoundException
    {

       return projectFile.readProjectListFile("ProjectList.bin");
  }


   */

  public void saveProject(String filename) throws IOException
  {
    projectFile.writeEmployeeListFile(employeeList, filename);
  }

  public void addEmployeeToSystem(String name, int ID, String role) {
      employeeList.addTeamMember(name,ID,role);
  }

  public void removeEmployeeFromSystem(int ID) {
    employeeList.removeEmployee(ID);
  }

  public void setProjectStatus(String status, String projectName) {
    projectList.getProject(projectName).setStatus(status);
  }

  /*TODO: Kutaiba implements the next 8*/

  public void setRequirementStatus(String status, int requirementID) {

   projectList.getProject(currentProject).getRequirementList().
       getRequirement(requirementID).setStatus(status);

  }

  public void setTaskStatus(String status, int taskID, int requirementID) {

    projectList.getProject(currentProject).getRequirementList().
        getRequirement(requirementID).getTaskList().getTask(taskID).
        setStatus(status);

  }

  public void setRequirementPriority(String priority, int requirementID) {

    projectList.getProject(currentProject).getRequirementList().
        getRequirement(requirementID).setPriority(priority);

  }

  public String whoWhatWhen(int requirementID) {

    return projectList.getProject(currentProject).getRequirementList().
        getRequirement(requirementID).getUserStory();
  }

  public void registerTaskTime(int time, int requirementID, int taskID) {

    projectList.getProject(currentProject).getRequirementList().
        getRequirement(requirementID).getTaskList().getTask(taskID).
        registerTimeSpent(time);

  }

  public void registerReqTime(int requirementID, int time) {

    projectList.getProject(currentProject).getRequirementList().
        getRequirement(requirementID).setTimeSpendInHours(time);

  }

  public int getTaskTime(String taskName, int requirementID, int taskID) {

    return projectList.getProject(currentProject).getRequirementList().
        getRequirement(requirementID).getTaskList().getTask(taskID).
        getTimeSpendInHours();
  }

  public int getReqTime(int requirementID, int time) {

    return projectList.getProject(currentProject).getRequirementList().
        getRequirement(requirementID).getTimeSpendInHours();
  }

  /*TODO: Lilian implements the next 8.*/

  public RequirementList getRequirementList(String projectName) {

    return projectList.getProject(projectName).getRequirementList();
  }

  public TeamMember getResponsibleTeamMemberReq(String projectName ,int requirementID) {
    return projectList.getProject(projectName).getRequirementList().
        getRequirement(requirementID).getResponsibleTeamMember();
  }

  public TeamMember getResponsibleTeamMemberTask(String projectName ,int taskID, int requirementID) {
    return projectList.getProject(projectName).getRequirementList().
        getRequirement(requirementID).getTaskList().
        getTask(taskID).getResponsibleTeamMember();
  }

  public MyDate getReqDeadline(String projectName ,int requirementID) {
    return projectList.getProject(projectName).getRequirementList().
        getRequirement(requirementID).getDeadline();
  }

  public MyDate getTaskDeadline(String projectName ,int requirementID, int taskID) {
    return projectList.getProject(projectName).getRequirementList().
        getRequirement(requirementID).getTaskList().getTask(taskID).getDeadline();
  }

  public MyDate getProjectDeadline(String projectName) {
    return projectList.getProject(projectName).getDeadline();
  }

  public ArrayList<String> getPriorityList(String projectName, int requirementID)
  {
    return projectList.getProject(projectName).getRequirementList().getRequirement(requirementID).getPriorityList();
  }

  /*TODO: Christian implements the last 8 methods*/

}
