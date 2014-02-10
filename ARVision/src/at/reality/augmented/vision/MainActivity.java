package at.reality.augmented.vision;

import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends Activity {

	public static Activity act;
	
	private Camera cam;
	private CameraSurface cs;
	private DrawingSurface ds;
	private FrameLayout flay;
	private RelativeLayout interactionThings;
		// fillings
		private Button button;
		private TextView tv;
	private boolean inPreview = false; // is camera in preview mode?
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		act = this;
		
		// get camera instance
		this.cam = getCameraInstance();
		/* create FrameView and Surfaces -- done in onResume()
		this.flay = new FrameLayout(this);
		this.cs = new CameraSurface(this, cam);
		this.ds = new DrawingSurface(this);
		*/
		// create RelativeLayout to be used for buttons etc
		this.interactionThings = new RelativeLayout(this);
		// fill the RelativeLayout (with interactionThings)
		button = new Button(this);
		tv = new TextView(this);
		button.setWidth(150);
		button.setText("imabutton");
		tv.setText("abcdefg");
		// add to Relative
		interactionThings.addView(button);
		interactionThings.addView(tv);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	protected void onPause()
	{
		if (inPreview)
			cam.stopPreview();
		
		if (cam != null)
		{
			cs.setCamera(null);
			inPreview = false;
			cam.release();
			cam = null;
		}
		if (ds != null)
		{
	        //ds.onPause();
	    }
		super.onPause();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		if (cam == null)
		{
			cam = getCameraInstance();
		}
		
		if (cam != null){
	        flay = new FrameLayout(this);
	        flay.setLayoutParams(new LayoutParams(
	           LayoutParams.MATCH_PARENT,
	           LayoutParams.MATCH_PARENT));
	 
	        cs = new CameraSurface(this, cam);
	        flay.addView(cs);
	 
	        ds = new DrawingSurface(this);
	        flay.addView(ds);
	        
	        // uncomment when ready to build interactivity:
	        // interactionThings = new RelativeLayout(this);
	        flay.addView(interactionThings);
	        
	        setContentView(flay);
	    }
		
		cs.setCamera(cam);
		startPreview();
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
	
	public static Camera getCameraInstance()
	{
		Camera c = null;
	    try
	    {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e)
	    {
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	public void startPreview()
	{
		if (cam != null)
		{
			try
			{
				cam.startPreview();
			} catch (Exception e) {}
			inPreview = true;
		}
	}
}
