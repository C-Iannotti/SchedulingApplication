package schedulingapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**Starts the scheduling application
 *
 * @author Conner Iannotti
 */
public class SchedulingApplication extends Application{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
     /**Starts the GUI of the program
     * 
     * @param stage where the main form of the application is stored and shown
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load(
        getClass().getResource("/view_controller/LoginForm.fxml"));
 
        Scene scene = new Scene(parent);
 
        stage.setTitle("Scheduling Application");
        stage.setScene(scene);
        stage.show();

    }
    
}
