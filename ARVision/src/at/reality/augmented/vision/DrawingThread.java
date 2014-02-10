/**
 * 
 */
package at.reality.augmented.vision;

import at.reality.augmented.vision.CameraSurface;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * this class contains the threading functionality of the application,
 * which primarily are the update and the draw-to-canvas calls
 * 
 * @author Daniel Heger
 */
public class DrawingThread extends Thread
{
	private static final String TAG = UpdateThread.class.getSimpleName();
	
	private SurfaceHolder surfaceHolder; //Holder that accesses physical surface
	private DrawingSurface ds; //View that handles inputs and draws to the drawing surface
	
	private boolean running; // is this Thread running?
	
	// FPS calculations
	private final static int    maxfps = 50;
	private final static int    max_skips = 3;
	private final static int    frameperiod = 1000 / maxfps;
	
	/**
	 * Constructor
	 * 
	 * @param surfaceHolder
	 * @param ds
	 */
	public DrawingThread(SurfaceHolder surfaceHolder, DrawingSurface ds)
	{
		super();
		this.surfaceHolder = surfaceHolder;
		this.ds = ds;
	}
	
	/**
	 * returns if this thread is running
	 * @return true if running, false if not running
	 */
	public boolean getRunning()
	{
		return this.running;
	}
	
	/**
	 * set this thread running
	 * @param running true to start this thread, false to stop it
	 */
	public void setRunning(boolean running)
	{
		this.running = running;
	}
	
	@Override
	public void run()
	{
		// FPS-limiter variables
		long beginTime, timeDiff;
		int sleepTime, framesSkipped;
		
		while (this.running)
		{
			Canvas c = null;
			
			try
			{
				c = this.surfaceHolder.lockCanvas(null);
				synchronized (this.surfaceHolder)
				{
					 beginTime = System.currentTimeMillis();
					 framesSkipped = 0;
					 sleepTime = 0;
					
					//ds.onDraw(c); // draws the frame
					ds.draw(c); // draws the frame
					ds.update(); // updates the surface
					
					/*
					timeDiff = System.currentTimeMillis() - beginTime;
					// calculate sleep time
					sleepTime = (int)(frameperiod - timeDiff);
					if (sleepTime > 0)
					{
						try
						{
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {}
					}
						
					while (sleepTime < 0 && framesSkipped < max_skips)
					{
						this.cs.update();
						sleepTime += frameperiod;
						framesSkipped++;
					}
					*/
					
				}
			}
			finally
			{
				if (c != null)
					this.surfaceHolder.unlockCanvasAndPost(c);
			}
			
		}
		
	}
	
}
