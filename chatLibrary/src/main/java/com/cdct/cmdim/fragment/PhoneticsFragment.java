package com.cdct.cmdim.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cdct.cmdim.R;
import com.cdct.cmdim.beans.ChatDialogueBean;
import com.cdct.cmdim.contract.ChatScreenContract;
import com.cdct.cmdim.fragment_interface.FragmentInterface;
import com.cdct.cmdim.presenter.ChatScreenPresenter;
import com.cdct.cmdim.recording.AudioRecorderUtils;
import com.cdct.cmdim.recording.PopupWindowFactory;
import com.cdct.cmdim.recording.TimeUtils;
import com.cdct.cmdim.utils.RoleUtils;


/**
 * Created by hxb on 2017/10/30.
 * 语音
 */

public class PhoneticsFragment extends Fragment implements View.OnTouchListener ,ChatScreenContract.View{
    private FragmentInterface.PhoneticsInterface mPhoneticsInterface;
    private ChatScreenPresenter mPresenter;//操作类（Presenter）
    public static PhoneticsFragment initialize(FragmentInterface.PhoneticsInterface phoneticsInterface,View viewAnchor,ChatScreenPresenter mPresenter) {
        PhoneticsFragment fragment = new PhoneticsFragment();
        fragment.mPhoneticsInterface = phoneticsInterface;
        fragment.mViewAnchor = viewAnchor;
        fragment.mPresenter = mPresenter;
        return fragment;
    }

    static final int VOICE_REQUEST_CODE = 66;

    private View mViewMIC, mViewRecorderDelete;//录音按钮,删除按钮
    private int v_delete_x = 0, v_delete_y = 0;//删除按钮相对于全屏的位置
    private int mViewDeleteWidth = 0, mViewDeleteHeight = 0;//删除按钮的宽度
    private PopupWindowFactory mPop;//音量弹窗
    private View mViewAnchor;//弹窗的锚点
    private ImageView mImageView;//弹窗中的控件
    private TextView mTextView;
    private AudioRecorderUtils mAudioRecorderUtils;//录音


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_phonetics, container, false);
        mViewMIC = view.findViewById(R.id.viewMIC);
        mViewMIC.setOnTouchListener(this);
//        mViewMIC.setEnabled(false);
        mViewRecorderDelete = view.findViewById(R.id.viewRecorderDelete);
        mViewRecorderDelete.setSelected(false);

        //PopupWindow的布局文件
        final View popView = View.inflate(getContext(), R.layout.layout_microphone, null);
        mPop = new PopupWindowFactory(getContext(), popView);
        //PopupWindow布局文件里面的控件
        mImageView =  popView.findViewById(R.id.iv_recording_icon);
        mTextView =  popView.findViewById(R.id.tv_recording_time);

        mAudioRecorderUtils = new AudioRecorderUtils();
        recordBackCall();//录音回调
//        requestPermissions();//请求权限
        return view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == VOICE_REQUEST_CODE) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

                mViewMIC.setEnabled(true);

            } else {
                Toast.makeText(getContext(), "已拒绝权限！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * 开启扫描之前判断权限是否打开
     */
    private void requestPermissions() {
        //判断是否开启摄像头权限
        if ((ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {

            mViewMIC.setEnabled(true);//判断是否开启语音权限

        } else {
            //请求获取摄像头权限
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, VOICE_REQUEST_CODE);
        }

    }

    private boolean ifInPlace = false;//判断拖动时的坐标是否在删除按钮范围内，并实时检测
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v_delete_x == 0 && v_delete_y == 0) {
            int[] location = new int[2];
            mViewRecorderDelete.getLocationOnScreen(location);
            v_delete_x = location[0];
            v_delete_y = location[1];
            Log.d("------", "onTouch:          ");
            mViewDeleteWidth = mViewRecorderDelete.getWidth();
            mViewDeleteHeight = mViewRecorderDelete.getHeight();
        }

        float event_y = event.getRawY();//获取event相对于屏幕的坐标
        float event_x = event.getRawX();


        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                Log.e("按下", "onTouch: " );
                mPop.showAtLocation(mViewAnchor, Gravity.CENTER, 0, 0);
                mAudioRecorderUtils.startRecord();
                mViewMIC.setSelected(true);
//                Log.e("测试录音按钮的拖动坐标", "-------   X坐标：" + event_x + "  Y坐标：" + event_y);
                break;

            case MotionEvent.ACTION_MOVE:
                Log.e("移动", "onTouch: " );
//                Log.e("测试删除按钮坐标", "++++++++++++++  X坐标： " + v_delete_x + "  Y坐标：" + v_delete_y + "    宽度" + mViewDeleteWidth + "   高度" + mViewDeleteHeight);
//                Log.e("测试录音按钮的拖动坐标", "-------   X坐标：" + event_x + "  Y坐标：" + event_y);
                if (event_x > v_delete_x && event_x < v_delete_x + mViewDeleteWidth &&
                        event_y > v_delete_y && event_y < v_delete_y + mViewDeleteHeight) {
                    ifInPlace = true;
                    mViewRecorderDelete.setSelected(true);
                } else {
                    ifInPlace = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.e("抬起", "onTouch:  " );
                if (ifInPlace){
                    mAudioRecorderUtils.cancelRecord();    //取消录音（不保存录音文件）
                    Toast.makeText(getContext(), "录音取消",Toast.LENGTH_SHORT).show();
                }else {
                    mAudioRecorderUtils.stopRecord();        //结束录音（保存录音文件）
                }
                ifInPlace = false;
                mViewMIC.setSelected(ifInPlace);
                mViewRecorderDelete.setSelected(ifInPlace);
                mPop.dismiss();

                break;
            case MotionEvent.ACTION_CANCEL:
                Log.e("取消", "onTouch: ");
                if (ifInPlace){
                    mAudioRecorderUtils.cancelRecord();    //取消录音（不保存录音文件）
                    Toast.makeText(getContext(), "录音取消",Toast.LENGTH_SHORT).show();
                }else {
                    mAudioRecorderUtils.stopRecord();        //结束录音（保存录音文件）
                }
                ifInPlace = false;
                mViewMIC.setSelected(ifInPlace);
                mViewRecorderDelete.setSelected(ifInPlace);
                mPop.dismiss();
                break;

        }
        return true;
    }

    private void recordBackCall() {
        mAudioRecorderUtils.setOnAudioStatusUpdateListener(new AudioRecorderUtils.OnAudioStatusUpdateListener() {
            //录音中....db为声音分贝，time为录音时长
            @Override
            public void onUpdate(double db, long time) {
                mImageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
                mTextView.setText(TimeUtils.long2String(time));
            }

            @Override
            public void onStop(String filePath) {
                mTextView.setText(TimeUtils.long2String(0));
                mPhoneticsInterface.phonetics_recording(filePath);
                if (null != mFileCallBack){
                    mFileCallBack.fileCallBack(filePath);
                }
            }
        });
    }

//    @Override
//    public Object[] serverAddressConfig() {
//        return new Object[]{RoleUtils.ip, RoleUtils.port};
//    }

//    @Override
//    public Object senderLogin() {
//        return RoleUtils.sendUserId;
//    }
    private String phonetics = "";
    @Override
    public void msgSuccessStatus(Object message) {
        if (message instanceof Integer) {

        } else {
            // TODO: 2018/3/29 这里服务端需要返回用户是否连接服务成功的状态
            if (!TextUtils.isEmpty(phonetics)) {
                ChatDialogueBean model = new ChatDialogueBean();
                model.itemType = RoleUtils.VOICE_NET;//代表自己
                model.textContent = phonetics;
            }
        }
    }

    @Override
    public void receivedMsg(ChatDialogueBean bean) {

    }


    public interface FileCallBack{
        void fileCallBack(String filePath);
    }
    private FileCallBack mFileCallBack;

    public void setFileCallBack(FileCallBack fileCallBack) {
        this.mFileCallBack = fileCallBack;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFileCallBack = null;
    }
}
