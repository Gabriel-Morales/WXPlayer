package application;
	
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
	
	public static Stage stage;
	public static HashMap <String, String> settings;
	
	@Override
	public void start(Stage primaryStage) {
		
		settings = new HashMap<>();
		stage = primaryStage;

		// Perform one-time setting(s) read/write. All other read writes are 
		// done when the value actually changes in our hashmap.
		File settingsUrl = new File(System.getProperty("user.home") + "/Desktop/WXPlayer_Settings.txt");
		
		if (!settingsUrl.exists())
		{
			try {
				settingsUrl.createNewFile();
				
				FileWriter fW = new FileWriter(settingsUrl);
				fW.write(String.format("%s%n", "save_time=false"));
				fW.write(String.format("%s%n", "skip_menu=false"));
				fW.write(String.format("%s%n", "time=unset"));
				fW.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		else
		{
			try
			{
				Scanner scan = new Scanner(settingsUrl);
				while (scan.hasNextLine())
				{
					String line = scan.nextLine();
					String[] settingArray = line.split("=");
					
					settings.put(settingArray[0], settingArray[1]);
				}
				scan.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
		try {
			
			Parent root = FXMLLoader.load(getClass().getResource("view/Main.fxml"));
		
			primaryStage.setScene(new Scene(root, 600, 600));

			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
