package at.reality.augmented.vision.PreviewThreading;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public final class CameraPreviewHandlerThread extends HandlerThread {
	private final String TAG = CameraPreviewHandlerThread.class.getName();
	private Handler handler = null;
	private Camera cam;

    public CameraPreviewHandlerThread() {
        super("CameraHandlerThread");
        start();
        handler = new Handler(getLooper());
    }

    private synchronized void notifyCameraOpened() {
        notify();
    }

    public Camera openCamera() {
        handler.post(new Runnable() {
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
        return cam;
    }
    
    private final void getCameraInstance()
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

}
