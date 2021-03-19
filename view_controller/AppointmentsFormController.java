package view_controller;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;

/**
 * 
 * @author Conner Iannotti
 */
public final class AppointmentsFormController {

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button exitButton;

    @FXML
    private RadioButton monthRB;
    
    @FXML
    private RadioButton viewAllRB;

    @FXML
    private ToggleGroup sortByRB;

    @FXML
    private RadioButton weekRB;

    @FXML
    private Label sortByLabel;

    @FXML
    private DatePicker selectDatePicker;

    @FXML
    private Label selectDateLabel;

    @FXML
    private TableView<Appointment> appointmentsTable;

    @FXML
    private TableColumn<Appointment, Integer> appointmentId;

    @FXML
    private TableColumn<Appointment, String> title;

    @FXML
    private TableColumn<Appointment, String> description;

    @FXML
    private TableColumn<Appointment, String> locationColumn;

    @FXML
    private TableColumn<Appointment, Integer> contact;

    @FXML
    private TableColumn<Appointment, String> type;

    @FXML
    private TableColumn<Appointment, ZonedDateTime> startDateTime;

    @FXML
    private TableColumn<Appointment, ZonedDateTime> endDateTime;

    @FXML
    private TableColumn<Appointment, Integer> customerId;
    
    private Connection conn = null;
    private String username = null;
    private final String getAllAppointments = "SELECT * FROM appointments";
    private final String addAppointment = "INSERT INTO appointments (Title, Description, Location, Type, Start, "
            + "End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String updateAppointment = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, "
            + "Type = ?, Start = ?, End = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, "
            + "Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
    private final String deleteAppointment = "DELETE FROM appointments WHERE Appointment_ID = ?";
    
    /**Initializes appointmentsTable and its columns to model Appointment data.
     * 
     */
    public void initialize() {
        appointmentId.setCellValueFactory(new PropertyValueFactory<>("id"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        contact.setCellValueFactory(new PropertyValueFactory<>("contactId"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        startDateTime.setCellValueFactory(new PropertyValueFactory<>("start"));
        endDateTime.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
    }
    
    /**Copies the database connection and the current user for the controller to use.
     * 
     * @param conn database connection
     * @param username current user
     */
    public void setConnection(Connection conn, String username) {
        this.conn = conn;
        this.username = username;
    }
    
    /**Called when event is created, finds all appointments that match the selected date's
     * month and displays them in the appointments table.
     * 
     * @param event created when month radio button is clicked
     * @throws Exception 
     */
    @FXML
    void monthRBListener(ActionEvent event) throws Exception {
        try(PreparedStatement ps = conn.prepareStatement(getAllAppointments)) {
            ResultSet rs = ps.executeQuery();
            ObservableList<Appointment> appointments = FXCollections.observableArrayList();
            
            while(rs.next()) {
                Timestamp start = rs.getTimestamp(6);
                ZonedDateTime startZoned = ZonedDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault());
                Timestamp end = rs.getTimestamp(7);
                ZonedDateTime endZoned = ZonedDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault());
                Timestamp create = rs.getTimestamp(8);
                ZonedDateTime createZoned = ZonedDateTime.ofInstant(create.toInstant(), ZoneId.systemDefault());
                Timestamp update = rs.getTimestamp(10);
                ZonedDateTime updateZoned = ZonedDateTime.ofInstant(update.toInstant(), ZoneId.systemDefault());
                
                if(selectDatePicker.getValue() == null ||
                        startZoned.getMonth().equals(selectDatePicker.getValue().getMonth())){
                    Appointment appointment = new Appointment(rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    startZoned,
                    endZoned,
                    createZoned,
                    rs.getString(9),
                    updateZoned,
                    rs.getString(11),
                    rs.getInt(12),
                    rs.getInt(13),
                    rs.getInt(14));

                    appointments.add(appointment);
                }
            }
            
            appointmentsTable.setItems(appointments);
        } catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to load appointments!", ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    /**Called when event is created, finds all appointments that match the selected date's
     * week and displays them on appointmentsTable
     * 
     * @param event created when week radio button is clicked
     * @throws Exception 
     */
    @FXML
    void weekRBListener(ActionEvent event) throws Exception {
        try(PreparedStatement ps = conn.prepareStatement(getAllAppointments)) {
            ResultSet rs = ps.executeQuery();
            ObservableList<Appointment> appointments = FXCollections.observableArrayList();
            
            while(rs.next()) {
                Timestamp start = rs.getTimestamp(6);
                ZonedDateTime startZoned = ZonedDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault());
                Timestamp end = rs.getTimestamp(7);
                ZonedDateTime endZoned = ZonedDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault());
                Timestamp create = rs.getTimestamp(8);
                ZonedDateTime createZoned = ZonedDateTime.ofInstant(create.toInstant(), ZoneId.systemDefault());
                Timestamp update = rs.getTimestamp(10);
                ZonedDateTime updateZoned = ZonedDateTime.ofInstant(update.toInstant(), ZoneId.systemDefault());
                TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
                
                if(selectDatePicker.getValue() == null ||
                        startZoned.get(woy) == selectDatePicker.getValue().get(woy)){
                    Appointment appointment = new Appointment(rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    startZoned,
                    endZoned,
                    createZoned,
                    rs.getString(9),
                    updateZoned,
                    rs.getString(11),
                    rs.getInt(12),
                    rs.getInt(13),
                    rs.getInt(14));

                    appointments.add(appointment);
                }
            }
            
            appointmentsTable.setItems(appointments);
        } catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to load appointments!", ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    /**Called when event is created, displays all appointments on in appointmentTable
     * 
     * @param event created when view all radio button is clicked
     * @throws Exception 
     */
    @FXML
    void viewAllRBListener(ActionEvent event) throws Exception {
        try(PreparedStatement ps = conn.prepareStatement(getAllAppointments)) {
            ResultSet rs = ps.executeQuery();
            ObservableList<Appointment> appointments = FXCollections.observableArrayList();
            
            while(rs.next()) {
                Timestamp start = rs.getTimestamp(6);
                ZonedDateTime startZoned = ZonedDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault());
                Timestamp end = rs.getTimestamp(7);
                ZonedDateTime endZoned = ZonedDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault());
                Timestamp create = rs.getTimestamp(8);
                ZonedDateTime createZoned = ZonedDateTime.ofInstant(create.toInstant(), ZoneId.systemDefault());
                Timestamp update = rs.getTimestamp(10);
                ZonedDateTime updateZoned = ZonedDateTime.ofInstant(update.toInstant(), ZoneId.systemDefault());
                        
                Appointment appointment = new Appointment(rs.getInt(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5),
                startZoned,
                endZoned,
                createZoned,
                rs.getString(9),
                updateZoned,
                rs.getString(11),
                rs.getInt(12),
                rs.getInt(13),
                rs.getInt(14));
                
                appointments.add(appointment);
            }
            
            appointmentsTable.setItems(appointments);
        } catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to load appointments!", ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    /**Called when event is created, opens AppointmentsAddAndUpdateForm, calls the
     * scenes getAppointment method, and adds it to the database.
     * 
     * @param event created when the add button is clicked
     * @throws Exception 
     */
    @FXML
    void addButtonListener(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("AppointmentsAddAndUpdateForm.fxml"));
            
        Parent parent = loader.load();
        AppointmentsAddAndUpdateFormController appAddAndUpdateController = loader.getController();
            
        appAddAndUpdateController.setConnection(conn, username);
            
        //Created new stage.
        Stage stage = new Stage();
            
        // Build the scene graph.
        Scene scene = new Scene(parent);
 
        // Display our window, using the scene graph.
        stage.setTitle("Appointment add form");
        stage.setScene(scene);
            
        stage.showAndWait();
        
        Appointment appointment = appAddAndUpdateController.getAppointment();
        if (appointment != null) {
            try(PreparedStatement ps = conn.prepareStatement(addAppointment)) {
                ps.setString(1, appointment.getTitle());
                ps.setString(2, appointment.getDescription());
                ps.setString(3, appointment.getLocation());
                ps.setString(4, appointment.getType());
                ps.setTimestamp(5, Timestamp.from(appointment.getStart().toInstant()));
                ps.setTimestamp(6, Timestamp.from(appointment.getEnd().toInstant()));
                ps.setTimestamp(7, Timestamp.from(appointment.getCreateDate().toInstant()));
                ps.setString(8, appointment.getCreatedBy());
                ps.setTimestamp(9, Timestamp.from(appointment.getLastUpdate().toInstant()));
                ps.setString(10, appointment.getLastUpdatedBy());
                ps.setInt(11, appointment.getCustomerId());
                ps.setInt(12, appointment.getUserId());
                ps.setInt(13, appointment.getContactId());
                ps.executeUpdate();
                
                CustomersAndAppointmentsFormController.recentChanges += String.format("Added Appointment(ID: %d, Type: %s, Title: %s, Time: %s)\n",
                        appointment.getId(), appointment.getType(), appointment.getTitle(), ZonedDateTime.now().toString());
            } catch(Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to add appointment!", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }
    
    /**Called when event is created, opens AppointmentsAddAndUpdateForm, calls the scenes setAppointment
     * method for it to access the appointment's data, calls the getAppointment method, and adds it
     * to the database.
     * 
     * @param event created when the update button is clicked
     * @throws Exception 
     */
    @FXML
    void updateButtonListener(ActionEvent event) throws Exception {
        if(appointmentsTable.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select an appointment!", ButtonType.OK);
            alert.showAndWait();
        }
        else {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("AppointmentsAddAndUpdateForm.fxml"));

            Parent parent = loader.load();
            AppointmentsAddAndUpdateFormController appAddAndUpdateController = loader.getController();

            appAddAndUpdateController.setConnection(conn, username);
            appAddAndUpdateController.setAppointment(appointmentsTable.getSelectionModel().getSelectedItem());

            //Created new stage.
            Stage stage = new Stage();

            // Build the scene graph.
            Scene scene = new Scene(parent);

            // Display our window, using the scene graph.
            stage.setTitle("Appointment add form");
            stage.setScene(scene);

            stage.showAndWait();

            Appointment appointment = appAddAndUpdateController.getAppointment();
            if (appointment != null) {
                try(PreparedStatement ps = conn.prepareStatement(updateAppointment)) {
                    ps.setString(1, appointment.getTitle());
                    ps.setString(2, appointment.getDescription());
                    ps.setString(3, appointment.getLocation());
                    ps.setString(4, appointment.getType());
                    ps.setTimestamp(5, Timestamp.from(appointment.getStart().toInstant()));
                    ps.setTimestamp(6, Timestamp.from(appointment.getEnd().toInstant()));
                    ps.setTimestamp(7, Timestamp.from(appointment.getCreateDate().toInstant()));
                    ps.setString(8, appointment.getCreatedBy());
                    ps.setTimestamp(9, Timestamp.from(appointment.getLastUpdate().toInstant()));
                    ps.setString(10, appointment.getLastUpdatedBy());
                    ps.setInt(11, appointment.getCustomerId());
                    ps.setInt(12, appointment.getUserId());
                    ps.setInt(13, appointment.getContactId());
                    ps.setInt(14, appointment.getId());
                    ps.executeUpdate();
                    
                    CustomersAndAppointmentsFormController.recentChanges += String.format("Updated Appointment(ID: %d, Type: %s, Title: %s, Time: %s)\n",
                        appointment.getId(), appointment.getType(), appointment.getTitle(), ZonedDateTime.now().toString());
                } catch(Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to update appointment!", ButtonType.OK);
                    alert.showAndWait();
                }
            }
        }
    }
    
    /**Called when event is created, gets the selected appointment. If an appointment
     * is not selected, tells the user to select an appointment. Otherwise, deletes
     * the appointment from the database.
     * 
     * @param event created when the delete button is clicked
     * @throws Exception 
     */
    @FXML
    void deleteButtonListener(ActionEvent event) throws Exception {
        Appointment appointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select an appointment in the table.", ButtonType.OK);
            alert.showAndWait();
        }
        else {
            try(PreparedStatement ps = conn.prepareStatement(deleteAppointment)) {
                ps.setInt(1, appointmentsTable.getSelectionModel().getSelectedItem().getId());
                ps.executeUpdate();
                
                CustomersAndAppointmentsFormController.recentChanges += String.format("Deleted Appointment(ID: %d, Type: %s, Title: %s, Time: %s)\n",
                        appointment.getId(), appointment.getType(), appointment.getTitle(), ZonedDateTime.now().toString());
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        String.format("Deleted Appointment(ID: %d, Type: %s, Title: %s)",
                        appointment.getId(), appointment.getType(), appointment.getTitle()),
                        ButtonType.OK);
                alert.setResizable(true);
                alert.showAndWait();
            } catch(Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to delete appointment!", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }
    
    /**Called when event is created, closes AppointmentsForm.
     * 
     * @param event created when Exit button is clicked
     */
    @FXML
    void exitButtonListener(ActionEvent event) {
        Stage stage = (Stage)exitButton.getScene().getWindow();
        stage.close();
    }
}
