package view;

import commands.DisconnectCommand;
import interpreter.AutoPilotParser;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Model;
import viewmodel.ViewModel;

import javax.swing.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Flight.fxml"));
            Parent parent = fxmlLoader.load();
            FlightController controller = fxmlLoader.getController();

            ViewModel viewModel=new ViewModel();
            Model model=new Model();
            model.addObserver(viewModel);
            viewModel.setModel(model);
            viewModel.addObserver(controller);
            controller.setViewModel(viewModel);

            primaryStage.setTitle("Flight Gear Simulator");
            primaryStage.setScene(new Scene(parent));
            primaryStage.show();
            primaryStage.setOnCloseRequest(event -> {
                DisconnectCommand command=new DisconnectCommand();
                String[] disconnect={""};
                command.executeCommand(disconnect);
                AutoPilotParser.calc_thread.interrupt();
                model.stopAll();
                System.out.println("bye");
            });
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
