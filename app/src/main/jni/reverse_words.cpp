//
// Created by hxb on 2018/10/31.
//

#include <stdio.h>
#include <jni.h>
#include <android/log.h>
#include <string>
#include "reverse_words.h"
using namespace std;
//extern是C，C++中的一个关键字。extern可置于变量或者函数前，
// 以表示变量或者函数的定义在别的文件中，提示编译器遇到此变量或函数时，
// 在其它模块中寻找其定义。另外，extern也可用来进行链接指定。

class ReverseWords {
  /**
   * 如果类对象写在a.cpp 文件中，b.cpp想使用a.cpp中的代码，
   * 就先在a.cpp中写一个类实现的方法,再在c.h文件中抽象该方法或是声明该方法，
   * b.cpp就include "c.h"， 注意a.cpp中也要include "c.h"，这样a.cpp中的方法才能暴露出去。
   *
   *
   * */
public:
    //这个是语句颠倒算法(如：i love you -- you love i)
    static string  reverseWords(string &s) {
        int storeIndex = 0, n = s.size();
        reverse(s.begin(), s.end());
        for (int i = 0; i < n; ++i) {
            if (s[i] != ' ') {
                if (storeIndex != 0) s[storeIndex++] = ' ';
                int j = i;
                while (j < n && s[j] != ' ') s[storeIndex++] = s[j++];
                reverse(s.begin() + storeIndex - (j - i), s.begin() + storeIndex);
                i = j;
            }
        }
        s.resize(storeIndex);
        return s;
    }

    static std::string jstring2str(JNIEnv* env, jstring jstr)
    {
        char*   rtn   =   NULL;
        jclass   clsstring   =   env->FindClass("java/lang/String");
        jstring   strencode   =   env->NewStringUTF("GB2312");
        jmethodID   mid   =   env->GetMethodID(clsstring,   "getBytes",  "(Ljava/lang/String;)[B");
        jbyteArray   barr=   (jbyteArray)env->CallObjectMethod(jstr,mid,strencode);
        jsize   alen   =   env->GetArrayLength(barr);
        jbyte*   ba   =   env->GetByteArrayElements(barr,JNI_FALSE);
        if(alen   >   0)
        {
            rtn   =   (char*)malloc(alen+1);
            memcpy(rtn,ba,alen);
            rtn[alen]=0;
        }
        env->ReleaseByteArrayElements(barr,ba,0);
        std::string stemp(rtn);
        free(rtn);
        return   stemp;
    }


    static char* jstringToChar(JNIEnv* env, jstring jstr) {
        char* rtn = NULL;
        jclass clsstring = env->FindClass("java/lang/String");
//    jstring strencode = env->NewStringUTF("GB2312");
        jstring strencode = env->NewStringUTF("utf-8");
        jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
        jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
        jsize alen = env->GetArrayLength(barr);
        jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
        if (alen > 0) {
            rtn = (char*) malloc(alen + 1);
            memcpy(rtn, ba, alen);
            rtn[alen] = 0;
        }
        env->ReleaseByteArrayElements(barr, ba, 0);
        return rtn;
    }


///////////////////这个是错误的////////////////////////
    jstring charTojstring(JNIEnv* env, const char* pat) {

        //定义java String类 strClass
        jclass strClass = (env)->FindClass("Ljava/lang/String;");
        //获取String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
        jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
        //建立byte数组
        jbyteArray bytes = (env)->NewByteArray(strlen(pat));
        //将char* 转换为byte数组
        (env)->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*) pat);
        // 设置String, 保存语言类型,用于byte数组转换至String时的参数
        jstring encoding = (env)->NewStringUTF("GB2312");
        //将byte数组转换为java String,并输出
        return (jstring) (env)->NewObject(strClass, ctorID, bytes, encoding);
    }
};





JNIEXPORT jstring JNICALL Java_hxb_xb_1testandroidfunction_test_1jni_NativeUtils_reverseWords(JNIEnv *env,jobject clazz,jstring s){

    char *chardata = ReverseWords::jstringToChar(env, s);
    __android_log_print(ANDROID_LOG_ERROR,"TAG","test:%s",chardata);
    std::string string1 = chardata;
    std::string string2 = ReverseWords::reverseWords(string1);
    chardata = const_cast<char *>(string2.data());
    __android_log_print(ANDROID_LOG_ERROR,"TAG","test2:%s",chardata);
    jstring str_j = env->NewStringUTF(chardata);//将c/c++中的string转成jstring(目前拥有这样的作用)


    return str_j;


//    return env->NewStringUTF("sfjsljlf jls ");//测试jni接口是否可用
}



