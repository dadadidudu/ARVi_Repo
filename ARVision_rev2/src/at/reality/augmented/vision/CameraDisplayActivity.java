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
}
