package application.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSlider;

import application.Main;
import application.model.CallbackMediaPlayerFX;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.media.MediaRef;
import uk.co.caprica.vlcj.media.TrackType;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;



public class VideoController implements Initializable {

	
	
	private static String fileUrl;
	public static String mediaType;
	
	private static MediaPlayerFactory mpf;
	private static EmbeddedMediaPlayer emp;
	private static CallbackMediaPlayerFX cmpfx;
	
	@FXML private BorderPane borderPane;
	@FXML private Label progressLabel;
	@FXML private JFXSlider progressSlider;
	@FXML private JFXButton playButton;
	@FXML private GridPane scrubberGrid;
	@FXML private HBox ctrlBox;
	@FXML private JFXButton stopButton;
	@FXML private JFXButton forTen;
	@FXML private JFXButton backTen;
	@FXML private JFXButton aspectButton;
	@FXML private JFXButton menuButton;


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		Main.stage.setResizable(true);
		
		String vlcArgs = "";
		
		if (mediaType != null && mediaType.equals("dvd"))
		{
			vlcArgs = "dvd://";
		}
		
		if (mediaType != null && mediaType.equals("other"))
		{
			vlcArgs = "file://";
		}
		
		
		mpf = new MediaPlayerFactory(vlcArgs);
		emp = mpf.mediaPlayers().newEmbeddedMediaPlayer();
		cmpfx = new CallbackMediaPlayerFX(Main.stage.getScene());

		
		
		
		playButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				
				emp.controls().pause();
				
				if (!emp.status().isPlaying() && Boolean.valueOf(Main.settings.get("save_time")) == true)
				{
						
					Main.settings.put("time", String.valueOf(emp.status().time()));
					try {
							FileWriter fW = new FileWriter(new File(System.getProperty("user.home") + "/Desktop/WXPlayer_Settings.txt"));
							fW.write(String.format("%s%n", "skip_menu=" + Main.settings.get("skip_menu")));
							fW.write(String.format("%s%n", "save_time=" + Main.settings.get("save_time")));
							fW.write(String.format("%s%n", "time=" + Main.settings.get("time")));
							fW.close();
					} catch (IOException e) {
							
						e.printStackTrace();
					}
		
				}
				
			}
			
		});
		
		
	
		emp.videoSurface().set(cmpfx.getVideoSurface());
		
		emp.media().prepare(fileUrl);
		
		
		SVGPath pausePath = new SVGPath();
		pausePath.setContent("M6 19h4V5H6v14zm8-14v14h4V5h-4z");
		pausePath.setFill(Color.WHITE);
		pausePath.setScaleX(2.5);
		pausePath.setScaleY(2.5);
		
		playButton.setGraphic(pausePath);
		
		
		
		SVGPath stopPath = new SVGPath();
		stopPath.setContent("M0 0h24v24H0z");
		stopPath.setFill(Color.WHITE);
		
		
		stopButton.setGraphic(stopPath);
		
		
		SVGPath backPath = new SVGPath();
		backPath.setContent("M12 5V1L7 6l5 5V7c3.3 0 6 2.7 6 6s-2.7 6-6 6-6-2.7-6-6H4c0 4.4 3.6 8 8 8s8-3.6 8-8-3.6-8-8-8zm-1.1 11H10v-3.3L9 13v-.7l1.8-.6h.1V16zm4.3-1.8c0 .3 0 .6-.1.8l-.3.6s-.3.3-.5.3-.4.1-.6.1-.4 0-.6-.1-.3-.2-.5-.3-.2-.3-.3-.6-.1-.5-.1-.8v-.7c0-.3 0-.6.1-.8l.3-.6s.3-.3.5-.3.4-.1.6-.1.4 0 .6.1c.2.1.3.2.5.3s.2.3.3.6.1.5.1.8v.7zm-.9-.8v-.5s-.1-.2-.1-.3-.1-.1-.2-.2-.2-.1-.3-.1-.2 0-.3.1l-.2.2s-.1.2-.1.3v2s.1.2.1.3.1.1.2.2.2.1.3.1.2 0 .3-.1l.2-.2s.1-.2.1-.3v-1.5z" );
		backPath.setFill(Color.WHITE);
		backPath.setScaleX(2);
		backPath.setScaleY(2);

		
		backTen.setGraphic(backPath);
		
		SVGPath forPath = new SVGPath();
		forPath.setContent("M4 13c0 4.4 3.6 8 8 8s8-3.6 8-8h-2c0 3.3-2.7 6-6 6s-6-2.7-6-6 2.7-6 6-6v4l5-5-5-5v4c-4.4 0-8 3.6-8 8zm6.8 3H10v-3.3L9 13v-.7l1.8-.6h.1V16zm4.3-1.8c0 .3 0 .6-.1.8l-.3.6s-.3.3-.5.3-.4.1-.6.1-.4 0-.6-.1-.3-.2-.5-.3-.2-.3-.3-.6-.1-.5-.1-.8v-.7c0-.3 0-.6.1-.8l.3-.6s.3-.3.5-.3.4-.1.6-.1.4 0 .6.1.3.2.5.3.2.3.3.6.1.5.1.8v.7zm-.8-.8v-.5s-.1-.2-.1-.3-.1-.1-.2-.2-.2-.1-.3-.1-.2 0-.3.1l-.2.2s-.1.2-.1.3v2s.1.2.1.3.1.1.2.2.2.1.3.1.2 0 .3-.1l.2-.2s.1-.2.1-.3v-1.5z" );
		forPath.setFill(Color.WHITE);
		forPath.setScaleX(2);
		forPath.setScaleY(2);
		
		forTen.setGraphic(forPath);
		
		SVGPath aspectPath = new SVGPath();
		aspectPath.setContent("M19 12h-2v3h-3v2h5v-5zM7 9h3V7H5v5h2V9zm14-6H3c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h18c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16.01H3V4.99h18v14.02z");;
		aspectPath.setFill(Color.WHITE);
		aspectPath.setScaleX(2);
		aspectPath.setScaleY(2);
		
		aspectButton.setGraphic(aspectPath);
		
		stopButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
		
				if (Boolean.valueOf(Main.settings.get("save_time")) == true)
				{
					ExecutorService e = Executors.newCachedThreadPool();
					final long saveTime = emp.status().time();
					e.execute(new Runnable() {
					
						@Override
						public void run() {
							Main.settings.put("time", String.valueOf(saveTime));
							try {
								FileWriter fW = new FileWriter(new File(System.getProperty("user.home") + "/Desktop/WXPlayer_Settings.txt"));
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
				
				emp.controls().stop();
				Main.stage.setMinHeight(600);
				Main.stage.setMinWidth(600);
				Main.stage.setHeight(600);
				Main.stage.setWidth(600);
				try {
					Parent root = FXMLLoader.load(getClass().getResource("../view/Main.fxml"));
					
					Main.stage.setScene(new Scene(root, 600, 600));
					Main.stage.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		Canvas renderedCanvas = cmpfx.getRenderedCanvas();	
		borderPane.getChildren().add(renderedCanvas);
		renderedCanvas.toBack();
		
		cmpfx.setCurrPane(borderPane);		
		cmpfx.setScrubber(progressSlider);
		
		progressSlider.setFocusTraversable(false);
		
		progressSlider.getStylesheets().clear();
		progressSlider.getStylesheets().add(getClass().getResource("../../Resources/jfx-slider-alt.css").toExternalForm());
		
		
		
		progressSlider.addEventHandler(MouseEvent.MOUSE_PRESSED, scrubberEventPress());
		
		progressSlider.addEventHandler(MouseEvent.MOUSE_DRAGGED, scrubberEventDrag());
		
		progressSlider.addEventHandler(MouseEvent.MOUSE_RELEASED, scrubberEventReleased());
		
		backTen.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				
				emp.controls().setTime(emp.status().time() - 10000);
				
			}
			
		});
		
		forTen.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				
				emp.controls().setTime(emp.status().time() + 10000);
				
			}
			
		});
		
		
		
		borderPane.addEventHandler(KeyEvent.KEY_PRESSED, menuEvents());
		
		emp.events().addMediaPlayerEventListener(new MediaPlayerEventListener() {

			@Override
			public void audioDeviceChanged(MediaPlayer arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void backward(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void buffering(MediaPlayer arg0, float arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void chapterChanged(MediaPlayer arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void corked(MediaPlayer arg0, boolean arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void elementaryStreamAdded(MediaPlayer arg0, TrackType arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void elementaryStreamDeleted(MediaPlayer arg0, TrackType arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void elementaryStreamSelected(MediaPlayer arg0, TrackType arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void error(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void finished(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						emp.release();
					}
					
				});
			}

			@Override
			public void forward(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void lengthChanged(MediaPlayer arg0, long arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mediaChanged(MediaPlayer arg0, MediaRef arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mediaPlayerReady(MediaPlayer arg0) {
				// TODO Auto-generated method stub

				
			}

			@Override
			public void muted(MediaPlayer arg0, boolean arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void opening(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				
				if (mediaType.equals("dvd"))
				{
					stopButton.setVisible(false);
					scrubberGrid.setVisible(false);
					ctrlBox.setVisible(false);
					
					aspectButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent arg0) {
						
						
								// TODO Auto-generated method stub
								JFXDialog dialog = new JFXDialog();
								JFXDialogLayout layout = new JFXDialogLayout();
								aspectButton.setDisable(true);
								dialog.setOnDialogClosed((a) -> aspectButton.setDisable(false));
								
								Label header = new Label("SELECT ASPECT RATIO");
								header.setFont(new Font("open-sans", 17));
								header.setStyle("-fx-text-fill: #0D47A1");
								
								VBox container = new VBox();
								JFXButton asD = new JFXButton("Default (" + cmpfx.getAspectRatio().getNumerator() + ":" + cmpfx.getAspectRatio().getDenominator() + ")");
								asD.setOnAction((a) -> cmpfx.setAspectDimensions(cmpfx.getAspectRatio().getNumerator(), cmpfx.getAspectRatio().getDenominator()));
								JFXButton as1 = new JFXButton("16:9");
								as1.setOnAction((a) -> cmpfx.setAspectDimensions(16, 9));
								JFXButton as2 = new JFXButton("5:4");
								as2.setOnAction((a) -> cmpfx.setAspectDimensions(5, 4));		
								JFXButton as3 = new JFXButton("4:3");
								as3.setOnAction((a) -> cmpfx.setAspectDimensions(4, 3));
								JFXButton as4 = new JFXButton("1:1");
								as4.setOnAction((a) -> cmpfx.setAspectDimensions(1, 1));
							
							
								container.getChildren().add(asD);
								container.getChildren().add(as1);
								container.getChildren().add(as2);
								container.getChildren().add(as3);
								container.getChildren().add(as4);
							
								layout.setHeading(header);
								layout.setBody(container);
								
								dialog.setContent(layout);
								

								dialog.show((StackPane) borderPane.getCenter());
							
						
						}
					
					
					});
				}
				
			}

			@Override
			public void pausableChanged(MediaPlayer arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void paused(MediaPlayer arg0) {
				
				// TODO BUTTON ICON PLAY
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						
						
						// TODO Auto-generated method stub
						SVGPath playPath = new SVGPath();
						playPath.setContent("M8 5v14l11-7z");
						
						playPath.setFill(Color.WHITE);
						playPath.setScaleX(2.5);
						playPath.setScaleY(2.5);
						
						playButton.setGraphic(playPath);
					}
					
				});
				
			}

			@Override
			public void playing(MediaPlayer arg0) {
	
				// TODO BUTTON ICON PAUSE
				Platform.runLater(new Runnable() {

					
					
					@Override
					public void run() {
						
						// TODO Auto-generated method stub
						SVGPath pausePath = new SVGPath();
						pausePath.setContent("M6 19h4V5H6v14zm8-14v14h4V5h-4z");
						
						pausePath.setFill(Color.WHITE);
						pausePath.setScaleX(2.5);
						pausePath.setScaleY(2.5);
						
						playButton.setGraphic(pausePath);
						
						
						
						
						
						
						
					}
					
				});
			}

			@Override
			public void positionChanged(MediaPlayer arg0, float arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void scrambledChanged(MediaPlayer arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void seekableChanged(MediaPlayer arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void snapshotTaken(MediaPlayer arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void stopped(MediaPlayer arg0) {
				
				
				
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						
						emp.release();
					}
					
				});
				
			}

			@Override
			public void timeChanged(MediaPlayer arg0, long arg1) {
				
					
				//Update the timestamp of the movie.
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						
						progressSlider.setFocusTraversable(false);

						
						long totalT = emp.status().length();
						
						progressSlider.setMax(totalT);
						
						
						
						long totalM = (totalT / 1000) / 60;
						long totalS = (totalT / 1000) % 60;
						
						
						
						String tLabel = formatTime(totalM, totalS);
						
						
						
						
						long currT = arg1;
						
						long currM = (currT / 1000) / 60;
						long currS = (currT / 1000) % 60;
						
						
						progressSlider.adjustValue(currT);
						
						String cLabel = formatTime(currM, currS);
						
						
						progressLabel.setText(cLabel + "/" + tLabel);
						progressSlider.setValueFactory(slider ->
			      		Bindings.createStringBinding(
			      			() -> (cLabel),
			      			slider.valueProperty()
			      		)
						);
						
						
						
					}
					
				});
				
				
				
				
				
				
			}

			@Override
			public void titleChanged(MediaPlayer arg0, int arg1) {
				// TODO Auto-generated method stub
				
				if (arg0.titles().titleDescriptions() != null)
				{
					
					stopButton.setVisible(true);
					menuButton.setVisible(false);
					if (arg0.titles().titleDescriptions().get(arg0.titles().title()).isMenu())
					{
					
						final int menu = arg0.titles().title();
						menuButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

							@Override
							public void handle(MouseEvent arg0) {
								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										
										emp.titles().setTitle(menu);
										emp.controls().setTime(0);
										
									}
									
								});
								
							}
							
						});
						scrubberGrid.setDisable(true);
						ctrlBox.setDisable(true);
						scrubberGrid.setVisible(false);
						ctrlBox.setVisible(false);
						
						// JFoenix needs to lose the window focus 
						progressSlider.setFocusTraversable(false);
						scrubberGrid.setFocusTraversable(false);
						ctrlBox.setFocusTraversable(false);
						renderedCanvas.setFocusTraversable(true);
						
						if (Boolean.valueOf(Main.settings.get("save_time")) == true || Boolean.valueOf(Main.settings.get("skip_menu")) == true)
						{
							emp.titles().setTitle(emp.titles().title() + 1);
						}
						
					}
					else
					{
						
						//Simple workaround for possible back to menu bug if
						//skip menu option is enabled.
						if (Boolean.valueOf(Main.settings.get("skip_menu")))
						{
							menuButton.setVisible(true);
						}
					
						scrubberGrid.setDisable(false);
						ctrlBox.setDisable(false);
						scrubberGrid.setVisible(true);
						ctrlBox.setVisible(true);
						
						
					}
				}
			}

			@Override
			public void videoOutput(MediaPlayer arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void volumeChanged(MediaPlayer arg0, float arg1) {
				// TODO Auto-generated method stub
				
			}
			
			
		});
		
		
		borderPane.addEventHandler(KeyEvent.KEY_PRESSED, spaceBarPressed());
		
		
		emp.controls().play();
		
		borderPane.setFocusTraversable(true);
		borderPane.requestFocus();
		
	}
	
	protected static void setFileUrl (String fileUrlIn)
	{
		if (fileUrlIn == null)
			return;
		
		fileUrl = fileUrlIn;
		
		
	}
	
	
	private EventHandler<KeyEvent> menuEvents ()
	{
		
		EventHandler<KeyEvent> myEv = new EventHandler<KeyEvent>()
		{

			@Override
			public void handle(KeyEvent event) {
				
				
				if (event.getCode().equals(KeyCode.RIGHT))
				{
					emp.menu().right();
				}
				
				if (event.getCode().equals(KeyCode.LEFT))
				{
					emp.menu().left();
				}
				
				if (event.getCode().equals(KeyCode.UP))
				{
					emp.menu().up();
				}
				
				if (event.getCode().equals(KeyCode.DOWN))
				{
					emp.menu().down();
				}
				
				
				if (event.getCode().equals(KeyCode.ENTER))
				{
					emp.menu().activate();
				}
				

			}

		};
		
		return myEv;
		
	}
	
	private EventHandler<KeyEvent> spaceBarPressed()
	{
		
		playButton.setFocusTraversable(false);
		backTen.setFocusTraversable(false);
		forTen.setFocusTraversable(false);
		aspectButton.setFocusTraversable(false);
		stopButton.setFocusTraversable(false);
		progressSlider.setFocusTraversable(false);
		
		EventHandler<KeyEvent> myEv = new EventHandler<KeyEvent>()
		{

			@Override
			public void handle(KeyEvent event) {
				
				if (event.getCode().equals(KeyCode.SPACE))
				{
					
					emp.controls().pause();
				}
			}
			
		};
		
		return myEv;
	}

	
	private EventHandler<MouseEvent> scrubberEventPress()
	{
		EventHandler<MouseEvent> myEv =  new EventHandler<MouseEvent>()
		{

				@Override
				public void handle(MouseEvent event) {

					emp.controls().pause();
				
				}

		};
		
		return myEv;
	}
	
	private EventHandler<MouseEvent> scrubberEventDrag()
	{
		EventHandler<MouseEvent> myEv = new EventHandler<MouseEvent>()
		{

			@Override
			public void handle(MouseEvent event) {
				
				
				long currT = (long) progressSlider.getValue();
						
				long currM = (currT / 1000) / 60;
				long currS = (currT / 1000) % 60;
						
				String str = formatTime(currM, currS);
						
				progressSlider.setValueFactory(slider ->
			    Bindings.createStringBinding(
			      	() -> (str),
			      	slider.valueProperty()
			      ));
						
						
			}
							
		};
		
		return myEv;
	}
	
	
	private EventHandler<MouseEvent> scrubberEventReleased()
	{
		
		EventHandler<MouseEvent> myEv = new EventHandler<MouseEvent>()
		{

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				emp.controls().setTime((long) progressSlider.getValue());
				
				emp.controls().play();
				
			}
			
		};
				
		return myEv;
		
	}
	
	private String formatTime(long minutes, long seconds)
	{
		
		long hours = 0;
		while (minutes >= 60)
		{
			hours += 1;
			minutes -= 60;
		}
		
		while (seconds >= 60)
		{
			minutes += 1;
			hours -= 60;
		}
		
		
		
		String csLabel = "";
		if (seconds < 10)
		{
			csLabel = "0" + String.valueOf(seconds);
		}
		else
		{
			csLabel = String.valueOf(seconds);
		}
		
		String cmLabel = "";
		if (minutes < 10)
		{
			cmLabel = "0" + String.valueOf(minutes);
		}
		else
		{
			cmLabel = String.valueOf(minutes);
		}
		
		return String.format("%d:%s:%s", hours, cmLabel, csLabel);
			
	}
	

}
