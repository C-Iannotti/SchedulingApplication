package view_controller;

import java.sql.*;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;

/**
 * 
 * @author Conner Iannotti
 */
public final class AppointmentsAddAndUpdateFormController {

    @FXML
    private Label appointmentIdLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label contactLabel;

    @FXML
    private Label typeLabel;

    @FXML
    private Label startDateLabel;

    @FXML
    private Label startTimeLabel;

    @FXML
    private Label endDateLabel;

    @FXML
    private Label customerIdLabel;

    @FXML
    private Label userIdLabel;

    @FXML
    private Label endTimeZoneLabel;

    @FXML
    private Label startTimeZoneLabel;

    @FXML
    private ComboBox<String> contactComboBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField appointmentIdField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField locationField;

    @FXML
    private TextField startTimeField;

    @FXML
    private TextField customerIdField;

    @FXML
    private TextField typeField;

    @FXML
    private TextField userIdField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField endTimeField;
    
    private final int OPEN_OFFICE = 8 * 60;
    private final int CLOSE_OFFICE = 22 * 60;
    private Connection conn = null;
    private String username = null;
    private Appointment appointment = null;
    private final String getContacts = "SELECT * FROM contacts";
    private final String getAppointmentTimes = "SELECT Start, End, Appointment_ID FROM appointments";
    private final String getUserIds = "SELECT User_ID FROM users";
    private final String getCustomerIds = "SELECT Customer_ID FROM customers";
    
    /**Sets the time zone labels to display the user's time zone
     * 
     */
    public void initialize() {
        startTimeZoneLabel.setText(ZoneId.systemDefault().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
        endTimeZoneLabel.setText(ZoneId.systemDefault().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
    }
    
    /**Copies the database connection and current user for the controller to use,
     * then gets the contacts from the database to display in a list.
     * 
     * @param conn database connection
     * @param username current user
     * @throws Exception 
     */
    public void setConnection(Connection conn, String username) throws Exception {
        this.conn = conn;
        this.username = username;
        
        try(PreparedStatement ps = conn.prepareStatement(getContacts)) {
            ResultSet rs = ps.executeQuery();
            ObservableList<String> contacts = FXCollections.observableArrayList();
            
            while(rs.next()) {
                contacts.add(rs.getString(1) + " " + rs.getString(2));
            }
            
            contactComboBox.setItems(contacts);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to load Contact data!", ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    /**Sets the appointment for the controller to use, then updates the information
     * displayed to show the appointment's data.
     * The lambda is used to go through each item in the contact combo box and select
     * it if it matches the appointment's contactId. This is used instead of converting
     * the list into an array, finding the item that matches, and then going back through
     * the list to select it.
     * 
     * @param appointment
     * @throws Exception 
     */
    public void setAppointment(Appointment appointment) throws Exception {
        this.appointment = appointment;
        
        appointmentIdField.setText(Integer.toString(appointment.getId()));
        titleField.setText(appointment.getTitle());
        descriptionField.setText(appointment.getDescription());
        locationField.setText(appointment.getLocation());
        typeField.setText(appointment.getType());
        customerIdField.setText(Integer.toString(appointment.getCustomerId()));
        userIdField.setText(Integer.toString(appointment.getUserId()));
        
        startDatePicker.setValue(appointment.getStart().toLocalDate());
        String startTime = appointment.getStart().toLocalTime().toString().substring(0, 5);
        startTimeField.setText(startTime);
        
        endDatePicker.setValue(appointment.getEnd().toLocalDate());
        String endTime = appointment.getEnd().toLocalTime().toString().substring(0, 5);
        endTimeField.setText(endTime);
        
        int contactId = appointment.getContactId();
        contactComboBox.getItems().forEach((String c) -> 
        {if(Character.getNumericValue(c.charAt(0)) == contactId) contactComboBox.getSelectionModel().select(c);
        });
    }
    
    /**Gets the appointment the controller is using
     * 
     * @return appointment
     */
    public Appointment getAppointment() {
        if (appointment == null) {
            return null;
        }
        
        return appointment.copy();
    }
    
    /**Called when event is created, creates or updates an Appointment object based on
     * the information in the fields on the form, then closes AppointmentsAddAndUpdateForm.
     * 
     * @param event created when Save button is clicked
     */
    @FXML
    void saveButtonListener(ActionEvent event) throws Exception {
        try(PreparedStatement ps = conn.prepareStatement(getAppointmentTimes)) {
            if(titleField.getText().equals("") || 
                    descriptionField.getText().equals("") || 
                    locationField.getText().equals("") || 
                    contactComboBox.getSelectionModel().isEmpty() ||
                    typeField.getText().equals("") || 
                    startDatePicker.getValue() == null ||
                    startTimeField.getText().equals("") ||
                    endDatePicker.getValue() == null ||
                    endTimeField.getText().equals("") || 
                    customerIdField.getText().equals("") || 
                    userIdField.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "There is an empty field!", ButtonType.OK);
                alert.showAndWait();
                throw new Exception();
            }
            
            if(appointment == null) {
                appointment = new Appointment(0, null, null, null, null, null, null,
                        ZonedDateTime.now(), username, null, null, 0, 0, 0);
            }
            
            ZonedDateTime start = ZonedDateTime.of(startDatePicker.getValue(), 
                    LocalTime.parse(("00000" + startTimeField.getText()).substring(startTimeField.getText().length())), ZoneId.systemDefault());
            ZonedDateTime startET = start.withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime end = ZonedDateTime.of(endDatePicker.getValue(), 
                    LocalTime.parse(("00000" + endTimeField.getText()).substring(endTimeField.getText().length())), ZoneId.systemDefault());
            ZonedDateTime endET = end.withZoneSameInstant(ZoneId.of("America/New_York"));
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                if (((start.toInstant().compareTo(rs.getTimestamp(1).toInstant()) >= 0 &&
                        start.toInstant().compareTo(rs.getTimestamp(2).toInstant()) < 0) ||
                        (end.toInstant().compareTo(rs.getTimestamp(1).toInstant()) > 0 &&
                        end.toInstant().compareTo(rs.getTimestamp(2).toInstant()) <= 0)) &&
                        appointment.getId() != rs.getInt(3)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Overlapping appointment times!", ButtonType.OK);
                    alert.showAndWait();
                    throw new Exception();
                }
            }
            if(start.compareTo(end) > 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "The end time cannot be before the start!", ButtonType.OK);
                    alert.showAndWait();
                    throw new Exception();
                }
                
            if((startET.getHour() * 60) + start.getMinute() < OPEN_OFFICE ||
                    (endET.getHour() * 60) + end.getMinute() > CLOSE_OFFICE) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Not within office hours (8:00 AM and 10:00 PM ET", ButtonType.OK);
                alert.showAndWait();
                throw new Exception();
            }
                
            try(PreparedStatement userId = conn.prepareStatement(getUserIds)) {
                ResultSet userIdRS = userId.executeQuery();
                boolean foundUserId = false;
                
                while(userIdRS.next()) {
                    if(userIdRS.getString(1).equals(userIdField.getText())) {
                        appointment.setUserId(Integer.parseInt(userIdField.getText()));
                        foundUserId = true;
                    }
                }
                
                if(!foundUserId) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid User ID!", ButtonType.OK);
                    alert.showAndWait();
                    throw new Exception();
                }
            }
                
            try(PreparedStatement customerId = conn.prepareStatement(getCustomerIds)) {
                ResultSet customerIdRS = customerId.executeQuery();
                boolean foundCustomerId = false;
                    
                while(customerIdRS.next()) {
                    if(customerIdRS.getString(1).equals(customerIdField.getText())) {
                        appointment.setUserId(Integer.parseInt(customerIdField.getText()));
                        foundCustomerId = true;
                    }
                }
                    
                if(!foundCustomerId) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Customer ID!", ButtonType.OK);
                    alert.showAndWait();
                    throw new Exception();
                }
            }
                
            appointment.setTitle(titleField.getText());
            appointment.setDescription(titleField.getText());
            appointment.setLocation(titleField.getText());
            appointment.setType(titleField.getText());
            appointment.setStart(start);
            appointment.setEnd(end);
            appointment.setCustomerId(Integer.parseInt(customerIdField.getText()));
            appointment.setUserId(Integer.parseInt(userIdField.getText()));
            appointment.setLastUpdate(ZonedDateTime.now());
            appointment.setLastUpdatedBy(username);
            appointment.setContactId(Character.getNumericValue(contactComboBox.getSelectionModel().getSelectedItem().charAt(0)));

            Stage stage = (Stage)saveButton.getScene().getWindow();
            stage.close();
        } catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to save information!", ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    /**Called when event is created, sets the appointment to null and closes the form.
     * 
     * @param event created when Cancel button is clicked
     */
    @FXML
    void cancelButtonListener(ActionEvent event) {
        appointment = null;
        Stage stage = (Stage)cancelButton.getScene().getWindow();
        stage.close();
    }
}
