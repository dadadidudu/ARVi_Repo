package at.reality.augmented.vision.decoder;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Bitmap.Config;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import at.reality.augmented.vision.decoder.IYuvToRgbDecoder;

// ~50ms!
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class IntrinsicsDecoder implements IYuvToRgbDecoder {

	// Variablen
	private RenderScript rs;
	private ScriptIntrinsicYuvToRGB intrinsic;
	private Type.Builder tb1, tb2;
	private Allocation allocIn, allocOut;
	private Bitmap out;

	// Anm: moegliches Problem ist, dass das zu oft created bzw aufgerufen wird
	// -> evtl als singleton umsetzen?
	// oder static RenderScript

	public IntrinsicsDecoder(Context context) {
		rs = RenderScript.create(context);
		intrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
	}

	public Bitmap decode(byte[] data, int width, int height) {

		tb1 = new Type.Builder(rs, Element.createPixel(rs, Element.DataType.UNSIGNED_8, Element.DataKind.PIXEL_YUV));
		tb1.setX(width);
		tb1.setY(height);
		tb1.setMipmaps(false);
		tb1.setYuvFormat(ImageFormat.NV21); // API 18!
		allocIn = Allocation.createTyped(rs, tb1.create());
		allocIn.copyFrom(data);

		tb2 = new Type.Builder(rs, Element.RGBA_8888(rs));
		tb2.setX(width);
		tb2.setY(height);
		tb2.setMipmaps(false);
		allocOut = Allocation.createTyped(rs, tb2.create());

		intrinsic.setInput(allocIn);
		intrinsic.forEach(allocOut);

		out = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		allocOut.copyTo(out);

		// clean vars
		allocIn = null;
		allocOut = null;

		return out;

	}
}
