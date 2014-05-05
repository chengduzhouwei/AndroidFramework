package org.zw.android.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

public final class FileUtil {

	private FileUtil(){
		
	}
	
	public static String createSdcardDataPath(String folderName){
		
		if(folderName == null || folderName.trim().equals("")){
			return null ;
		}
		
		String dir = null ;
		
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			dir =  Environment.getExternalStorageDirectory().getPath() + File.separator + folderName ;
		}
		
		if (dir != null) {
			
			File f = new File(dir);

			if (!f.exists()) {
				f.mkdirs();
			}
		}
		
		return dir;
	}
	
	public static String createPrivateDataPath(Context context,String folderName) {
		
		final String dir = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? getExternalCacheDir(context)
				.getPath() : context.getCacheDir().getPath();

		String out = dir != null ? (dir + File.separator + folderName) : null;
		
		if (out != null) {
			File f = new File(out);

			if (!f.exists()) {
				f.mkdirs();
			}
		}
		
		return out;
	}
	
	private static File getExternalCacheDir(Context context) {
		
		if(context == null){
			return null ;
		}
		
		final String cacheDir = "/Android/data/" + context.getPackageName() + "/";
		
		return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
	}
	
	public static final String insertImageToPhotos(Context context,
			String imagePath, String name, String description){
		
		try{
			
			String out = insertImage(context.getContentResolver(),imagePath,name,description);
			
			if(StringUtils.isEmpty(out)){
				context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory() + out)));
				return out ;
			}
		} catch (Exception e){
			e.printStackTrace() ;
		}
		
		return null ;
	}
	
	/** 插入图片到相册中*/
	public static final String insertImage(ContentResolver cr,
			String imagePath, String name, String description)
			throws FileNotFoundException {
		
		if(StringUtils.isEmpty(imagePath)){
			return null ;
		}
		
		// Check if file exists with a FileInputStream
		FileInputStream stream = new FileInputStream(imagePath);
		try {
			Bitmap bm = BitmapFactory.decodeFile(imagePath);
			String ret = insertImage(cr, bm, name, description);
			bm.recycle();
			return ret;
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static final String insertImage(ContentResolver cr, Bitmap source,
			String title, String description) {
		
		ContentValues values = new ContentValues();
		values.put(Images.Media.TITLE, title);
		values.put(Images.Media.DESCRIPTION, description);
		values.put(Images.Media.MIME_TYPE, "image/jpeg");

		Uri url = null;
		String stringUrl = null; /* value to be returned */

		try {
			url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

			if (source != null) {
				OutputStream imageOut = cr.openOutputStream(url);
				try {
					source.compress(Bitmap.CompressFormat.JPEG, 80, imageOut);
				} finally {
					imageOut.close();
				}

				long id = ContentUris.parseId(url);
				
				// Wait until MINI_KIND thumbnail is generated.
				Bitmap miniThumb = Images.Thumbnails.getThumbnail(cr, id,Images.Thumbnails.MINI_KIND, null);
				
				// This is for backward compatibility.
				StoreThumbnail(cr, miniThumb, id, 50F, 50F,Images.Thumbnails.MICRO_KIND);
			} else {
				cr.delete(url, null, null);
				url = null;
			}
		} catch (Exception e) {
			if (url != null) {
				cr.delete(url, null, null);
				url = null;
			}
		}

		if (url != null) {
			stringUrl = url.toString();
		}

		return stringUrl;
	}
	
	private static final Bitmap StoreThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width, float height,
            int kind) {
        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                                           source.getWidth(),
                                           source.getHeight(), matrix,
                                           true);

        ContentValues values = new ContentValues(4);
        values.put(Images.Thumbnails.KIND,     kind);
        values.put(Images.Thumbnails.IMAGE_ID, (int)id);
        values.put(Images.Thumbnails.HEIGHT,   thumb.getHeight());
        values.put(Images.Thumbnails.WIDTH,    thumb.getWidth());

        Uri url = cr.insert(Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);

            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        }
        catch (FileNotFoundException ex) {
            return null;
        }
        catch (IOException ex) {
            return null;
        }
    }
}
