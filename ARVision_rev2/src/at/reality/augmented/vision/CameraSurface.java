package at.reality.augmented.vision;

import java.io.IOException;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import at.reality.augmented.vision.PreviewThreading.CameraPreviewHandlerThread;
import at.reality.augmented.vision.decoder.IYuvToRgbDecoder;
import at.reality.augmented.vision.decoder.IntrinsicsDecoder;

public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback {
	// Debugging-Tag
	private static final String TAG = CameraSurface.class.getSimpleName();
	
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
		// initialise decoder
		this.initDecoder(context);
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {		

		if (surfaceHolder.getSurface() == null) // preview surface does not exist
			surfaceHolder = holder;
        
        if (cam == null) 
			this.openCamera();
        else
        	cam.stopPreview();
        // change preview settings, set the PreviewDisplay and PreviewCallack and start the preview
        cameraParams = cam.getParameters();
		cameraParams.setPreviewSize(width, height);
		cam.setParameters(cameraParams);
		try {
			cam.setPreviewDisplay(surfaceHolder);
			cam.setPreviewCallback(this);
		} catch (IOException ex) {
			Log.e(TAG, "Surface unavailable");		
			ex.printStackTrace();
		}
		cam.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
				
		cam.stopPreview();
		cam.setPreviewCallback(null);
		getHolder().removeCallback(this);
		cam.release();
		cam = null;
		try {
			// this line only for API 18+
			// previewThread.quitSafely();
			previewThread.quit();
			previewThread.join();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} finally {
			previewThread = null;
		}
		
		// de-initialise decoder
		decoder = null;
		System.gc();
	}

	
	/**
	 * this mehtod should be called from the thread that executed
	 * the run()-method of this class
	 * @param data
	 * @param camera
	 */
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		
		// decode to Bitmap
		if (camera != null) {
			Log.d(TAG, "this PreviewFrame is brought to you by: " + Thread.currentThread().getName());

			previewRGB = decoder.decode(data,
					cameraParams.getPreviewSize().width,
					cameraParams.getPreviewSize().height);
			int colorDings = previewRGB.getPixel(0, 0);
			Log.i(TAG,
					"Value of top left pixel is: A = "
							+ Color.alpha(colorDings) + ", R = "
							+ +Color.red(colorDings) + ", G = "
							+ Color.green(colorDings) + ", B = "
							+ +Color.blue(colorDings));
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
			{
				Log.i(TAG, "DOWN --- X: " + event.getX() + " - Y: " + event.getY());
				break;
			}
			case MotionEvent.ACTION_MOVE:
			{
				Log.i(TAG, "MOVE --- X: " + event.getX() + " - Y: " + event.getY());
				break;
			}
			case MotionEvent.ACTION_UP:
			{
				Log.i(TAG, "UP --- X: " + event.getX() + " - Y: " + event.getY());
				break;
			}
		}
		
		return true;
	}
	
	@Override
	public boolean onHoverEvent(MotionEvent event)
	{
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_HOVER_ENTER:
			{
				Log.i(TAG, "HOVER enter on --- X: " + event.getX() + " - Y: " + event.getY());
				break;
			}
			case MotionEvent.ACTION_HOVER_MOVE:
			{
				Log.i(TAG, "HOVER moves on --- X: " + event.getX() + " - Y: " + event.getY());
				break;
			}
			case MotionEvent.ACTION_HOVER_EXIT:
			{
				Log.i(TAG, "HOVER exit on --- X: " + event.getX() + " - Y: " + event.getY());
				break;
			}
		}
		return true;
	}
	
	
	// --------------
	/**
	 * opens the Camera in a separate Thread in the manner that
	 * onPreviewFrame() can be executed in that Thread.
	 */
	private final void openCamera() {

		if (previewThread == null)
			previewThread = new CameraPreviewHandlerThread();

		synchronized (previewThread) {
			this.cam = previewThread.openCamera();
		}
	}
}
