package at.reality.augmented.vision;

import android.graphics.Bitmap;

public interface YUVToRGBDecoder {
	
	public Bitmap decode(byte[] YUVData, int width, int height);

}
