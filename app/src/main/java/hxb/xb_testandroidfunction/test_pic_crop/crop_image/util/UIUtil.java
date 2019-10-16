package hxb.xb_testandroidfunction.test_pic_crop.crop_image.util;

import android.content.Context;
import androidx.annotation.Nullable;

/**
 * Created by mac on 17/12/21.
 */

public class UIUtil {
    public static int dip2px(@Nullable Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
