package hxb.xb_testandroidfunction.test_language_change;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.Locale;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_language_change.utils.LanguageEnum;
import hxb.xb_testandroidfunction.test_language_change.utils.RestartAppUtils;


/**
 * Created by hxb on 2018/7/10.
 */

public class TestLanguageChangeActivity extends FragmentActivity implements View.OnClickListener {

    private SharedPreferences.Editor mEditor;
    private static Bundle mBundle;

    private SharedPreferences mSharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
        setContentView(R.layout.activity_test_language_chanage);

        mEditor = initEditor();
        mSharedPreferences = initSharedPreferences();
        if (mSharedPreferences.getInt("language",-1) == LanguageEnum.CHINA.getI()){
//            DisplayMetrics metrics = getResources().getDisplayMetrics();
//            Configuration configuration = getResources().getConfiguration();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                configuration.setLocale(LanguageEnum.CHINA.getLocale());
////                configuration.setLayoutDirection(LanguageEnum.CHINA.getLocale());
//            } else {
//                configuration.locale = LanguageEnum.CHINA.getLocale();
//            }
//            getResources().updateConfiguration(configuration, metrics);
            Log.e("TAG", "onCreate:----------------- china" );
        }else if (mSharedPreferences.getInt("language",-1) == LanguageEnum.US.getI()){
//            DisplayMetrics metrics = getResources().getDisplayMetrics();
//            Configuration configuration = getResources().getConfiguration();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                configuration.setLocale(LanguageEnum.US.getLocale());
////                configuration.setLayoutDirection(LanguageEnum.US.getLocale());
//            } else {
//                configuration.locale = LanguageEnum.US.getLocale();
//            }
//            getResources().updateConfiguration(configuration, metrics);
            Log.e("TAG", "onCreate:----------------- US" );

        }



        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
//        findViewById(R.id.btn3).setOnClickListener((v)->{
//            Toast.makeText(this,"测试该写法是否成功",Toast.LENGTH_SHORT).show();
//        });



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                changeAppLanguage(LanguageEnum.CHINA.getLocale());
                mEditor.putInt("language", LanguageEnum.CHINA.getI());
                mEditor.commit();
//                restartTheApp();
                break;
            case R.id.btn2:
                changeAppLanguage(LanguageEnum.US.getLocale());
                mEditor.putInt("language", LanguageEnum.US.getI());
                mEditor.commit();
//                restartTheApp();
                break;
        }
    }


    /**
     * 更改应用语言
     *
     * @param locale
     */
    public void changeAppLanguage(Locale locale) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Configuration configuration = getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        getResources().updateConfiguration(configuration, metrics);
        //方法1：重新启动Activity
//        Intent intent = new Intent(this, TestLanguageChangeActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
        //方法2：相当于上面的重新启动activity
//        recreate();//不过这个方法有闪屏的问题

        //方法3：
        startActivity(new Intent(this,TestLanguageChangeActivity.class));
        overridePendingTransition(R.anim.anim_preview_pic_enter,R.anim.anim_preview_pic_exit);
        finish();

        test(1|2);

    }
    /**
     * 重启app
     * */
    private void restartTheApp(){
        //重启app
        RestartAppUtils.restartAPP(this);
    }



    private SharedPreferences.Editor initEditor(){
        SharedPreferences sharedPreferences = getSharedPreferences("locale", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        return editor;
    }

    private SharedPreferences initSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("locale", Context.MODE_PRIVATE);
//        String username = sharedPreferences.getString("username", "");
//        int password = sharedPreferences.getInt("password", 0);
        return sharedPreferences;
    }

    private void test(int i){
        Log.e("TAG", "test: oooooooooooooooo  "+i );
    }
}
