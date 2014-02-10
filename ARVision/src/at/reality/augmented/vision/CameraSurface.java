package at.reality.augmented.vision;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.AttributeSet;
import android.util.Log;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.*;

/**
 * this method represents the basic surface of the camera image plus overlayings
 * 
 * @author Daniel Heger 
 */
public class CameraSurface extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback
{
	private static final String TAG = CameraSurface.class.getSimpleName();// DEBUG-Message
	// variables
	private Context context; // the context we're in
	private static boolean paused; // is the activity paused?
	private UpdateThread th;
	private Camera cam;
	private SurfaceHolder cholder;
	private Paint paint = new Paint();
	//private DrawingSurface ds;
	
	// Bild
	private static Bitmap bmp;
	
	//This variable is responsible for getting and setting the camera settings  
    private Parameters parameters;  
    //this variable stores the camera preview size   
    private Size previewSize;  
    //this array stores the pixels as hexadecimal pairs   
    private int[] pixels;
    //private int[][][] RGB;	// RGB[x = width] [y = height] [0 for R, 1 for G, 2 for B]
    
    // statistische Zeitmessung fuer PreviewBild-Conversion
    long beginT, endT, totalRunT, execCount;
	
	// Constructors
	public CameraSurface(Context context)
	{
		super(context);
		cholder = getHolder();
		cholder.addCallback(this);
		setFocusable(true);
		this.context = context;
	}
	
	public CameraSurface(Context context, Camera c)
	{
		super(context);
		setFocusable(true);
		cholder = getHolder();
		cholder.addCallback(this);
		this.context = context;
		this.cam = c;
	}
	
	public CameraSurface(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		/*
		cholder = getHolder();
		cholder.addCallback(this);
		setFocusable(true);
		this.context = context;
		*/
	}
	
	public CameraSurface(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		/*
		cholder = getHolder();
		cholder.addCallback(this);
		setFocusable(true);
		this.context = context;
		*/
	}
	// Constructors end
	
	public void setCamera(Camera c)
	{
		cam = c;
        if (cam != null) {
            //mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();
        }
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		paint.setTextSize(25);
		/*
		// after initializing, create and start the thread
		th = new UpdateThread(getHolder(), this);
		th.setRunning(true);
		th.start();
		Log.d(TAG, "Thread created");
		*/
		// The Surface has been created, now tell the camera where to draw the preview.
        try
        {
        	if (cam != null)
        		cam.setPreviewDisplay(holder);
        	// Preview-Callback
        	cam.setPreviewCallback(this);
            //cam.startPreview(); in SurfaceChanged
        	
            // initialize the variables  
            parameters = cam.getParameters();  
            previewSize = parameters.getPreviewSize();  
            pixels = new int[previewSize.width * previewSize.height];
            //RGB = new int[previewSize.width][previewSize.height][3];
            
            // experimentelle Unterstuetzung fuer Threading
            th = new UpdateThread(cholder, this);
            th.start();
            Log.d(TAG, "Camera-Thread (re)started");
            
        } catch (IOException e) {
        	cam.release();
        	cam = null;
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
         
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		if (cholder.getSurface() == null) // preview surface does not exist
			return;
		/*
	        // stop preview before making changes
	        try
	        {
	            cam.stopPreview();
	        } catch (Exception e){
	          // ignore: tried to stop a non-existent preview
	        }
			*/
		
	        // set preview size and make any resize, rotate or
	        // reformatting changes here
	        // Now that the size is known, set up the camera parameters and begin  
	        // the preview.  
	        parameters.setPreviewSize(width, height);  
	        //set the camera's settings  
	        cam.setParameters(parameters); 
	        cam.startPreview();
	        
	        /*
	        // start preview with new settings
	        try {
	            cam.setPreviewDisplay(cholder);
	            cam.startPreview();

	        } catch (Exception e){
	            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
	        }
	        */
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		if (cam != null)
		{
			cam.stopPreview();
			cam.release();
			cam = null;
		}
		
		Log.d(TAG, "Durchschnittslaufzeit fuer die Conversion = " + Long.toString(totalRunT/execCount) + "ms");
		
		// shutdown thread
		boolean retry = true;
		while (retry)
		{
			try
			{
				th.setRunning(false);
				th.join();
				retry = false;
				((Activity)getContext()).finish();
				Log.d(TAG, "Camera-Thread shut down, Activity finished");
			}
			catch (Exception e)
			{
				e.getMessage();
				e.getStackTrace();
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
			{
				Log.d(TAG, "DOWN --- X: " + event.getX() + " - Y: " + event.getY());
				break;
			}
			case MotionEvent.ACTION_MOVE:
			{
				Log.d(TAG, "MOVE --- X: " + event.getX() + " - Y: " + event.getY());
				break;
			}
			case MotionEvent.ACTION_UP:
			{
				Log.d(TAG, "UP --- X: " + event.getX() + " - Y: " + event.getY());
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
				Log.d(TAG, "HOVER enter on --- X: " + event.getX() + " - Y: " + event.getY());
				break;
			}
			case MotionEvent.ACTION_HOVER_MOVE:
			{
				Log.d(TAG, "HOVER moves on --- X: " + event.getX() + " - Y: " + event.getY());
				break;
			}
			case MotionEvent.ACTION_HOVER_EXIT:
			{
				Log.d(TAG, "HOVER exit on --- X: " + event.getX() + " - Y: " + event.getY());
				break;
			}
		}
		return true;
	}
	
	public void update()
	{
		// update
	}
	
	public void doDraw(Canvas canvas)
	{
		if (th.getRunning() == true)
		{
			//super.draw(canvas);
			
			//canvas.drawText("abcdefffffff", 400, 200, paint);
			
		}
	}

	
	/*
	 * FUNKTIONIERT, manipuliert int-Array
	 * ist relativ schnell
	 */ 
	@Override
	public void onPreviewFrame(byte[] data, Camera camera)
	{
		//transforms NV21 pixel data into RGB pixels 
		beginT = System.currentTimeMillis();
        decodeYUV420SP(pixels, data, previewSize.width, previewSize.height);
        endT = System.currentTimeMillis();
		totalRunT = (endT - beginT);
		execCount++;
		Log.i("CameraSurf", "runtime for decodeYUV() = "+ totalRunT);
		
		
		// Bitmap wird mit dem Bild der Kamera befuellt
		beginT = System.currentTimeMillis();
		bmp = Bitmap.createBitmap(pixels, previewSize.width, previewSize.height, Config.ARGB_8888);
		endT = System.currentTimeMillis();
		totalRunT = (endT - beginT);
		
        //Outuput the value of the top left pixel in the preview to LogCat  
		// TODO: not sure if works, please check
		// anm: performance um die 200-300ms fuer Erstellung der Bitmap, Programm aber recht unfluessig, maybe bec.of conversion
		Log.i("CameraSurf", "The top right pixel has the following value: " + bmp.getPixel(0, 0));
		Log.i("CameraSurf", "runtime for createBitmap() = "+ totalRunT);
		
		/*
        Log.i("Pixels", "The top right pixel has the following RGB (hexadecimal) values:"  
                +Integer.toHexString(pixels[0]) + " -- decimal value: "+ Integer.toString(pixels[0]));
        Log.i("Pixels", "Alpha = " + Integer.toHexString(pixels[0]).substring(0, 2)
        		+ " - R = " +  Integer.toHexString(pixels[0]).substring(2, 4)
        		+ " - G = " +  Integer.toHexString(pixels[0]).substring(4, 6)
        		+ " - B = " +  Integer.toHexString(pixels[0]).substring(6, 8));
        /*
        Log.i(TAG, "dedcated picture-array: RED HEX = " + Integer.toHexString(RGB[0][0][0]) + "Dec = " + Integer.toString(RGB[0][0][0]));
        Log.i(TAG, "dedcated picture-array: GREEN HEX = " + Integer.toHexString(RGB[0][0][1]) + "Dec = " + Integer.toString(RGB[0][0][1]));
        Log.i(TAG, "dedcated picture-array: BLUE HEX = " + Integer.toHexString(RGB[0][0][2]) + "Dec = " + Integer.toString(RGB[0][0][2]));
		*/
	}
	
	// conversion to RGB
	// needs ca 1500 - 3000 ms of time
	void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height)
	{  
        
        final int frameSize = width * height;  

        for (int j = 0, yp = 0; j < height; j++) {       int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;  
          for (int i = 0; i < width; i++, yp++) {  
            int y = (0xff & ((int) yuv420sp[yp])) - 16;  
            if (y < 0)  
              y = 0;  
            if ((i & 1) == 0) {  
              v = (0xff & yuv420sp[uvp++]) - 128;  
              u = (0xff & yuv420sp[uvp++]) - 128;  
            }  

            int y1192 = 1192 * y;  
            int r = (y1192 + 1634 * v);  
            int g = (y1192 - 833 * v - 400 * u);  
            int b = (y1192 + 2066 * u);  

            if (r < 0)                  
            	r = 0;               
            else if (r > 262143)  
               r = 262143;  
            
            if (g < 0)                  
            	g = 0;               
            else if (g > 262143)  
               g = 262143;
            
            if (b < 0)                  
            	b = 0;               
            else if (b > 262143)  
               b = 262143;  

            //RGB[i][j][0] = r;
            //RGB[i][j][1] = g;
            //RGB[i][j][2] = b;
            
            rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            
          }  
        }  
      }
      
	
	/*
	@Override
	public void onPreviewFrame(byte[] data, Camera camera)
	{
		//transforms NV21 pixel data into RGB pixels
		beginT = System.currentTimeMillis();
		pixels = convertYUV420_NV21toRGB8888(data, previewSize.width, previewSize.height); 
		endT = System.currentTimeMillis();
		totalRunT = (endT - beginT);
		execCount++;
        //Outuput the value of the top left pixel in the preview to LogCat  
        Log.i("Pixels", "The top right pixel has the following RGB (hexadecimal) values:" +Integer.toHexString(pixels[0]));
        Log.i("CamSurf","runtime == " + totalRunT);
		
	}
	
	/**
	 * Converts YUV420 NV21 to RGB8888
	 * is about 1500ms fast
	 * 
	 * @param data byte array on YUV420 NV21 format.
	 * @param width pixels width
	 * @param height pixels height
	 * @return a RGB8888 pixels int array. Where each int is a pixels ARGB. 
	 *
	 *
	public static int[] convertYUV420_NV21toRGB8888(byte [] data, int width, int height)
	{
	    int size = width*height;
	    int offset = size;
	    int[] pixels = new int[size];
	    int u, v, y1, y2, y3, y4;

	    // i percorre os Y and the final pixels
	    // k percorre os pixles U e V
	    for(int i=0, k=0; i < size; i+=2, k+=2) {
	        y1 = data[i  ]&0xff;
	        y2 = data[i+1]&0xff;
	        y3 = data[width+i  ]&0xff;
	        y4 = data[width+i+1]&0xff;

	        u = data[offset+k  ]&0xff;
	        v = data[offset+k+1]&0xff;
	        u = u-128;
	        v = v-128;

	        pixels[i  ] = convertYUVtoRGB(y1, u, v);
	        pixels[i+1] = convertYUVtoRGB(y2, u, v);
	        pixels[width+i  ] = convertYUVtoRGB(y3, u, v);
	        pixels[width+i+1] = convertYUVtoRGB(y4, u, v);

	        if (i!=0 && (i+2)%width==0)
	            i+=width;
	    }

	    return pixels;
	}

	private static int convertYUVtoRGB(int y, int u, int v)
	{
	    int r,g,b;

	    r = y + (int)1.402f*v;
	    g = y - (int)(0.344f*u +0.714f*v);
	    b = y + (int)1.772f*u;
	    r = r>255? 255 : r<0 ? 0 : r;
	    g = g>255? 255 : g<0 ? 0 : g;
	    b = b>255? 255 : b<0 ? 0 : b;
	    return 0xff000000 | (b<<16) | (g<<8) | r;
	}
	*/
}
