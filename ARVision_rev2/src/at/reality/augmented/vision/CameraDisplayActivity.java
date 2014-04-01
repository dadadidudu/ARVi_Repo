package at.reality.augmented.vision;

import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class CameraDisplayActivity extends Activity {

	private static final String TAG = CameraDisplayActivity.class.getSimpleName();// DEBUG-Message
	public static Activity act;
//	private static Camera cam;
	
	private CameraSurface cameraDisplaySurface;
	private UiSurface uiDisplaySurface;
	
	private FrameLayout frameHolder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		act = this;
		
		frameHolder = new FrameLayout(this);
		
		// force total fullscreen and keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();

		frameHolder = new FrameLayout(this);
		frameHolder.setLayoutParams(new LayoutParams(
		           LayoutParams.MATCH_PARENT,
		           LayoutParams.MATCH_PARENT));
		
		// create SurfaceView for the camera preview and set it visible
		cameraDisplaySurface = new CameraSurface(this);
		frameHolder.addView(cameraDisplaySurface);
		// same for the UiSurface
//		uiDisplaySurface = new UiSurface(this);
//		frameHolder.addView(uiDisplaySurface);
		
		
		// and show everything
		setContentView(frameHolder);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		cameraDisplaySurface = null;
//		uiDisplaySurface = null;
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
//	/**
//	 * returns an instance of the camera. Use only if the camera is not in use,
//	 * else an error-entry to the Log will be made.
//	 * @return an instance of the camera
//	 */
//	private static synchronized final Camera getCameraInstance()
//	{
//		cam = null;
//	    try
//	    {
//	        cam = Camera.open(); // attempt to get a Camera instance
//	    }
//	    catch (Exception ex)
//	    {
//	    	Log.e(TAG, "Camera is not available (in use or does not exist");
//	    }
//	    return cam; // returns null if camera is unavailable
//	}
//	
//	/**
//	 * starts the previewing of the Camera image data. Only use after
//	 * a PreviewCallback has been set to the Camera 
//	 */
//	private static synchronized final void startPreview()
//	{
//		if (cam != null)
//		{
//			try
//			{
//				cam.startPreview();
//			} catch (Exception ex) {}
//		}
//	}
}
