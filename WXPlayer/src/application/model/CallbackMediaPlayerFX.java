package application.model;

import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

import com.jfoenix.controls.JFXSlider;

import application.Main;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Affine;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat;

public class CallbackMediaPlayerFX {
	
	private CallbackVideoSurface cvs; 
	private Scene scene;
	
	private WritableImage img;
	private PixelWriter pixI;
	private WritablePixelFormat<ByteBuffer> pixelFormat;
	private Canvas renderedCanvas;
	private AspectRatio aspect;
	private double ratio;
	
	private BorderPane currPane;
	private JFXSlider scrubber;
	
	private double originalH;
	private double originalW;
	
	/* Normal constructor. Some of the code is supplied by Caprica on Github. 
	 * This simply allows the CallbackMediaPlayer to be setup. For this 
	 * project, however, I modified everything by Caprica to be within this class alone.
	 * */
	public CallbackMediaPlayerFX(Scene scene)
	{
		
		renderedCanvas = new Canvas(scene.getWidth(), scene.getHeight());	
		
		
		pixI = renderedCanvas.getGraphicsContext2D().getPixelWriter();
		pixelFormat = PixelFormat.getByteBgraInstance();
		
		this.scene = scene;
		
		
		cvs = new CallbackVideoSurface(createBufferedFormat(this.scene), 
				createRenderedCallback(), true, VideoSurfaceAdapters.getVideoSurfaceAdapter());
		
		
	}
	
	/* My guess is that this sets up the canvas and all the elements to create a buffer to be used 
	 * internally along with the rendering. Naturally, we use our PixelWriter and WritableImage here. */
	private BufferFormatCallback createBufferedFormat(Scene scene)
	{
		/* Buffer format internal code courtesy of caprica on Github */
		BufferFormatCallback bfc = new BufferFormatCallback ()
		{

			@Override
			public BufferFormat getBufferFormat(int arg0, int arg1) {
				
				img = new WritableImage(arg0, arg1);
				pixI = img.getPixelWriter();
				
				Platform.runLater(new Runnable()
				{

					@Override
					public void run() {
						
						Main.stage.setHeight(arg1);
						Main.stage.setWidth(arg0);
						
						Main.stage.setMinHeight(arg1);
						Main.stage.setMinWidth(arg0);
						
						AspectRatio a = new AspectRatio(arg0, arg1);
						setAspectRatio(a);
						setAspectDimensions(a.getNumerator(), a.getDenominator());
						
						originalH = arg1;
						originalW = arg0;
						
						currPane.setPrefHeight(arg1);
						currPane.setPrefWidth(arg0);
						
						getRenderedCanvas().setHeight(arg1);
						getRenderedCanvas().setWidth(arg0);
						
						
					}
					
					
				});
				
				
				
				return new RV32BufferFormat(arg0, arg1);

			}
			
		};
		
		
		return bfc;
		
	}
	
	/*  This function creates our renderCallback to be called internally by the API. Purpose of this
	 *  is to render the media onto the Canvas. As can be seen, we do so by setting the pixels and then
	 *  calling the renderFrame() function. */
	private RenderCallback createRenderedCallback()
	{
		/* Rendering internal code courtesy of caprica on Github */
		RenderCallback rcb = new RenderCallback ()
		{

			private Semaphore semaphore = new Semaphore(1);
			@Override
			public void display(MediaPlayer arg0, ByteBuffer[] arg1, BufferFormat arg2) {
				// Semaphore prevents multiple threads editing same object.
				try {

					semaphore.acquire();
					pixI.setPixels(0, 0, arg2.getWidth(), arg2.getHeight(), pixelFormat, arg1[0], arg2.getPitches()[0]);
					semaphore.release();
					
					Platform.runLater(new Runnable()
					{
						@Override
						public void run() {
							
							renderFrame();
						}		
	
					});
					
					
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
				
				
			}
		
		};
		
		
		
		return rcb;
		
	}
	
	/* Returns the CallbackVideoSurface to set in the video controller. */
	public CallbackVideoSurface getVideoSurface()
	{
		
		return cvs;
	}
	
	/* Returns our rendered canvas (initial) to the initializer method. */
	public Canvas getRenderedCanvas()
	{
		return renderedCanvas;
	}

	/* Below code courtesy of caprica on Github. This function is called every time
	 * the "display" method above is called. It's set on the JavaFX thread to update each time.
	 *  
	 *  This is my attempt at an explanation:
	 *  
	 *  We get the canvas that's been placed on the scene, then get the 2D context (assumedly to draw on?) 
	 *  We create a semaphore (this has to do with locking the object and disallowing multiple things to 
	 *  touch it while it's being updated on the thread i.e. I don't know anything about semaphores.)
	 *  
	 *  Since the canvas updates along with the stage, we grab it's height and width. Likewise,
	 *  we grab the image being drawn on the canvas' height and width. 
	 *  
	 *  Next we use an affine which is, from what I read, our way to save the current transformation.
	 *  We scale the image and lastly replace the transformation that we saved earlier.
	 *  
	 *  */
	private void renderFrame()
	{
			
		
		
		Canvas temp = renderedCanvas;


		GraphicsContext gc = temp.getGraphicsContext2D();

		Semaphore semaphore = new Semaphore(1);
		double width = temp.getWidth();
        double height = temp.getHeight();


        if (img != null) {
        	
            double imageWidth = img.getWidth();
            double imageHeight = img.getHeight();
            
            
            scrubber.setPrefWidth(Main.stage.getWidth() - 138);
            scrubber.setMaxWidth(Main.stage.getWidth() - 138);
            
           
            renderedCanvas.setHeight(Main.stage.getHeight());
            renderedCanvas.setWidth(Main.stage.getWidth());

           
            
            if (ratio == (originalW / originalH))
            {
            	setOriginalDimensions();
            }
            else
            {
            	
            	// SET THE CANVAS IN THE CENTER
            	double xCenter = (Main.stage.getWidth() / 2.0);
            	
            	renderedCanvas.setWidth(originalW * ratio);
            	double videoCenter = renderedCanvas.getWidth() / 2.0;
            	
            	renderedCanvas.setLayoutX(xCenter - videoCenter);
            	
            	if (!Main.stage.isMaximized())
            	{
            		Main.stage.setWidth(renderedCanvas.getWidth());
            	}
            	
            }
            
            
    		
            double sx = width / imageWidth;
            double sy = height / imageHeight;
            
            Affine ax = gc.getTransform();
            
            gc.scale(sx, sy);

            try {
                semaphore.acquire();
                gc.drawImage(img, 0, 0);
                semaphore.release();
            }
            catch (InterruptedException e) {
            }

            gc.setTransform(ax);
            
        }
		
		
		
		
		
	}
	
	private void setOriginalDimensions()
	{
		if (Main.stage.getWidth() != Main.stage.getMinWidth() && Main.stage.getHeight() == Main.stage.getMinHeight())
        {
        	renderedCanvas.setWidth(Main.stage.getMinWidth());
        	
        	double centerX = (Main.stage.getWidth() / 2.0);
        	double videoCenter = (renderedCanvas.getWidth() / 2.0);

        	renderedCanvas.setLayoutX(centerX - videoCenter); 
        	renderedCanvas.setLayoutY(0.0); 
        }
        else if (Main.stage.getHeight() != Main.stage.getMinHeight() && Main.stage.getWidth() == Main.stage.getMinWidth())
        {
        	renderedCanvas.setHeight(Main.stage.getMinHeight());
        	
        	double centerY = (Main.stage.getHeight() / 2.0);
        	double videoCenter = (renderedCanvas.getHeight() / 2.0);

        	renderedCanvas.setLayoutY(centerY - videoCenter); 
        	renderedCanvas.setLayoutX(0.0); 
        	
        }
        else if (Main.stage.getHeight() == Main.stage.getMinHeight() && Main.stage.getWidth() == Main.stage.getMinWidth())
        {
        	renderedCanvas.setLayoutY(0.0); 
        	renderedCanvas.setLayoutX(0.0); 
        }
        else
        {
        	renderedCanvas.setLayoutY(0.0); 
        	renderedCanvas.setLayoutX(0.0); 
        }
	}
	
	public void setCurrPane(BorderPane currPane)
	{
		this.currPane = currPane;
	}
	
	
	public void setScrubber(JFXSlider slider)
	{
		scrubber = slider;
	}
	
	public void setAspectDimensions(double num, double den)
	{
		this.ratio = num / den;
	}
	
	private void setAspectRatio(AspectRatio aspect)
	{
		this.aspect = aspect;
	}
	
	public AspectRatio getAspectRatio()
	{
		return this.aspect;
	}
	
}
