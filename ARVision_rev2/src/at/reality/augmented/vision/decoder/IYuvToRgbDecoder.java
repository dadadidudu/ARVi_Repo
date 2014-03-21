package at.reality.augmented.vision.decoder;

import android.graphics.Bitmap;

/**
 * interface that implements the YUV-to-RGB decoding operation
 * @author Daniel Heger
 *
 */
public interface IYuvToRgbDecoder {
	
	/**
	 * decodes YUV image data to RGB data and returns this data
	 * as a bitmap for further use
	 * @param YUVData the raw YUV camera picture data
	 * @param width the width of the picture data in pixel
	 * @param height the height of the image data in pixel
	 * @return an Android Bitmap containing the RGB picture
	 * 	data of the given YUV-image 
	 */
	public Bitmap decode(byte[] YUVData, int width, int height);

}
