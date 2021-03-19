package view_controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.*;
import java.time.ZonedDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.Customer;

/**
 * 
 * @author Conner Iannotti
 */
public final class CustomersAddAndUpdateFormController {

    @FXML
    private Label idLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label nameLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label postalCodeLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label countryLabel;

    @FXML
    private Label firstLevelDivisionLabel;

    @FXML
    private TextField idField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField postalCodeField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<String> countryField;

    @FXML
    private ComboBox<String> firstLevelDivisionField;
    
    private Connection conn = null;
    private String username = null;
    private Customer customer = null;
    private final String getCountries = "SELECT * FROM countries";
    private final String getDivisions = "SELECT * FROM first_level_divisions";
    private final String getCountryDivisions = "SELECT * FROM first_level_divisions WHERE Country_ID = ?";
    
    /**Copies database connection and the current user for the controller to use.
     * Then, gets the country information from the database to display in a list.
     * 
     * @param conn database connection
     * @param username current user
     * @throws Exception 
     */
    public void setConnection(Connection conn, String username) throws Exception {
        this.conn = conn;
        this.username = username;
        
        try(PreparedStatement ps = conn.prepareStatement(getCountries)) {
            ResultSet rs = ps.executeQuery();
            ObservableList<String> countries = FXCollections.observableArrayList();
            
            while(rs.next()) {
                countries.add(rs.getString(1) + " " + rs.getString(2));
            }
            
            countryField.setItems(countries);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to load Country data!", ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    /**Sets the customer for the controller to use, then updates the information
     * displayed to show the customer's data.
     * First lambda is used to select a country from countryField by comparing the Country ID
     * to the number at the beginning of each item in the list. This replaces having
     * to convert the observable list into an array and looping through it.
     * Second lambda is used to select a division from firstLevelDivisionField through a similar
     * method. This replaces having to convert the observable list into an array.
     * 
     * @param customer
     * @throws Exception 
     */
    public void setCustomer(Customer customer) throws Exception {
        this.customer = customer;
        
        idField.setText(Integer.toString(customer.getId()));
        nameField.setText(customer.getName());
        addressField.setText(customer.getAddress());
        postalCodeField.setText(customer.getPostalCode());
        phoneField.setText(customer.getPhone());
        
        try(PreparedStatement ps = conn.prepareStatement(getCountries)) {
            ResultSet rs = ps.executeQuery();
            ObservableList<String> countries = FXCollections.observableArrayList();
            
            while(rs.next()) {
                countries.add(rs.getString(1) + " " + rs.getString(2));
            }
            
            countryField.setItems(countries);
        } catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to load Country data!", ButtonType.OK);
            alert.showAndWait();
        }
        
        try(PreparedStatement ps = conn.prepareStatement(getDivisions)) {
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                if(customer.getDivisionId() == rs.getInt(1)) {
                    ObservableList<String> countries= countryField.getItems();
                    Integer countryId = rs.getInt(7);
                    Integer divisionId = rs.getInt(1);
                    
                    countries.forEach((String c) ->
                    {if(Character.getNumericValue(c.charAt(0)) == countryId) countryField.getSelectionModel().select(c);
                    });
                    
                    setDivisionField();
                    firstLevelDivisionField.getItems().forEach((String d) ->
                    {if(Integer.parseInt(d.substring(0, 3)) == divisionId) {firstLevelDivisionField.getSelectionModel().select(d); System.out.println("Made it!");}
                    });
                }
            }
            
        } catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to load Division data!", ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    /**Gets the customer the controller is using
     * 
     * @return customer
     */
    public Customer getCustomer() {
        if (customer == null) {
            return null;
        }
        
        return customer.copy();
    }
    
    /**Sets the divisions available to choose from by finding the divisions for the
     * country that is selected.
     * 
     * @throws Exception 
     */
    private void setDivisionField() throws Exception {
        if (!countryField.getSelectionModel().isEmpty()) {
            try(PreparedStatement ps = conn.prepareStatement(getCountryDivisions)) {
                ps.setInt(1, Character.getNumericValue(countryField.getSelectionModel().getSelectedItem().charAt(0)));
                ResultSet rs = ps.executeQuery();
                ObservableList<String> divisions = FXCollections.observableArrayList();
                
                while(rs.next()) {
                    divisions.add(String.format("%03d", rs.getInt(1)) + " " + rs.getString(2));
                }
                
                firstLevelDivisionField.setItems(divisions);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to load Country data!", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }
    
    /**Calls setDivisionField when event is created.
     * 
     * @param event created when firstLevelDivisionField list is opened
     * @throws Exception 
     */
    @FXML
    void firstLevelDivisionFieldListener(Event event) throws Exception {
        setDivisionField();
    }
    
    /**Called when event is created, creates or updates a Customer object based on
     * the information in the fields on the form, then closes CustomersAddAndUpdateForm.
     * 
     * @param event created when Save button is clicked
     */
    @FXML
    void saveButtonListener(ActionEvent event) {
        try {
            if(nameField.getText().equals("") ||
                    addressField.getText().equals("") ||
                    postalCodeField.getText().equals("") ||
                    phoneField.getText().equals("") ||
                    firstLevelDivisionField.getSelectionModel().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "There is an empty field!", ButtonType.OK);
                alert.showAndWait();
                throw new Exception();
            }
            
            if (customer == null) {
                customer = new Customer(0, null, null, null, null, ZonedDateTime.now(), username, null, null, 0);
            }
            customer.setName(nameField.getText());
            customer.setAddress(addressField.getText());
            customer.setPostalCode(postalCodeField.getText());
            customer.setPhone(phoneField.getText());
            customer.setLastUpdate(ZonedDateTime.now());
            customer.setLastUpdatedBy(username);
            customer.setDivisionId(Integer.parseInt(firstLevelDivisionField.getSelectionModel()
                    .getSelectedItem().substring(0, 3)));

            Stage stage = (Stage)saveButton.getScene().getWindow();
            stage.close();
        } catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to save information!", ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    /**Called when event is created, sets the customer to null and closes the form.
     * 
     * @param event created when Cancel button is clicked
     */
    @FXML
    void cancelButtonListener(ActionEvent event) {
        customer = null;
        Stage stage = (Stage)cancelButton.getScene().getWindow();
        stage.close();
    }
}
