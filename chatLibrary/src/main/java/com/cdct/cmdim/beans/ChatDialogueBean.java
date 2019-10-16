package com.cdct.cmdim.beans;

import android.graphics.Bitmap;

import java.util.Arrays;

/**
 * Created by hxb on 2017/10/31.
 */

public class ChatDialogueBean {
    public int itemType;
    public String headPortrait;//头像
    public String textContent;//文本内容
    public String voiceUrl;//语音路劲
    public String imageUrl;//图片
    public byte [] file;//文件流
    public Bitmap bitmap ;
    //计划
    public String CJKFABT;
    public String CYSMC;
    public String CJHJJ;
    public String DCREATTIME;

    public String getCJKFABT() {
        return CJKFABT;
    }

    public void setCJKFABT(String CJKFABT) {
        this.CJKFABT = CJKFABT;
    }

    public String getCYSMC() {
        return CYSMC;
    }

    public void setCYSMC(String CYSMC) {
        this.CYSMC = CYSMC;
    }

    public String getCJHJJ() {
        return CJHJJ;
    }

    public void setCJHJJ(String CJHJJ) {
        this.CJHJJ = CJHJJ;
    }

    public String getDCREATTIME() {
        return DCREATTIME;
    }

    public void setDCREATTIME(String DCREATTIME) {
        this.DCREATTIME = DCREATTIME;
    }

    @Override
    public String toString() {
        return "ChatDialogueBean{" +
                "itemType=" + itemType +
                ", headPortrait='" + headPortrait + '\'' +
                ", textContent='" + textContent + '\'' +
                ", voiceUrl='" + voiceUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", file=" + Arrays.toString(file) +
                ", bitmap=" + bitmap +
                ", CJKFABT='" + CJKFABT + '\'' +
                ", CYSMC='" + CYSMC + '\'' +
                ", CJHJJ='" + CJHJJ + '\'' +
                ", DCREATTIME='" + DCREATTIME + '\'' +
                '}';
    }
}
