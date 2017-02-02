package samanasoft.android.ottimo.common;

import android.content.Context;

public class Convert {
	public static int DpToPixel(Context context, int val){
		float scale = context.getResources().getDisplayMetrics().density;
		int pixels = (int) (val * scale + 0.5f);
		return pixels;
	}
	public static int ObjectToInt(Object val){
		return Integer.valueOf((String) val);
	}
}
