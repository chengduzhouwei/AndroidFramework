package org.zw.android.framework.util;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;

/**
 * 
 * @author zhouwei
 *
 */
public final class BitmapUtils {
	
	/** default bitmap width: 4:3*/
	public static int MAX_WIDTH			= 480 ;
	
	/** default bitmap height*/
	public static int MAX_HEIGHT		= 360 ;

	public static final Bitmap createBitmap(byte[] data,int target_width,int target_height) {
		try{
			int width				 = target_width <= 0 ? MAX_WIDTH : target_width ;
			int height				 = target_height <= 0 ? MAX_HEIGHT :  target_height;
			int minSideLength 		 = 0 ;
			Options opts  			 = new BitmapFactory.Options();
			opts.inJustDecodeBounds  = true;
			BitmapFactory.decodeByteArray(data, 0, data.length,opts);
			// set parameter
			minSideLength 	 		 = Math.min(width, height);
			opts.inSampleSize 		 = computeSampleSize(opts, minSideLength, width * height);
			opts.inJustDecodeBounds  = false;
			opts.inInputShareable 	 = true;
			opts.inPurgeable 		 = true;
			opts.inPreferredConfig 	 = Config.RGB_565 ;
			// decode bitmap
			return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		} catch(Exception e){
			return null ;
		} catch (OutOfMemoryError e) {
			e.printStackTrace() ;
			return null ;
		}
	}
	
	public static final Bitmap createBitmap(String path,int target_width,int target_height) {
		try{
			int width				 = target_width <= 0 ? MAX_WIDTH : target_width ;
			int height				 = target_height <= 0 ? MAX_HEIGHT :  target_height;
			int minSideLength 		 = 0 ;
			Options opts  			 = new BitmapFactory.Options();
			opts.inJustDecodeBounds  = true;
			
			// bitmap degree
			int degree 				 = parserBitmapDegree(path);
			
			// decode bitmap
			BitmapFactory.decodeFile(path,opts);
			
			// set parameter
			minSideLength 	 		 = Math.min(width, height);
			opts.inSampleSize 		 = computeSampleSize(opts, minSideLength, width * height);
			opts.inJustDecodeBounds  = false;
			opts.inInputShareable 	 = true;
			opts.inPurgeable 		 = true;
			opts.inPreferredConfig 	 = Config.RGB_565 ;
			
			// decode bitmap
			Bitmap out = BitmapFactory.decodeFile(path,opts) ;
			
			// parser bitmap degree
			if(out != null){
				return RotateBitmap(degree,out);
			}
			
			// decode bitmap
			return out;
		} catch(Exception e){
			return null ;
		} catch (OutOfMemoryError e) {
			e.printStackTrace() ;
			return null ;
		} 
	}
	
	public static int parserBitmapDegree(String path) {
		
		int degree = 0;
		
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return degree;
	}
	
	public static Bitmap RotateBitmap(int angle, Bitmap bitmap) {
		
		if(bitmap == null){
			return null ;
		}
		
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		
		Bitmap out = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		
		// bitmap.recycle() ;
		
		return out;
	}
	
	private final static int computeSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
	    int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
	    int roundedSize;
	    if (initialSize <= 8) {
	        roundedSize = 1;
	        while (roundedSize < initialSize) {
	            roundedSize <<= 1;
	        }
	    } else {
	        roundedSize = (initialSize + 7) / 8 * 8;
	    }
	    return roundedSize;
	}
	
	private final static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
	    double w = options.outWidth;
	    double h = options.outHeight;

	    int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
	    int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

	    if (upperBound < lowerBound) {
	        // return the larger one when there is no overlapping zone.
	        return lowerBound;
	    }

	    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
	        return 1;
	    } else if (minSideLength == -1) {
	        return lowerBound;
	    } else {
	        return upperBound;
	    }
	}
}
