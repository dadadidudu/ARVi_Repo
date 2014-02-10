package at.reality.augmented.vision;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawingSurface extends SurfaceView //implements SurfaceHolder.Callback
{
	private static final String TAG = CameraSurface.class.getSimpleName();// DEBUG-Message
	
	private Paint paint = new Paint();
	private SurfaceHolder holder;
	private Context context;
	//private DrawingThread th;
	
	public DrawingSurface(Context context)
	{
		super(context);
		this.context = context;
		//holder = getHolder();
		//holder.addCallback(this);
		// Create out paint to use for drawing
		paint.setARGB(255, 200, 0, 0);
		paint.setTextSize(60);
		// This call is necessary, or else the
		// draw method will not be called.
		setWillNotDraw(false);
	}

	/* threading hier funzt leider ned... (uncomment SurfaceHolder.Callback & addCallback(this))
	
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder)
		{
			// experimentelle Unterstuetzung fuer Threading
            //th = new DrawingThread(holder, this);
            //th.start();
            //Log.d(TAG, "Drawing-Thread (re)started");
			
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder)
		{
			// shutdown thread
			boolean retry = true;
			while (retry)
			{
				try
				{
					//th.setRunning(false);
					//th.join();
					retry = false;
					//((Activity)getContext()).finish();
					//Log.d(TAG, "Drawing-Thread shut down, Activity finished");
				}
				catch (Exception e)
				{
					e.getMessage();
					e.getStackTrace();
				}
			}
			
		}
	*/

		protected void onDraw(Canvas canvas)
		 {
			 //if (th.getRunning() == true)
				{
				 	// A Simple Text Render to test the display
				 	canvas.drawText("Hello World!", 400, 200, paint);
					
					canvas.drawText("abcdef", 200, 200, paint);
					
					Log.d(TAG, "executed");
				}
		 }
		
		public void update()
		{
			// TODO Auto-generated method stub
			
		}
		

}
