package view_controller;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Customer;
import model.Appointment;

/**
 * 
 * @author Conner Iannotti
 */
public final class CustomersAndAppointmentsFormController {

    @FXML
    private Label customersLabel;

    @FXML
    private Label appointmentsLabel;
    
    @FXML
    private TableView<Customer> customersTable;

    @FXML
    private TableColumn<Customer, Integer> customerIdCustomerTable;

    @FXML
    private TableColumn<Customer, String> customerNameCustomerTable;

    @FXML
    private TableColumn<Customer, String> addressCustomerTable;

    @FXML
    private TableColumn<Customer, String> postalCodeCustomerTable;

    @FXML
    private TableColumn<Customer, String> phoneCustomerTable;

    @FXML
    private TableColumn<Customer, ZonedDateTime> createDateCustomerTable;

    @FXML
    private TableColumn<Customer, String> lastUpdateCustomerTable;

    @FXML
    private TableColumn<Customer, Integer> divisionIdCustomerTable;
    
    @FXML
    private TableColumn<Customer, String> createdByCustomerTable;

    @FXML
    private TableColumn<Customer, String> updatedByCustomerTable;

    @FXML
    private Button addCustomerTable;

    @FXML
    private Button updateCustomerTable;

    @FXML
    private Button deleteCustomerTable;

    @FXML
    private Button refreshCustomerTable;
    
    @FXML
    private TableView<Appointment> appointmentsTable;

    @FXML
    private TableColumn<Appointment, Integer> appointmentIdAppointmentsTable;

    @FXML
    private TableColumn<Appointment, String> titleAppointmentsTable;

    @FXML
    private TableColumn<Appointment, String> descriptionAppointmentsTable;

    @FXML
    private TableColumn<Appointment, String> locationAppointmentsTable;

    @FXML
    private TableColumn<Appointment, Integer> contactAppointmentsTable;

    @FXML
    private TableColumn<Appointment, String> typeAppointmentsTable;

    @FXML
    private TableColumn<Appointment, ZonedDateTime> startDateTimeAppointmentsTable;

    @FXML
    private TableColumn<Appointment, ZonedDateTime> endDateTimeAppointmentsTable;

    @FXML
    private TableColumn<Appointment, Integer> customerIdAppointmentsTable;

    @FXML
    private Button refreshAppointmentsTable;

    @FXML
    private Button addAppointmentsTable;

    @FXML
    private Button updateAppointmentsTable;

    @FXML
    private Button deleteAppointmentsTable;

    @FXML
    private Button viewAllAppointmentsButton;

    @FXML
    private Button generateReportButton;

    @FXML
    private Button exitButton;
    
    private Connection conn = null;
    private String username = null;
    public static String recentChanges = "";
    private final int WITHIN_SECONDS = 15 * 60;
    private final String getCustomers = "SELECT * FROM customers";
    private final String addCustomer = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, "
            + "Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String updateCustomer = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, "
            + "Phone = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, Division_Id = ? "
            + "WHERE Customer_ID = ?";
    private final String deleteCustomer = "DELETE FROM customers WHERE Customer_ID = ?";
    private final String getAllAppointments = "SELECT * FROM appointments";
    private final String addAppointment = "INSERT INTO appointments (Title, Description, Location, Type, Start, "
            + "End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String updateAppointment = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, "
            + "Type = ?, Start = ?, End = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, "
            + "Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
    private final String deleteAppointment = "DELETE FROM appointments WHERE Appointment_ID = ?";
    
    /**Initializes customersTable and appointmentsTable and their columns to model Customer data
     * and Appointment data.
     * 
     */
    public void initialize() {
        customerIdCustomerTable.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameCustomerTable.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressCustomerTable.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCodeCustomerTable.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneCustomerTable.setCellValueFactory(new PropertyValueFactory<>("phone"));
        createDateCustomerTable.setCellValueFactory(new PropertyValueFactory<>("createDate"));
        lastUpdateCustomerTable.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
        divisionIdCustomerTable.setCellValueFactory(new PropertyValueFactory<>("divisionId"));
        createdByCustomerTable.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        updatedByCustomerTable.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));
        
        appointmentIdAppointmentsTable.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleAppointmentsTable.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionAppointmentsTable.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationAppointmentsTable.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactAppointmentsTable.setCellValueFactory(new PropertyValueFactory<>("contactId"));
        typeAppointmentsTable.setCellValueFactory(new PropertyValueFactory<>("type"));
        startDateTimeAppointmentsTable.setCellValueFactory(new PropertyValueFactory<>("start"));
        endDateTimeAppointmentsTable.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerIdAppointmentsTable.setCellValueFactory(new PropertyValueFactory<>("customerId"));
    }
    
    /**Copies the connection and username to the controller for the controller to use.
     * Then, it gets all customers and appointments, creates objects to model their data,
     * connects appointments to their related customers and shows the customers on the
     * customersTable.
     * The lambda used in this method is used to go through each customer for each appointment and add
     * the appointment to the customer if their Customer IDs match. This replaces trying to convert
     * the observable list into an array and looping through it.
     * 
     * @param conn connection to database
     * @param username current user
     * @throws Exception 
     */
    public void setFields(Connection conn, String username) throws Exception {
        this.conn = conn;
        this.username = username;
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        
        try(PreparedStatement ps = conn.prepareStatement(getCustomers)) {
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Timestamp createDate = rs.getTimestamp(6);
                ZonedDateTime createDateZoned = ZonedDateTime.ofInstant(createDate.toInstant(), ZoneId.systemDefault());
                Timestamp lastUpdate = rs.getTimestamp(8);
                ZonedDateTime lastUpdateZoned = ZonedDateTime.ofInstant(lastUpdate.toInstant(), ZoneId.systemDefault());
                
                Customer customer = new Customer(rs.getInt(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5),
                createDateZoned,
                rs.getString(7),
                lastUpdateZoned,
                rs.getString(9),
                rs.getInt(10));
                
                customers.add(customer);
                }
        } catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to load Customer data!", ButtonType.OK);
            alert.showAndWait();
        }
        
        ObservableList<Appointment> scheduledSoon = FXCollections.observableArrayList();
        String alertInfo = "";
        
        try(PreparedStatement ps = conn.prepareStatement(getAllAppointments)) {
            ResultSet rs = ps.executeQuery();
                    
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
                
                if(Duration.between(Instant.now(), startZoned.toInstant()).getSeconds() <= WITHIN_SECONDS &&
                        Duration.between(Instant.now(), startZoned.toInstant()).getSeconds() >= 0) {
                    scheduledSoon.add(appointment);
                    alertInfo += String.format("Appointment ID: %d, Date: %s, Time: %s \n",
                            appointment.getId(), startZoned.toLocalDate().toString(), startZoned.toLocalTime().toString());
                }
                
                customers.forEach((Customer c) -> 
                    {if (c.getId() == appointment.getCustomerId()) c.addAppointment(appointment);});
                }
        } catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to load Appointment data!", ButtonType.OK);
            alert.showAndWait();
        }
        
        if(alertInfo.equals("")) {
            Alert alert = new Alert(Alert.AlertType.NONE, "No appointments scheduled within 15 minutes!", ButtonType.OK);
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.NONE, "Appointments within 15 minutes: \n" + alertInfo, ButtonType.OK);
            alert.showAndWait();
        }
        
        customersTable.setItems(customers);
        appointmentsTable.setItems(scheduledSoon);
    }
    
    /**Called when event is created, gets all customers and appointments, creates objects to model their data,
     * connects appointments to their related customers and shows the customers on the
     * customersTable.
     * The lambda used in this method is used to go through each customer for each appointment and add
     * the appointment to the customer if their Customer IDs match. This replaces trying to convert
     * the observable list into an array and looping through it.
     * 
     * @param event created when the customersTable refresh button is clicked
     * @throws Exception 
     */
    @FXML
    void refreshCustomerTableListener(ActionEvent event) throws Exception {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        
        try(PreparedStatement ps = conn.prepareStatement(getCustomers)) {
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Timestamp createDate = rs.getTimestamp(6);
                ZonedDateTime createDateZoned = ZonedDateTime.ofInstant(createDate.toInstant(), ZoneId.systemDefault());
                Timestamp lastUpdate = rs.getTimestamp(8);
                ZonedDateTime lastUpdateZoned = ZonedDateTime.ofInstant(lastUpdate.toInstant(), ZoneId.systemDefault());
                
                Customer customer = new Customer(rs.getInt(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5),
                createDateZoned,
                rs.getString(7),
                lastUpdateZoned,
                rs.getString(9),
                rs.getInt(10));
                
                customers.add(customer);
                }
        } catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to load Customer data!", ButtonType.OK);
            alert.showAndWait();
        }
        
        try(PreparedStatement ps = conn.prepareStatement(getAllAppointments)) {
            ResultSet rs = ps.executeQuery();
                    
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
                
                customers.forEach((Customer c) -> 
                    {if (c.getId() == appointment.getCustomerId()) c.addAppointment(appointment);});
                }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to load Appointment data!", ButtonType.OK);
            alert.showAndWait();
        }
        
        customersTable.setItems(customers);
    }
    
    /**Called when event is created, opens CustomersAddAndUpdateForm, calls the
     * scenes getCustomer method, and adds it to the database. Then, the customer
     * and appointment data is refreshed.
     * 
     * @param event created when customersTable add button is clicked
     * @throws Exception 
     */
    @FXML
    void addCustomerTableListener(ActionEvent event) throws Exception{
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("CustomersAddAndUpdateForm.fxml"));
            
        Parent parent = loader.load();
        CustomersAddAndUpdateFormController cusAddAndUpdateController = loader.getController();
            
        cusAddAndUpdateController.setConnection(conn, username);
            
        Stage stage = new Stage();
            
        Scene scene = new Scene(parent);
 
        stage.setTitle("Customer add form");
        stage.setScene(scene);
            
        stage.showAndWait();
        
        Customer customer = cusAddAndUpdateController.getCustomer();
        if (customer != null) {
            try(PreparedStatement ps = conn.prepareStatement(addCustomer)) {
                ps.setString(1, customer.getName());
                ps.setString(2, customer.getAddress());
                ps.setString(3, customer.getPostalCode());
                ps.setString(4, customer.getPhone());
                ps.setTimestamp(5, Timestamp.from(customer.getCreateDate().toInstant()));
                ps.setString(6, customer.getCreatedBy());
                ps.setTimestamp(7, Timestamp.from(customer.getLastUpdate().toInstant()));
                ps.setString(8, customer.getLastUpdatedBy());
                ps.setInt(9, customer.getDivisionId());
                ps.executeUpdate();
                
                recentChanges += String.format("Added Customer(ID: %d, Name: %s, Time: %s)\n",
                        customer.getId(), customer.getName(), ZonedDateTime.now().toString());
            } catch(Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to add customer!", ButtonType.OK);
                alert.showAndWait();
            }
            
            refreshCustomerTable.fire();
        }
    }
    
    /**Called when event is created, opens CustomerAddAndUpdateForm, calls the scenes setCustomer
     * method for it to access the customer's data, calls the getCustomer method, and adds it
     * to the database. Then, the customer and appointment data is refreshed.
     * 
     * @param event created when the customersTable update button is clicked
     * @throws Exception 
     */
    @FXML
    void updateCustomerTableListener(ActionEvent event) throws Exception{
        if (customersTable.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a customer in the table.", ButtonType.OK);
            alert.showAndWait();
        }
        else {
            FXMLLoader loader = new FXMLLoader(
            getClass().getResource("CustomersAddAndUpdateForm.fxml"));

            Parent parent = loader.load();
            CustomersAddAndUpdateFormController cusAddAndUpdateController = loader.getController();

            cusAddAndUpdateController.setConnection(conn, username);
            cusAddAndUpdateController.setCustomer(customersTable.getSelectionModel().getSelectedItem());

            Stage stage = new Stage();

            Scene scene = new Scene(parent);

            stage.setTitle("Customer update form");
            stage.setScene(scene);

            stage.showAndWait();

            Customer customer = cusAddAndUpdateController.getCustomer();
            if (customer != null) {
                try(PreparedStatement ps = conn.prepareStatement(updateCustomer)) {
                    ps.setString(1, customer.getName());
                    ps.setString(2, customer.getAddress());
                    ps.setString(3, customer.getPostalCode());
                    ps.setString(4, customer.getPhone());
                    ps.setTimestamp(5, Timestamp.from(customer.getCreateDate().toInstant()));
                    ps.setString(6, customer.getCreatedBy());
                    ps.setTimestamp(7, Timestamp.from(customer.getLastUpdate().toInstant()));
                    ps.setString(8, customer.getLastUpdatedBy());
                    ps.setInt(9, customer.getDivisionId());
                    ps.setInt(10, customer.getId());
                    ps.executeUpdate();
                    
                    recentChanges += String.format("Updated Customer(ID: %d, Name: %s, Time: %s)\n",
                        customer.getId(), customer.getName(), ZonedDateTime.now().toString());
                } catch(Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to update customer!", ButtonType.OK);
                    alert.showAndWait();
                }

                refreshCustomerTable.fire();
            }
        }
    }
    
    /**Called when event is created, gets the selected customer. If a customer is not selected,
     * tells the user to select a customer. If the selected customer has appointments, tells
     * the user that the customer still had appointments. Otherwise, deletes the customer
     * from the database and refreshes customer and appointment data.
     * 
     * @param event created when the customersTable delete button is clicked
     * @throws Exception 
     */
    @FXML
    void deleteCustomerTableListener(ActionEvent event) throws Exception {
        Customer customer = customersTable.getSelectionModel().getSelectedItem();
        if (customer == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a customer in the table.", ButtonType.OK);
            alert.showAndWait();
        }
        else if (!customer.getAppointments().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "This customer still has appointments!", ButtonType.OK);
            alert.showAndWait();
        }
        else {
            try(PreparedStatement ps = conn.prepareStatement(deleteCustomer)) {
                ps.setInt(1, customer.getId());
                ps.executeUpdate();
                
                recentChanges += String.format("Deleted Customer(ID: %d, Name: %s, Time: %s)\n",
                        customer.getId(), customer.getName(), ZonedDateTime.now().toString());
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        String.format("Deleted Customer(ID: %d, Name: %s)",
                        customer.getId(), customer.getName()),
                        ButtonType.OK);
                alert.setResizable(true);
                alert.showAndWait();
            } catch(Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to delete customer!", ButtonType.OK);
                alert.showAndWait();
            }
            refreshCustomerTable.fire();
        }
    }
    
    /**Called when event is created, gets the selected customer. If no customer is selected,
     * tells the user to select a customer. Otherwise, gets the associated appointments
     * and displays them in the appointmentsTable.
     * 
     * @param event created when the appointmentsTable refresh button is clicked
     */
    @FXML
    void refreshAppointmentsTableListener(ActionEvent event) {
        if(customersTable.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a customer from the customers table!", ButtonType.OK);
            alert.showAndWait();
        }
        else {
            appointmentsTable.setItems(customersTable.getSelectionModel().getSelectedItem().getAppointments());
        }
    }
    
    /**Called when event is created, opens AppointmentsAddAndUpdateForm, calls the
     * scenes getAppointment method, and adds it to the database. Then, the customer
     * and appointment data is refreshed.
     * 
     * @param event created when the appointmentsTable add button is clicked
     * @throws Exception 
     */
    @FXML
    void addAppoinmentsTableListener(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("AppointmentsAddAndUpdateForm.fxml"));
            
        Parent parent = loader.load();
        AppointmentsAddAndUpdateFormController appAddAndUpdateController = loader.getController();
            
        appAddAndUpdateController.setConnection(conn, username);
            
        Stage stage = new Stage();
            
        Scene scene = new Scene(parent);
 
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
                
                recentChanges += String.format("Added Appointment(ID: %d, Type: %s, Title: %s, Time: %s)\n",
                        appointment.getId(), appointment.getType(), appointment.getTitle(), ZonedDateTime.now().toString());
            } catch(Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to add appointment!", ButtonType.OK);
                alert.showAndWait();
            }
            
            refreshCustomerTable.fire();
        }
    }
    
    /**Called when event is created, opens AppointmentsAddAndUpdateForm, calls the scenes setAppointment
     * method for it to access the appointment's data, calls the getAppointment method, and adds it
     * to the database. Then, the customer and appointment data is refreshed.
     * 
     * @param event created when the appointmentsTable update button is clicked
     * @throws Exception 
     */
    @FXML
    void updateAppointmentsTableListener(ActionEvent event) throws Exception {
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

            Stage stage = new Stage();

            Scene scene = new Scene(parent);

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
                    
                    recentChanges += String.format("Updated Appointment(ID: %d, Type: %s, Title: %s, Time: %s)\n",
                        appointment.getId(), appointment.getType(), appointment.getTitle(), ZonedDateTime.now().toString());
                } catch(Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to update appointment!", ButtonType.OK);
                    alert.showAndWait();
                }

                refreshCustomerTable.fire();
            }
        }
    }
    
    /**Called when event is created, gets the selected appointment. If an appointment
     * is not selected, tells the user to select an appointment. Otherwise, deletes
     * the appointment from the database and refreshes the customer and appointment
     * data.
     * 
     * @param event created when the appointmentsTable delete button is clicked
     * @throws Exception 
     */
    @FXML
    void deleteAppointmentsTableListener(ActionEvent event) throws Exception {
        Appointment appointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select an appointment in the table.", ButtonType.OK);
            alert.showAndWait();
        }
        else {
            try(PreparedStatement ps = conn.prepareStatement(deleteAppointment)) {
                ps.setInt(1, appointmentsTable.getSelectionModel().getSelectedItem().getId());
                ps.executeUpdate();
                
                recentChanges += String.format("Deleted Appointment(ID: %d, Type: %s, Title: %s, Time: %s)\n",
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
            refreshCustomerTable.fire();
        }
    }
    
    /** Called when even is created, opens AppointmentsForm and refreshes customer
     * and appointment data when it is closed.
     * 
     * @param event created when View All Appointments button is clicked
     * @throws Exception 
     */
    @FXML
    void viewAllAppointmentsButtonListener(ActionEvent event) throws Exception{
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("AppointmentsForm.fxml"));
            
        Parent parent = loader.load();
        AppointmentsFormController appController = loader.getController();
            
        appController.setConnection(conn, username);
            
        Stage stage = new Stage();
            
        Scene scene = new Scene(parent);
 
        stage.setTitle("All Appointments");
        stage.setScene(scene);
            
        stage.showAndWait();
        refreshCustomerTable.fire();
    }
    
    /**Called when event is created, opens ReportForm.
     * 
     * @param event created when Generate Report button is clicked
     * @throws Exception 
     */
    @FXML
    void generateReportButtonListener(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("ReportForm.fxml"));
            
        Parent parent = loader.load();
        ReportFormController reportController = loader.getController();
            
        reportController.setConnection(conn, username);
            
        Stage stage = new Stage();
            
        Scene scene = new Scene(parent);
 
        stage.setTitle("Reports");
        stage.setScene(scene);
            
        stage.showAndWait();
    }
    
    /**Called when event is created, closes CustomersAndAppointmentsForm.
     * 
     * @param event created when Exit button is clicked
     */
    @FXML
    void exitButtonListener(ActionEvent event) {
        Stage stage = (Stage)exitButton.getScene().getWindow();
        stage.close();
    }
}
