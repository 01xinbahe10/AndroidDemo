package hxb.xb_testandroidfunction.test_language_change.utils;

import java.util.Locale;

/**
 * Created by hxb on 2018/7/10.
 */

public enum LanguageEnum {
    CHINA(1,Locale.CHINA),US(2,Locale.US);
    private int i;
    private Locale locale;


    LanguageEnum(int i, Locale locale) {
        this.i = i;
        this.locale = locale;
    }




    public void setI(int i) {
        this.i = i;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public int getI() {

        return i;
    }

    public Locale getLocale() {
        return locale;
    }



}
