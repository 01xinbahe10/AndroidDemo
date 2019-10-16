/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hxb.xb_testandroidfunction.test_jni;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.os.Bundle;

import java.util.ArrayList;


public class HelloJni extends Activity
{

    static {
        System.loadLibrary("hello-jni");
        System.loadLibrary("native_libs");
    }
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /* Create a TextView and set its content.
         * the text is retrieved by calling a native
         * function.
         */
        TextView  tv = new TextView(this);
        tv.setText( NativeUtils.reverseWords("你 是 谁"));
        setContentView(tv);


        String str_base64 = "A2pfCF9PUk0KAw";
//        String enToStr = new String(Base64.decode(str_base64.getBytes(), Base64.DEFAULT));
//        Log.e("TAG", "onCreate: ------------- "+enToStr );

        byte[] bytes = Base64.decode(str_base64.getBytes(), Base64.DEFAULT);
        String key = "7228877";
        key  = Codec.MD5(key);
        String decodeStr = NativeUtils.parseP2PID(this,bytes,key);
        Log.e("TAG", "onCreate: ------------- "+decodeStr );

    }

    /* A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */
    public native String  stringFromJNI();

    /* This is another native method declaration that is *not*
     * implemented by 'hello-jni'. This is simply to show that
     * you can declare as many native methods in your Java code
     * as you want, their implementation is searched in the
     * currently loaded native libraries only the first time
     * you call them.
     *
     * Trying to call this function will result in a
     * java.lang.UnsatisfiedLinkError exception !
     */
    public native String  unimplementedStringFromJNI();

    /* this is used to load the 'hello-jni' library on application
     * startup. The library has already been unpacked into
     * /data/data/com.example.hellojni/lib/libhello-jni.so at
     * installation time by the package manager.
     */

}
