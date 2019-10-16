//
// Created by hxb on 2018/11/2.
//
#include <stdio.h>
#include <string>
#include <jni.h>
#include "reverse_words.h"
using namespace std;

//废弃：字符0  和 数字0相互转的时候可能出错
//JNIEXPORT jstring JNICALL Java_hxb_xb_1testandroidfunction_test_1jni_NativeUtils_parseP2PID(JNIEnv *env, jclass clazz, jobject content,
//                                               jstring id, jstring rid) {
//    if (id) {
//#if 0
//        jclass cls = env->GetObjectClass(context);
//        jmethodID mid = env->GetMethodID(cls, "getPackageName", "()Ljava/lang/String;");
//        jstring packName = static_cast<jstring>(env->CallObjectMethod(context, mid));
//        char* pkg = (char *) env->GetStringUTFChars(packName, 0);
//        int ret = strcmp(pkg, "com.robelf.haze");
//        env->ReleaseStringUTFChars(packName, pkg);
//        if(ret != 0)
//            return 0;
//#endif
//        char *pId = (char *) env->GetStringUTFChars(id, 0);
//        int len = strlen(pId);
//        char *pRid = (char *) env->GetStringUTFChars(rid, 0);
//        int rlen = strlen(pRid);
//
//        char out[16];
//        int first = (unsigned char) *pId;
//        out[0] = (char) ((unsigned char) *pId ^ (unsigned char) *pRid);
//        for (int i = 1; i < len; i++) {
//            int j = (i + first) % rlen;
//            out[i] = (char) ((unsigned char) *(pId + i) ^ (unsigned char) *(pRid + j));
//        }
//        out[len] = 0;
//        env->ReleaseStringUTFChars(id, pId);
//        env->ReleaseStringUTFChars(rid, pRid);
//        return env->NewStringUTF(out);
//    }
//    return 0;
//}


JNIEXPORT jstring JNICALL Java_hxb_xb_1testandroidfunction_test_1jni_NativeUtils_parseP2PID(JNIEnv *env, jclass clazz, jobject content,
                                                                                            jbyteArray raw, jstring rid) {
    if (raw) {
#if 0
        jclass cls = env->GetObjectClass(context);
        jmethodID mid = env->GetMethodID(cls, "getPackageName", "()Ljava/lang/String;");
        jstring packName = static_cast<jstring>(env->CallObjectMethod(context, mid));
        char* pkg = (char *) env->GetStringUTFChars(packName, 0);
        int ret = strcmp(pkg, "com.robelf.haze");
        env->ReleaseStringUTFChars(packName, pkg);
        if(ret != 0)
            return 0;
#endif
        jbyte *pId = env->GetByteArrayElements(raw,0);
        int len = env->GetArrayLength(raw);
        char *pRid = (char *) env->GetStringUTFChars(rid, 0);
        int rlen = strlen(pRid);

        char out[16];
        int first = (unsigned char) *pId;
        out[0] = (char) ((unsigned char) *pId ^ (unsigned char) *pRid);
        for (int i = 1; i < len; i++) {
            int j = (i + first) % rlen;
            out[i] = (char) ((unsigned char) *(pId + i) ^ (unsigned char) *(pRid + j));
//            __android_log_print(ANDROID_LOG_ERROR, "parseP2PID", "%02x %c = %02x ^ %02x\n", (unsigned char)out[i], out[i], (unsigned char) *(pId + i), (unsigned char) *(pRid + j));
        }
        out[len] = 0;
        env->ReleaseByteArrayElements(raw, pId, 0);
        env->ReleaseStringUTFChars(rid, pRid);
        return env->NewStringUTF(out);
    }
    return 0;
}

