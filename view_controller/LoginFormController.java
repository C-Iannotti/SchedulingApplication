package view_controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.Locale;
import java.sql.*;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.io.File;
import java.io.FileWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * 
 * @author Conner Iannotti
 */
public final class LoginFormController {

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private Label loginFormLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label locationField;

    @FXML
    private Button loginFormSubmitButton;
    
    @FXML
    private Button loginFormExitButton;
    
    private Locale locale = null;
    private MysqlDataSource ds = null;
    private ResourceBundle rb = null;
    private File loginFile = null;
    
    /**Gets database connection, login_activity file, and appropriate resource bundle based on user's locale.
     * Changes form's text to appropriate language based on the resource bundle.
     * 
     */
    public void initialize() {
        locale = Locale.getDefault();
        loginFile = new File("login_activity.txt");
        
        ds = new MysqlDataSource();
        ds.setServerName("wgudb.ucertify.com");
        ds.setPort(3306);
        ds.setDatabaseName("WJ07vft");
        ds.setUser("U07vft");
        ds.setPassword("53689144689");
        
        rb = ResourceBundle.getBundle("resources.Login", locale);
        loginFormLabel.setText(rb.getString("loginForm"));
        usernameLabel.setText(rb.getString("username"));
        passwordLabel.setText(rb.getString("password"));
        locationLabel.setText(rb.getString("location"));
        locationField.setText(locale.getDisplayCountry() + " "
                + ZoneId.systemDefault().getDisplayName(TextStyle.SHORT, locale));
        loginFormSubmitButton.setText(rb.getString("submit"));
        loginFormExitButton.setText(rb.getString("exit"));
    }
    
    /**Called when event is created, checks entered to username and password for a matching user
     * for the database. Opens application if successful, alerts user of incorrect username or
     * password if not. Records each login attempt in a file called login_activity with username
     * used, date of attempt, and time of attempt in UTC.
     * 
     * @param event created when Login Form's submit button is clicked
     * @throws Exception 
     */
    @FXML
    void loginFormSubmitButtonListener(ActionEvent event) throws Exception {
        try (Connection conn = ds.getConnection()) {
            boolean success = false;
            String getLogins = "Select User_Name, Password From users";
            
            try (PreparedStatement ps = conn.prepareStatement(getLogins)) {
                ResultSet rs = ps.executeQuery();
                boolean foundUser = false;
                String username = null;
                
                while(rs.next()) {
                    if (usernameField.getText().equals(rs.getString(1)) &&
                            passwordField.getText().equals(rs.getString(2))) {
                        foundUser = true;
                        username = rs.getString(1);
                        break;
                    }
                }
                
                if (foundUser) {
                    FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("CustomersAndAppointmentsForm.fxml"));
            
                    Parent parent = loader.load();
                    CustomersAndAppointmentsFormController cusAndAppController = loader.getController();
            
                    cusAndAppController.setFields(conn, username);
            
                    //Created new stage.
                    Stage stage = new Stage();
            
                    // Build the scene graph.
                    Scene scene = new Scene(parent);
 
                    // Display our window, using the scene graph.
                    stage.setTitle("Customers and Appointments");
                    stage.setScene(scene);
            
                    stage.showAndWait();
                    success = true;
                }
                else {
                    Alert alert = new Alert(AlertType.NONE, rb.getString("invalidLoginMessage"), ButtonType.OK);
                    alert.showAndWait();
                }
                
                loginFile.createNewFile();
                try(FileWriter writer = new FileWriter(loginFile, true)) {
                    if(success) {
                        writer.write(String.format("Username: %s Date: %s Time: %s (Successful)\n", usernameField.getText(),
                                ZonedDateTime.now(ZoneId.of("Z")).toLocalDate().toString(),
                                ZonedDateTime.now(ZoneId.of("Z")).toLocalTime().toString()));
                    }
                    else {
                        writer.write(String.format("Username: %s Date: %s Time: %s (Unsuccessful)\n", usernameField.getText(),
                                ZonedDateTime.now(ZoneId.of("Z")).toLocalDate().toString(),
                                ZonedDateTime.now(ZoneId.of("Z")).toLocalTime().toString()));
                    }
                } catch(Exception e) {
                    System.out.println("Unable to write login attempt.");
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.NONE, rb.getString("invalidConnectionMessage"), ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    /**Called when event is created, confirms with the user if they wish to exit the application and exits
     * the application if yes.
     * 
     * @param event created when Login Form's exit button is clicked
     */
    @FXML
    void loginFormExitButtonListener(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION, rb.getString("exitMessage"), ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        
        if(alert.getResult() == ButtonType.YES) {
            Stage stage = (Stage)loginFormExitButton.getScene().getWindow();
            stage.close();
        }
    }
}

