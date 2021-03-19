package view_controller;

import java.sql.*;
import java.time.Month;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

/**
 * 
 * @author Conner Iannotti
 */
public final class ReportFormController {

    @FXML
    private Label selectReportLabel;

    @FXML
    private RadioButton totalAppointmentsRB;

    @FXML
    private RadioButton contactScheduleRB;

    @FXML
    private ToggleGroup selectReportRB;

    @FXML
    private RadioButton recentChangesRB;

    @FXML
    private TextArea reportTextArea;

    @FXML
    private Button exitButton;
    
    private Connection conn = null;
    private String username = null;
    private final String getContacts = "SELECT Contact_ID, Contact_Name FROM contacts";
    private final String contactAppointments = "SELECT * FROM appointments WHERE Contact_ID = ?";
    private final String getAppointments = "SELECT Type, MONTH(Start) FROM appointments ORDER BY 1, 2";
    
    /**Copies database connection and the current user to the controller
     * for it to use.
     * 
     * @param conn connection to database
     * @param username current user
     */
    public void setConnection(Connection conn, String username) {
        this.conn = conn;
        this.username = username;
    }
    
    /**Called when event is created, gets all appointments by type and displays
     * the number for each month.
     * 
     * @param event created when Total Appointments button is clicked
     * @throws Exception 
     */
    @FXML
    void totalAppointmentsRBListener(ActionEvent event) throws Exception {
        try(PreparedStatement ps = conn.prepareStatement(getAppointments)) {
            ResultSet rs = ps.executeQuery();
            String resultString = "";
            String currentType = "";
            int currentMonth = 1;
            int count = 0;
            
            while(rs.next()) {
                if(!rs.getString(1).equals(currentType)) {
                    if(!currentType.equals("")) {
                        resultString += "\t" + Month.of(currentMonth).toString() + ": " + count + "\n";
                    }
                    
                    currentType = rs.getString(1);
                    resultString += currentType + ": \n";
                    currentMonth = rs.getInt(2);
                    count = 1;
                }
                else if(rs.getInt(2) != currentMonth) {
                    resultString += "\t" + Month.of(currentMonth).toString() + ": " + count + "\n";
                    currentMonth = rs.getInt(2);
                    count = 1;
                }
                else {
                    count += 1;
                }
            }
            
            resultString += "\t" + Month.of(currentMonth).toString() + ": " + count + "\n";
            
            reportTextArea.setText(resultString);
        } catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to load report!", ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    /**Called when event is created, displays each contact and their appointments.
     * 
     * @param event created when Contact Schedule button is clicked
     * @throws Exception 
     */
    @FXML
    void contactScheduleRBListener(ActionEvent event) throws Exception {
        try(PreparedStatement ps = conn.prepareStatement(getContacts)) {
            ResultSet rs = ps.executeQuery();
            String resultString = "";
            
            while(rs.next()) {
                resultString += String.format("Contact Name: %s, ID: %s\n", rs.getString(2), rs.getString(1));
                
                try(PreparedStatement psApp = conn.prepareStatement(contactAppointments)) {
                    psApp.setInt(1, rs.getInt(1));
                    ResultSet rsApp = psApp.executeQuery();
                    
                    while(rsApp.next()) {
                        resultString += String.format("\t Appointment ID: %s, Title: %s, Type: %s, Description: %s, "
                                + "Start: %s, End: %s, Customer ID: %s\n", rsApp.getString(1), rsApp.getString(2), rsApp.getString(5), 
                                rsApp.getString(3), rsApp.getString(6), rsApp.getString(7), rsApp.getString(12));
                    }
                }
                
                resultString += "\n";
            }
            
            reportTextArea.setText(resultString);
        } catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to load report!", ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    /**Called when event is created, displays all the changes to customers and appointments
     * in the database made during the session. The information displayed is the id, name, and time of
     * change in user's timezone for customers and id, type, title, and time of change for appointments.
     * 
     * @param event created when Recent Changes button is clicked
     */
    @FXML
    void recentChangesRBListener(ActionEvent event) {
        reportTextArea.setText(CustomersAndAppointmentsFormController.recentChanges);
    }
    
    /**Called when event is created, closes ReportForm.
     * 
     * @param event created when Exit button is clicked
     */
    @FXML
    void exitButtonListener(ActionEvent event) {
        Stage stage = (Stage)exitButton.getScene().getWindow();
        stage.close();
    }

}
