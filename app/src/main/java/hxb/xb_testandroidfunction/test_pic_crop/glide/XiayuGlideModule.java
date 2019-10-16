package hxb.xb_testandroidfunction.test_pic_crop.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by hxb on 2019/4/17.
 * 自定义Glide Module
 */
public class XiayuGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
//        builder.setDiskCache();//自定义磁盘缓存
//
//        builder.setMemoryCache();//自定义内存缓存
//
//        builder.setBitmapPool(); //自定义图片池
//
//        builder.setDiskCacheService();//自定义本地缓存的线程池
//
//        builder.setResizeService();//自定义核心处理的线程池

//        builder.setDecodeFormat();//自定义图片质量

        builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565);

    }

    @Override
    public void registerComponents(Context context, Glide glide) {

    }
}
