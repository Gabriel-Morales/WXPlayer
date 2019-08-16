package application.controller;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;

import application.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class MainController implements EventHandler<ActionEvent>, Initializable {

	@FXML private JFXButton CDButton;
	@FXML private Label CDLabel;
	@FXML private JFXButton playButton;
	@FXML private Button CDGlyph;
	@FXML private JFXToggleButton saveToggle;
	@FXML private JFXToggleButton skipToggle;
	@FXML private Button mediaGlyph;
	@FXML private Label mediaLabel;
	@FXML private JFXButton mediaPlay;
	
	@Override
	public void handle (ActionEvent arg0) {
		
		DirectoryChooser dc = new DirectoryChooser();
		File dir = dc.showDialog(new Stage());
		
		if (dir == null)
		{
			return;
		}
		
	
		playButton.setDisable(false);
		
		CDLabel.setText(dir.getAbsolutePath());

		VideoController.setFileUrl(dir.getAbsolutePath());
		VideoController.mediaType = "dvd";
	
		
	}
	

	public void handleOther (ActionEvent arg0) {
		
		FileChooser dc = new FileChooser();
		File dir = dc.showOpenDialog(new Stage());
		
		if (dir == null)
		{
			return;
		}
		

		mediaPlay.setDisable(false);
		
		mediaLabel.setText(dir.getAbsolutePath());

		VideoController.setFileUrl(dir.getAbsolutePath());
		VideoController.mediaType = "other";
	
		
	}
	
	
	public void pushToController()
	{
		try {
			
			Parent root = FXMLLoader.load(getClass().getResource("../view/VideoView.fxml"));
			Main.stage.setScene(new Scene(root, 600, 600));
			Main.stage.show();
					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				

	}
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		String save_time = Main.settings.get("save_time");
		String skip_menu = Main.settings.get("skip_menu");
		
		if (Boolean.valueOf(save_time) == true)
		{
		
			saveToggle.setSelected(true);
			
		}
		else
		{
		
			saveToggle.setSelected(false);
		}
		
		if (Boolean.valueOf(skip_menu) == true)
		{
			skipToggle.setSelected(true);
		}
		else
		{
			skipToggle.setSelected(false);
		}
		
		final String settingsURL = System.getProperty("user.home") + "/Desktop/WXPlayer_Settings.txt";
		
		skipToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				
				Main.settings.put("skip_menu", String.valueOf(arg2));
				
				//Very minimal write. Put on a separate thread to not halt program.
				
				ExecutorService e = Executors.newCachedThreadPool();
				e.execute(new Runnable() {

					@Override
					public void run() {

						try
						{
							
							FileWriter fW = new FileWriter(new File(settingsURL));
							fW.write(String.format("%s%n", "skip_menu=" + Main.settings.get("skip_menu")));
							fW.write(String.format("%s%n", "save_time=" + Main.settings.get("save_time")));
							fW.write(String.format("%s%n", "time=" + Main.settings.get("time")));
							fW.close();
							
						} catch (IOException e) {
							
							e.printStackTrace();
							
						}
						
					}
					
				});
				
			}
			
		});
		
		saveToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				
				Main.settings.put("save_time", String.valueOf(arg2));
				
				if (arg2 == false)
				{
					Main.settings.put("time", "unset");
				}
				else
				{
					Main.settings.put("time", "0");
				}
				
				//Very minimal write. Put on other thread to not hault program.

				ExecutorService e = Executors.newCachedThreadPool();
				e.execute(new Runnable() {

					@Override
					public void run() {

						try
						{
							
							FileWriter fW = new FileWriter(new File(settingsURL));
							fW.write(String.format("%s%n", "skip_menu=" + Main.settings.get("skip_menu")));
							fW.write(String.format("%s%n", "save_time=" + Main.settings.get("save_time")));
							fW.write(String.format("%s%n", "time=" + Main.settings.get("time")));
							fW.close();
							
						} catch (IOException e) {
							
							e.printStackTrace();
							
						}
						
					}
					
				});
				
			}
			
		});
		
		
		Main.stage.setMaxHeight(600);
		Main.stage.setMaxWidth(600);
		CDLabel.setText("None selected.");
		mediaLabel.setText("None selected.");
		playButton.setDisable(true);
		mediaPlay.setDisable(true);
		
		SVGPath CD = new SVGPath();
		CD.setContent("M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 14.5c-2.49 0-4.5-2.01-4.5-4.5S9.51 7.5 12 7.5s4.5 2.01 4.5 4.5-2.01 4.5-4.5 4.5zm0-5.5c-.55 0-1 .45-1 1s.45 1 1 1 1-.45 1-1-.45-1-1-1z");
		CD.setScaleX(2.0);
		CD.setScaleY(2.0);
		
		SVGPath media = new SVGPath();
		media.setContent("M18 4l2 4h-3l-2-4h-2l2 4h-3l-2-4H8l2 4H7L5 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V4h-4z");
		media.setScaleX(2.0);
		media.setScaleY(2.0);
		mediaGlyph.setGraphic(media);
		
		CDGlyph.setGraphic(CD);
		
		saveToggle.setDisableVisualFocus(true);
		skipToggle.setDisableVisualFocus(true);

	}

}
