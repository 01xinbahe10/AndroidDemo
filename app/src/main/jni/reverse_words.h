//
// Created by DELL on 2018/10/31.
//

#ifndef FRISTPROJECT_REVERSE_WORDS_H
#define FRISTPROJECT_REVERSE_WORDS_H

#include <iostream>
#include <string>
#include <algorithm>
#include <jni.h>

using namespace std;

#ifdef __cplusplus
extern "C" {
#endif


JNIEXPORT jstring JNICALL Java_hxb_xb_1testandroidfunction_test_1jni_NativeUtils_reverseWords(JNIEnv *env,jobject clazz,jstring s);
JNIEXPORT jstring JNICALL Java_hxb_xb_1testandroidfunction_test_1jni_NativeUtils_parseP2PID(JNIEnv *env, jclass clazz, jobject content, jbyteArray raw, jstring rid);



#ifdef __cplusplus
}
#endif

#endif //FRISTPROJECT_REVERSE_WORDS_H
