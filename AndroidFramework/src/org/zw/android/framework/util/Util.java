package org.zw.android.framework.util;

import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 
 * android util
 * 
 * @author wei.zhou
 * 
 */
public final class Util {

	/***/
	//private final static float TARGET_HEAP_UTILIZATION = 0.75f;
	
	/** 9M heap size*/
	//private final static int CWJ_HEAP_SIZE = 9 * 1024 * 1024;
	
	/**
	 * set vm utiliztion
	 */
	public static final void setDalvikHeapUtilization(){
		//VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION);
	}
	
	/**
	 * set dalvik bitmap memory size: 9M
	 * 
	 */
	public static final void setDalvikMinimumHeapSize(){
		//VMRuntime.getRuntime().setMinimumHeapSize(CWJ_HEAP_SIZE);
	}
	
	public static final void showApplicationMemoryInfo(){
		float totalMemory = Runtime.getRuntime().totalMemory() >> 20 ;
		float maxMemory = Runtime.getRuntime().maxMemory() >> 20 ;
		float freeMemory = Runtime.getRuntime().freeMemory() >> 10 ;
		android.util.Log.d("Memeory Info", "Max : " + maxMemory + " M") ;
		android.util.Log.d("Memeory Info", "Tol : " + totalMemory + " M" ) ;
		android.util.Log.d("Memeory Info", "Free: " + freeMemory + " KB") ;
	}
	
    /**
     * 
     * @param context
     * @param number
     */
    public static final void callSystemDial(final Context context, final String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        context.startActivity(intent);
    }

    /**
     * 
     * @param context
     * @param number
     * @param message
     */
    public static final void callSystemShortMessage(final Context context, final String number, final String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + number));
        intent.putExtra("sms_body", message);
        context.startActivity(intent);
    }

    /**
     * 
     * @param context
     * @return
     */
    public static final boolean isDeviceReadyForConnection(Context context) {
    	if(context == null) return false ;
    	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	// network
        if (cm != null 
        		&& (cm.getActiveNetworkInfo() != null) 
        		&& cm.getActiveNetworkInfo().isConnected())
            return true;
        return false;
    }
    
    /**
     * 
     * @param activity
     * @param afterInterval
     */
    public static final void showVirtualKeyPad(final Context context) {
        if (context != null) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                    }
                }
            }, 1);
        }
    }

    /**
     *
     * 
     * @param acitvity
     */
    public static final void hideVirtualKeyPad(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * The method hide key pad.
     * 
     * @param context
     */
    public static final void hideVirtualKeyPad(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null && view.getWindowToken() != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * get device name
     * @return
     */
    public static String getDeviceName() {
        return Build.DEVICE;
    }

    /**
     * This method validates an email id. It checks whether only a single '@'
     * character is present in the email id and it is not the first and last
     * character of the email id. It checks whether atleast one '.' character is
     * present in the email id and it is not the first and the last character.
     * It also checks whether the following characters are not present in the
     * email id. '#','~','|',':',';','*','?','^','!'
     * 
     * @param email
     *            The String value of the email to be validated.
     * @return It returns true if the email is valid or returns false.
     */
    public final static boolean validateEmail(String email) {
        char[] specialCharacters = { '#', '~', '|', ':', ';', '*', '?', '^', '!', ' ', ',' };
        int firstIndexOfCharacter = email.indexOf("@");
        if (((firstIndexOfCharacter) > 0) && ((firstIndexOfCharacter) < (email.length() - 1))
                && (email.indexOf("@", firstIndexOfCharacter + 1) < 0)) {
            if (((email.indexOf(".")) > 0) && ((email.lastIndexOf('.')) < (email.length() - 1))
                    && (email.lastIndexOf('.') > firstIndexOfCharacter + 1)) {
                for (int i = 0; i < specialCharacters.length; i++) {
                    if ((email.indexOf(specialCharacters[i])) > 0) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * This method does the URL encoding for a String for the following
     * characters '%','$','&','<','>','?',';','#',':','=','\"','\'','~','+'
     * 
     * @param string
     *            The string to be URL encoded.
     * @return The URL encoded string of the original string.
     */
    public static String urlEncodeString(String string) {
        String tempString = string;
        char[] character = { '%', '$', '&', '<', '>', '?', ';', '#', ':', '=', ',', '\"', '\'', '~', '+', '@', ' ' };
        String[] replaceString = { "%25", "%24", "%26", "%3C", "%3E", "%3F", "%3B", "%23", "%3A", "%3D", "%2C", "%22",
                "%27", "%7E", "%2B", "%40", "%20" };
        StringBuffer tempStringBuffer = new StringBuffer(string);
        int[] index = new int[character.length];
        int i = 0;
        while (i < index.length) {
            index[i] = tempString.indexOf(character[i], index[i]);
            if (index[i] != -1) {
                tempStringBuffer.deleteCharAt(index[i]);
                tempStringBuffer.insert(index[i], replaceString[i]);
                tempString = tempStringBuffer.toString();
                index[i]++;
            } else {
                i++;
            }
        }
        return tempString;
    }

    /**
             * This method formats a String to be a part of an xml document by replacing 
             * the following character with their HTML codes
             * '&','{','%','"','=','<'
             * @param string String to be formatted
             * @return The formatted string of the original String.
             */
    public final static String formatString(String string) {
        String tempString = string;
        StringBuffer tempStringBuffer = new StringBuffer(string);
        char[] character = { '&', '<', '{', '%', '"', '=' };
        int[] index = new int[character.length];
        String[] replaceString = { "&#38;", "&#60;", "&#123;", "&#37;", "&#34;", "&#61;" };
        int i = 0;
        while (i < index.length) {

            index[i] = tempString.indexOf(character[i], index[i]);
            if (index[i] != -1) {
                tempStringBuffer.deleteCharAt(index[i]);
                tempStringBuffer.insert(index[i], replaceString[i]);
                tempString = tempStringBuffer.toString();
                index[i]++;
            } else {
                i++;
            }
        }
        return tempString;
    }

    /**
     * 
     * 
     * @return
     */
    public final static boolean validateTextLength(String text) {
        if (text != null && text.length() == 0) {
            return false;
        }
        return true;
    }

    public final static boolean validatePasswordField(String password) {
        boolean valid = false;
        if (password.length() == 0) {
            valid = false;
        } else if (!(hasMinimumCharectersForPasswordField(password))) {
            valid = false;
        } else {
            valid = true;
        }
        return valid;
    }

    public static boolean hasMinimumCharectersForPasswordField(String password) {
        if (password.length() < 3) {
            return false;
        }
        return true;
    }

    public static boolean validateBirthday(String trim) {
        String arr[] = trim.split("/");
        int year = 0, month = 0, day = 0;

        if (arr[0].length() == 2 && arr[1].length() == 2 && arr[2].length() == 4) {
            try {
                month = Integer.parseInt(arr[0]);
                day = Integer.parseInt(arr[1]);
                year = Integer.parseInt(arr[2]);

                Calendar nowTime = Calendar.getInstance();
                if ((nowTime.get(Calendar.YEAR) - 100) < year && year < (nowTime.get(Calendar.YEAR) - 1)) {
                    if (month >= 1 && month <= 12) {
                        if (month == 2) {
                            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                                if (day >= 1 && day <= 29) {
                                    return true;
                                }
                            } else if (day >= 1 && day <= 28) {
                                return true;
                            }
                        } else {
                            switch (month) {
                            case 1:
                            case 3:
                            case 5:
                            case 7:
                            case 8:
                            case 10:
                            case 12:
                                if (day >= 1 && day <= 31) {
                                    return true;
                                }
                                break;
                            default:
                                if (day >= 1 && day <= 30) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 
     * @param input
     * @return
     */
    public static final String EncodingZhToUTF(final String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    /**
     * 
     * @param content
     * @return
     */
    public final static  String encodingToHtml(String content)
	{
		StringBuilder result = new StringBuilder(content);
		// replacing
		int position = 0;
		while((position = result.indexOf("#")) != -1)
		{
			result.replace(position, position + 1, "%23");
		}
		position = 0;
		while((position = result.indexOf("%", position)) != -1)
		{
			result.replace(position, position + 1, "%25");
			position = position + 1;
		}
		position = 0;
		while((position = result.indexOf("\\")) != -1)
		{
			result.replace(position, position + 1, "%27");
		}
		position = 0;
		while((position = result.indexOf("?")) != -1)
		{
			result.replace(position, position + 1, "%3f");
		}
		return result.toString();
	}
    
   /**
    * 
    * @param bitmap
    * @param pixels
    * @param high
    * @return
    */
	public final static Bitmap createRoundCornerBitmap(Bitmap bitmap, float round,Config config) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), config);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, round, round, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
	/**
	 * create gray bitmap
	 * @param bmpOriginal
	 * @return
	 */
	public final static Bitmap createGrayscaleBitmap(Bitmap bmpOriginal) {
		int height = bmpOriginal.getHeight();
		int width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}
}
