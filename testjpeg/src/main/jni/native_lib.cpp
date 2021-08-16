//
// Created by 1614526981 on 2019-07-08.
//

#include <jni.h>
#include <string>

#include <android/log.h>
#include <android/bitmap.h>
#include <malloc.h>

extern "C"{
#include "jpeglib.h"
}
#define LOGE(...)__android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define LOG_TAG "mineJpegTest"
#define true 1
typedef uint8_t BYTE;

void writeImg(BYTE *data, const char *path,int w,int h){
    //调用jpeg函数
    LOGE("  xxxxxxxxxxxxxx  -------------------4444444444444444444");
    //初始化
    struct jpeg_compress_struct jpeg_struct;
    //设置错误处理信息
    jpeg_error_mgr err;
    jpeg_struct.err = jpeg_std_error(&err);
    LOGE("  xxxxxxxxxxxxxx  -------------------5555555555555555555");
    //给结构体分配内存
    jpeg_create_compress(&jpeg_struct);

    FILE *file = fopen(path,"wb");
    if (!file){
        LOGE("  file open fail  ");
    }
    jpeg_stdio_dest(&jpeg_struct,file);

    jpeg_struct.image_height = (JDIMENSION)h;
    jpeg_struct.image_width = (JDIMENSION)w;
    //核心  打开鲁班压缩
    jpeg_struct.arith_code = FALSE;
    jpeg_struct.optimize_coding = TRUE;//优化编码
    jpeg_struct.in_color_space = JCS_RGB;//图片格式
    jpeg_struct.input_components = 3;//单个像素颜色组成个数
    //其它默认压缩
    jpeg_set_defaults(&jpeg_struct);
    jpeg_set_quality(&jpeg_struct,10,TRUE);//配置，压缩质量(10-100),压缩队列

    //开始压缩
    jpeg_start_compress(&jpeg_struct,TRUE);
    JSAMPROW row_pointer[1];//每一行的行脚标
    while (jpeg_struct.next_scanline < h){
        //无符号字节
        row_pointer[0] = &data[jpeg_struct.next_scanline*w*3];
        jpeg_write_scanlines(&jpeg_struct,row_pointer,1);
    }
    //结束压缩
    jpeg_finish_compress(&jpeg_struct);
    jpeg_destroy_compress(&jpeg_struct);
    fclose(file);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_testjpeg_MineJni_compress(JNIEnv *env, jclass instance, jobject bitmap,
                                                jstring path_) {
    const char *path = env->GetStringUTFChars(path_, 0);
    LOGE("  xxxxxxxxxxxxxx  -------------------1111111111111");
    //图片 解析成一组像素数组 w*h
    AndroidBitmapInfo bitmapInfo;
    AndroidBitmap_getInfo(env, bitmap, &bitmapInfo);
    BYTE *pixels;
    AndroidBitmap_lockPixels(env, bitmap, (void **) &pixels);
    int h = bitmapInfo.height;
    int w = bitmapInfo.width;

    BYTE *data, *tmpData;
    data = (BYTE *) malloc(w * h * 3);
    tmpData = data;
    BYTE r, g, b;
    int color;

    //遍历每一个像素点 将rgb 提取出来
    //jpeg opencv b g r
    //数组 w*h*3 [b g r b g r]

    for (int i = 0; i < h; ++i) {
        for (int j = 0; j < w; ++j) {
            color = *((int *) pixels);
            r = ((color & 0x00FF0000) >> 16);
            g = ((color & 0x0000FF00) >> 8);
            b = ((color & 0x000000FF));
            *data = b;
            *(data + 1) = g;
            *(data + 2) = r;
            data += 3;
            pixels += 4;
        }
    }
    LOGE("  xxxxxxxxxxxxxx  -------------------222222222222222222");
    writeImg(tmpData,path,w,h);
    LOGE("  xxxxxxxxxxxxxx  -------------------33333333333333333");
    env->ReleaseStringUTFChars(path_, path);
}