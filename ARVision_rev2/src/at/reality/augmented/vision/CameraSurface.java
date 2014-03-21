package at.reality.augmented.vision;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import at.reality.augmented.vision.decoder.IYuvToRgbDecoder;
import at.reality.augmented.vision.decoder.IntrinsicsDecoder;

public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback, Runnable {
	// Debugging-Tag
	private static final String TAG = CameraSurface.class.getSimpleName();
	// executing?
	private boolean isStopped = false;
	private Context context;
	private SurfaceHolder surfaceHolder;
	
	private static Camera cam;
	private IYuvToRgbDecoder decoder;

	
	// --- constructors ---
	public CameraSurface(Context context) {
		super(context);
		this.context = context;
		this.surfaceHolder = getHolder();
		this.surfaceHolder.addCallback(this);
		this.setFocusable(true);
		this.initDecoder(context);

		// TODO fill
		
	}

	public CameraSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO delete this constructor eventually
		this.initDecoder(context);
	}

	public CameraSurface(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO delete this constructor eventually
		this.initDecoder(context);
	}
	
	private void initDecoder(Context context) {
		decoder = new IntrinsicsDecoder(context);
	}
	// --- constructors end ---
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	// --- constructors end ---
	
	/**
	 * this mehtod should be called from the thread that executed
	 * the run()-method of this class
	 * @param data
	 * @param camera
	 */
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void run() {
		// TODO get and open camera, start preview, etc
		
	}
	
	/**
	 * returns whether the CameraSurface is running or not
	 * @return true, if this CameraSurface is running, false otherwise
	 */
	private synchronized boolean isStopped() {
		return this.isStopped;
	}

	/**
	 * use this method to stop displaying Camera Preview Data on this Surface
	 */
	public synchronized void stop() {
		this.isStopped = true;
		// TODO: close camera, clear resources
		
	}
	
	/**
	 * returns an instance of the camera. Use only if the camera is not in use,
	 * else an error-entry to the Log will be made.
	 * @return an instance of the camera
	 */
	private static synchronized final Camera getCameraInstance()
	{
		cam = null;
	    try
	    {
	        cam = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception ex)
	    {
	    	Log.e(TAG, "Camera is not available (in use or does not exist");
	    }
	    return cam; // returns null if camera is unavailable
	}
	
	/**
	 * starts the previewing of the Camera image data. Only use after
	 * a PreviewCallback has been set to the Camera 
	 */
	private static synchronized final void startPreview()
	{
		if (cam != null)
		{
			try
			{
				cam.startPreview();
			} catch (Exception ex) {}
		}
	}

}
