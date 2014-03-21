package at.reality.augmented.vision.decoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import at.reality.augmented.vision.decoder.IYuvToRgbDecoder;

//~200ms
public class Decoder1 implements IYuvToRgbDecoder {
	
	public Decoder1(Context context) {}
	
	public Bitmap decode(byte[] yuv420sp, int width, int height)
	{

		final int frameSize = width * height;
		int[] rgb = new int[frameSize];

		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;  
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
		return Bitmap.createBitmap(rgb, width, height, Config.ARGB_8888);
	}
}
