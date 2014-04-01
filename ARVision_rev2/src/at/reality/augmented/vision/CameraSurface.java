package at.reality.augmented.vision;

import java.io.IOException;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import at.reality.augmented.vision.PreviewThreading.CameraPreviewHandlerThread;
import at.reality.augmented.vision.decoder.IYuvToRgbDecoder;
import at.reality.augmented.vision.decoder.IntrinsicsDecoder;

public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback, Runnable {
	// Debugging-Tag
	private static final String TAG = CameraSurface.class.getSimpleName();
	// executing?
	private boolean isStopped = false;
	private Context context;
	private SurfaceHolder surfaceHolder;
	
	/** the Thread that will be used for executing the method onPreviewFrame() */
	private CameraPreviewHandlerThread previewThread = null;
	
	private Camera cam;
	private Parameters cameraParams;
	/** the RGB-Bitmap that results from a preview frame */
	private Bitmap previewRGB;
	/** the decoder used for conversion of YUV to RGB */
	private static IYuvToRgbDecoder decoder;

	
	// --- constructors ---
	public CameraSurface(Context context) {
		super(context);
		Log.e(TAG, "right mehtod");
		this.context = context;
		this.surfaceHolder = getHolder();
		this.surfaceHolder.addCallback(this);
		this.setFocusable(true);
		

		// TODO fill
		
	}

	public CameraSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.e(TAG, "wrong method1");
		// TODO delete this constructor eventually
	}

	public CameraSurface(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Log.e(TAG, "wrong method2");
		// TODO delete this constructor eventually
	}
	
	private void initDecoder(Context context) {
		decoder = new IntrinsicsDecoder(context);
	}
	// --- constructors end ---
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// initialise decoder
		this.initDecoder(context);
		// ignore: done in run()
		
		// TESTING
//		if (cam == null) {
//			this.openCamera();
//			cameraParams = cam.getParameters();
//			try {
//				cam.setPreviewDisplay(holder);
//				cam.setPreviewCallback(this);
//			} catch (IOException ex) {
//				Log.e(TAG, "Surface unavailable");		
//				ex.printStackTrace();
//			}
//		}
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		/*
		Log.e(TAG, "called SurfaceChanged()-method");

		if (surfaceHolder.getSurface() == null) // preview surface does not exist
			return;
		
		// stop preview before making changes
        try
        {
            cam.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }
        cameraParams.setPreviewSize(width, height);
        // start preview with new settings
        try {
        	cam.setPreviewDisplay(surfaceHolder);
        	cam.startPreview();
        } catch (Exception e){
        	Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
		 */
		
//		// TESTING
//		cameraParams.setPreviewSize(width, height);    
//        cam.setParameters(cameraParams); 
//        cam.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO de-initialise decoder
		decoder = null;
		// ignore: done in stop()
		
//		// TESTING
//		cam.stopPreview();
//		cam.setPreviewCallback(null);
//		getHolder().removeCallback(this);
//		cam.release();
//		cam = null;
	}

	
	/**
	 * this mehtod should be called from the thread that executed
	 * the run()-method of this class
	 * @param data
	 * @param camera
	 */
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		Log.d(TAG, "this PreviewFrame is brought to you by: " + Thread.currentThread().getName());
		Log.d(TAG, "is mythread dead yet? " + Thread.activeCount());
	}

	@Override
	public void run() {
		// TODO get and open camera, start preview, etc
		
		if (cam == null) {
			try {
				this.openCamera();
				cam.setPreviewDisplay(surfaceHolder);
				cam.setPreviewCallback(this);
				cam.startPreview();

				cameraParams = cam.getParameters();
				
			} catch (IOException ex) {
				Log.e(TAG, "Surface unavailable");
				ex.printStackTrace();
			}
		}
		else {
			Log.e(TAG, "Camera in use");
		}
		
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
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public synchronized void stop() {
		this.isStopped = true;
		// TODO: close camera, clear resources
		
		if (cam != null)
		{
			cam.stopPreview();
			cam.setPreviewCallback(null);
			getHolder().removeCallback(this);
			cam.release();
			cam = null;
			try {
				previewThread.quitSafely();
				previewThread.join(2500);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} finally {
				previewThread = null;
			}
		}
		
	}
	
	
	
	
	/**
	 * opens the Camera in a separate Thread in the manner that
	 * onPreviewFrame() can be executed in that Thread.
	 */
	private final void openCamera() {
//		if (previewThread == null)
		{
	        previewThread = new CameraPreviewHandlerThread();
	    }

	    synchronized (previewThread) {
	        this.cam = previewThread.openCamera();
//	    	previewThread.openCamera();
	    }
	}
	
	/**
	 * returns an instance of the camera. Use only if the camera is not in use,
	 * else an error-entry to the Log will be made and cam will remain null.
	 */
	/*
	private final static void getCameraInstance()
	{
		cam = null;
	    try
	    {
	        cam = Camera.open(); // attempt to get a Camera instance
	        Log.i(TAG, "Camera reeived, is "+ cam.toString());
	        Log.d(TAG, "this Camera was opened by: " + Thread.currentThread().getName());
	    }
	    catch (Exception ex)
	    {
	    	Log.e(TAG, "Camera is not available (in use or does not exist");
	    }
	}
	
	
	private final static class CameraPreviewHandlerThread extends HandlerThread {
		Handler mHandler = null;

	    CameraPreviewHandlerThread() {
	        super("CameraHandlerThread");
	        start();
	        mHandler = new Handler(getLooper());
	    }

	    synchronized void notifyCameraOpened() {
	        notify();
	    }

	    void openCamera() {
	        mHandler.post(new Runnable() {
	            @Override
	            public void run() {
	                getCameraInstance();
	                notifyCameraOpened();
	            }
	        });
	        try {
	            wait();
	        }
	        catch (InterruptedException e) {
	            Log.w(TAG, "wait was interrupted");
	        }
	    }
	}
	*/
	

}
