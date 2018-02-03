package com.pescaworks.ned.expenses;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.pescaworks.ned.expenses.controller.MainController;
import com.pescaworks.ned.expenses.model.Product;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/Main.fxml"));

			// Perform loader.laod to get controller
			SplitPane rootLayout = (SplitPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			
			primaryStage.setScene(scene);
			primaryStage.show();
			
			// Set min and max width after stage.show (else scene.get returns null)
			primaryStage.setMinWidth(scene.getWidth());
			primaryStage.setMinHeight(scene.getHeight());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}
