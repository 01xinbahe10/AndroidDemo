package com.cdct.cmdim.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cdct.cmdim.R;
import com.cdct.cmdim.adapter.ChatDialogueAdapter;
import com.cdct.cmdim.beans.ChatDialogueBean;
import com.cdct.cmdim.contract.ChatScreenContract;
import com.cdct.cmdim.fragment.FunctionPanelFragment;
import com.cdct.cmdim.fragment.PhoneticsFragment;
import com.cdct.cmdim.fragment.PhraseFragment;
import com.cdct.cmdim.fragment_interface.FragmentInterface;
import com.cdct.cmdim.mina_net.ClientMina;
import com.cdct.cmdim.presenter.ChatScreenPresenter;
import com.cdct.cmdim.utils.FragmentPersistenceUtils;
import com.cdct.cmdim.utils.GetMp3Time;
import com.cdct.cmdim.utils.KeyBordDisplayUtils;
import com.cdct.cmdim.utils.PermissionUtils;
import com.cdct.cmdim.utils.RoleUtils;
import com.cdct.cmdim.view.ChatScreenView;
import com.cdct.cmdim.view.ListenerKeyBackEditTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import com.alibaba.android.arouter.launcher.ARouter;

/**
 * Created by hxb on 2017/10/30.
 * 聊天界面
 */

public class ChatScreenActivity extends FragmentActivity implements ChatScreenContract.View,
        FragmentInterface.PhoneticsInterface,
        FragmentInterface.FunctionPanelInterface,
        FragmentInterface.PhraseInterface,
        View.OnClickListener,
        TextWatcher,
        ChatScreenView.TheEvent {

    private ClientMina clientMina;
    private ChatScreenPresenter mPresenter;//操作类（Presenter）
    private ChatScreenView.Unregister mUnregister;
    private ChatScreenView mChatScreenView;

    private RecyclerView mChatRecyclerView;//聊天界面列表
    private ChatDialogueAdapter mChatAdapter;//聊天适配器
    private View mLlToolBar;//编辑工具栏
    private ListenerKeyBackEditTextView mEtChatContent;//文本编辑
    private TextView mTvSendMsg;//发送消息按钮
    private View mViewAddPic, mViewPhonetics;//发送图片和语音按钮
    private ArrayList<Fragment> mPanelList = new ArrayList<>();//各个面板的fragment
    private FragmentPersistenceUtils mFragmentPersistenceUtils;


    private MyHandler mMyHandler = new MyHandler(this);

    private static class MyHandler extends Handler {//优化的handler
        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference(context);
        }

        @Override
        public void handleMessage(Message msg) {
            ChatScreenActivity thiss = (ChatScreenActivity) reference.get();
            //11文本，21图片，22语音，31健康方案
            if (null != thiss) {
                switch (msg.what) {
                    case RoleUtils.TEXT_NET:
                        thiss.mChatAdapter.addChatNum((ChatDialogueBean) msg.obj);
                        thiss.mChatRecyclerView.scrollToPosition(thiss.mChatAdapter.getmList().size() - 1);
                        thiss.mEtChatContent.setText("");
                        break;
                    case RoleUtils.IMG_LEFT:
                    case RoleUtils.IMG_RIGHT:
                        thiss.mChatAdapter.addChatNum((ChatDialogueBean) msg.obj);
                        thiss.mChatRecyclerView.scrollToPosition(thiss.mChatAdapter.getmList().size() - 1);
                        break;
                    case RoleUtils.PLAN_NET:
                        thiss.mChatAdapter.addChatNum((ChatDialogueBean) msg.obj);
                        thiss.mChatRecyclerView.scrollToPosition(thiss.mChatAdapter.getmList().size() - 1);
                        break;
                    case RoleUtils.VOICE_RIGHT:
                    case RoleUtils.VOICE_LEFT:
                        thiss.mChatAdapter.addChatNum((ChatDialogueBean) msg.obj);
                        thiss.mChatRecyclerView.scrollToPosition(thiss.mChatAdapter.getmList().size() - 1);
                        break;
                    case RoleUtils.PLAN_RIGHT:
                    case RoleUtils.PLAN_LEFT:
                        thiss.mChatAdapter.addChatNum((ChatDialogueBean) msg.obj);
                        thiss.mChatRecyclerView.scrollToPosition(thiss.mChatAdapter.getmList().size() - 1);
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
//        mPresenter = new ChatScreenPresenter(this);
        clientMina = ClientMina.getIntrans();
        mPresenter = ChatScreenPresenter.getInstans(this);
//        clientMina = ClientMina.init();
//        clientMina = ClientMina.getIntrans();
        findViewById(R.id.tvImgBack).setOnClickListener(this);
        mLlToolBar = findViewById(R.id.llToolBar);
        mEtChatContent = findViewById(R.id.etChatContent);
        mTvSendMsg = findViewById(R.id.tvSendMsg);
        mViewAddPic = findViewById(R.id.viewAddPic);
        mViewPhonetics = findViewById(R.id.viewPhonetics);
        mEtChatContent.addTextChangedListener(this);
        mEtChatContent.setOnClickListener(this);
        mViewPhonetics.setOnClickListener(this);
        mViewAddPic.setOnClickListener(this);
        mTvSendMsg.setOnClickListener(this);
        mChatScreenView = ChatScreenView.initialize(this, this)
                .setParentPanel((LinearLayout) findViewById(R.id.llExpressionPanel), (LinearLayout) findViewById(R.id.llToolBar));
        mUnregister = mChatScreenView.followKeyboardDisplay(findViewById(R.id.rlViewAnchor));
        mChatScreenView.setTheEvent(this);
        initChatListView();//聊天对话列表
        initPanelFragment();
        initEditListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityCompat.requestPermissions(this, PermissionUtils.permissions, PermissionUtils.MY_PERMISSION_REQUEST_CODE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        KeyBordDisplayUtils.isShowSoftInput(mEtChatContent, this, 1);
        mChatScreenView.diePanel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mChatAdapter.mPlayRecorderUtils) {
            mChatAdapter.mPlayRecorderUtils.stopPlayer();
        }
        KeyBordDisplayUtils.isShowSoftInput(mEtChatContent, this, 1);
        mChatScreenView.diePanel();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMyHandler.removeCallbacksAndMessages(null);
        mChatScreenView.diePanel();
        KeyBordDisplayUtils.isShowSoftInput(mEtChatContent, this, 1);
        if (mUnregister != null) {
//            mUnregister.unregister();
        }
        if (mPresenter != null) {
//            mPresenter.closeClient();
        }

    }

    private String msgText;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvImgBack) {
            KeyBordDisplayUtils.isShowSoftInput(mEtChatContent, ChatScreenActivity.this, 1);//隐藏键盘
            finish();
        } else if (v.getId() == R.id.tvSendMsg) {//发送字符按钮
            msgText = mEtChatContent.getText().toString().trim();
            mPresenter.sendTextMsg(RoleUtils.reserverId, RoleUtils.TEXT_NET, "", msgText);
        } else if (v.getId() == R.id.viewPhonetics) {//语音
            if (PermissionUtils.checkPermissionAllGranted(this)) {
                mFragmentPersistenceUtils.switchFragment(0);
                mEtChatContent.requestFocus();
                mEtChatContent.setShowSoftInputOnFocus(true);
                mChatScreenView.showPanel(mLlToolBar, true);
                KeyBordDisplayUtils.isShowSoftInput(mEtChatContent, ChatScreenActivity.this, 1);
            } else {
                Toast.makeText(this, "请在手机设置中设置权限", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.viewAddPic) {//打开功能面板
            mFragmentPersistenceUtils.switchFragment(1);
            mChatScreenView.showPanel(mLlToolBar, true);
            KeyBordDisplayUtils.isShowSoftInput(mEtChatContent, ChatScreenActivity.this, 1);
        } else if (v.getId() == R.id.etChatContent) {//打开功能面板
            mChatScreenView.showPanel(mLlToolBar, false);
            //这里解决在显示面板时防止键盘隐藏和面板加载显现时的异步效果
            if (detectionKeyboardHeight()) {
                KeyBordDisplayUtils.isShowSoftInput(mEtChatContent, ChatScreenActivity.this, 2);//显示键盘
            }
        }
    }

    /**
     * 检测键盘是否测量
     */
    private boolean detectionKeyboardHeight() {
        SharedPreferences sharedPreferences = getSharedPreferences("key_bord", Context.MODE_PRIVATE);
        int keyBordHeight = sharedPreferences.getInt("height", 0);
        if (keyBordHeight == 0) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            return false;//未测量
        }
        return true;//已测量
    }


    /**
     * ChatScreenPresenter接口回调重写
     */
    @Override
    public void isKeyShow(boolean isKeyShow, int keyCode) {

        if (isKeyShow && ChatScreenView.KEY_CODE_DEF == keyCode) {


        } else if (ChatScreenView.KEY_CODE_DEF == keyCode) {
//            Log.e("测试键盘", "isKeyShow:   隐藏" );

        }
        //判断是否有第三方键盘点击了三方有自带的按钮关闭了键盘
        if (!isKeyShow && ChatScreenView.KEY_CODE_THIRD_PARTY == keyCode) {
            KeyBordDisplayUtils.isShowSoftInput(mEtChatContent, ChatScreenActivity.this, 1);
            mChatScreenView.diePanel();//不显示面板和键盘

        }

//        if (mChatScreenView.getLl_lp_parentPanel().height > 100){
//            //当面板升起的时候，将最新的一条消息滚动到键盘上方
//            Log.e("测试滚动位置", "isKeyShow: "+mChatRecyclerView.getScrollState()  );
//            mChatRecyclerView.scrollToPosition(mChatAdapter.getmList().size() - 1);
//        }
    }

    @Override
    public void keyboardMeasure(boolean isMeasure) {
        /**
         * detectionKeyboardHeight()方法检测，第一次是否获取了键盘高度，没有就在该方法中更改了键盘属性。
         * view.getViewTreeObserver().addOnGlobalLayoutListener（）就能监听到布局变化，从而完成测量，
         * 返回isMeasure == true 的值。
         *
         *　注意:　键盘属性一旦更为WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING，
         * view.getViewTreeObserver().addOnGlobalLayoutListener（）是不会有作用的）
         */
        if (isMeasure) {
            KeyBordDisplayUtils.isShowSoftInput(mEtChatContent, ChatScreenActivity.this, 2);
            mChatScreenView.showPanel(mEtChatContent, false);
        }
    }

    /**
     * Edit文本监听接口回调重写
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() != 0) {
            mViewAddPic.setVisibility(View.GONE);
            mTvSendMsg.setVisibility(View.VISIBLE);
        } else {
            mViewAddPic.setVisibility(View.VISIBLE);
            mTvSendMsg.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    //初始化面板界面
    private void initPanelFragment() {
        mPanelList.add(PhoneticsFragment.initialize(this, findViewById(R.id.rlViewAnchor), mPresenter));//录音面板
        mPanelList.add(FunctionPanelFragment.initialize(this, mPresenter));//功能面板
        mPanelList.add(PhraseFragment.initialize(this));//常用语
        mFragmentPersistenceUtils = FragmentPersistenceUtils.init(getSupportFragmentManager(), R.id.frameLayout, mPanelList);

    }

    //初始化聊天对话列表界面
    private void initChatListView() {
        mChatRecyclerView = findViewById(R.id.recyclerView);
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mChatAdapter = new ChatDialogueAdapter(this);
        mChatRecyclerView.setAdapter(mChatAdapter);
        //由于布局问题，所以RecyclerVIew的父布局也要监听onTouch事件
        findViewById(R.id.rlRVParent).setOnTouchListener(new View.OnTouchListener() {
            float down_y = 0f;
            boolean whetherToRepeat = false;//减少ACTION_MOVE中的方法调用次数（目前只调用一次）

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        down_y = event.getY();
                        break;
//                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_MOVE:
                        if (!whetherToRepeat && Math.abs(down_y - event.getY()) > 100f) {
                            KeyBordDisplayUtils.isShowSoftInput(mEtChatContent, ChatScreenActivity.this, 1);
                            mChatScreenView.diePanel();//不显示面板和键盘
                            whetherToRepeat = true;
//                            Log.e("测试是否拦截", "onTouch: 2" );
                        }
                        break;
                    case MotionEvent.ACTION_UP:
//                        Log.e("测试是否拦截", "onTouch: 3" );
                        whetherToRepeat = false;
                        break;
                }
                if (!whetherToRepeat && mChatScreenView.getLl_lp_parentPanel().height > 0) {
//                    Log.e("测试是否拦截", "onTouch: 1" );
                    return true;//自己消费（MotionEvent.ACTION_MOVE动作才会有）
                }
                return false;
            }
        });
        mChatRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            float down_y = 0f;

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        down_y = e.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //移动距离大于100时，拦截此次的移动事件给自己
                        if (Math.abs(down_y - e.getY()) > 100f && mChatScreenView.getLl_lp_parentPanel().height > 0) {
                            KeyBordDisplayUtils.isShowSoftInput(mEtChatContent, ChatScreenActivity.this, 1);
                            mChatScreenView.diePanel();//不显示面板和键盘
//                            Log.e("测试是否拦截", "onInterceptTouchEvent: " );
                            /** return true;
                             * 1,拦截（onTouchEvent中的MotionEvent.ACTION_MOVE动作才会有）
                             * 2,只执行一次，保证了键盘隐藏，面板关闭不会多次调用，节约性能
                             */
                            return true;
                        }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        Log.e("测试", "onTouchEvent:   DOWN");
                        break;
                    case MotionEvent.ACTION_UP:
//                        Log.e("测试", "onTouchEvent:    UP");
                        break;
                    case MotionEvent.ACTION_MOVE:
//                        Log.e("测试", "onTouchEvent:    MOVE");
                        break;
                }

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    //初始化Edit监听事件
    private void initEditListening() {
        mEtChatContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            //这是为了监听编辑框首次触发
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && v instanceof EditText) {
                    if (detectionKeyboardHeight()) {
                        KeyBordDisplayUtils.isShowSoftInput(mEtChatContent, ChatScreenActivity.this, 2);//显示键盘
                    }
                    mChatScreenView.showPanel(mLlToolBar, false);
                }
            }
        });

    }


//    @Override
//    public Object[] serverAddressConfig() {
//        //需要配置服务ip和端口号
//        return new Object[]{RoleUtils.ip, RoleUtils.port};
//    }


//    @Override
//    public Object senderLogin() {
//        //这里需要将发送者的ID交给服务器进行登陆注册
//        return RoleUtils.sendUserId;
//    }

    @Override
    public void msgSuccessStatus(Object message) {
        if (message instanceof Integer) {

        } else {
            // TODO: 2018/3/29 这里服务端需要返回用户是否连接服务成功的状态
            if (!TextUtils.isEmpty(msgText)) {
                ChatDialogueBean model = new ChatDialogueBean();
                model.itemType = RoleUtils.TEXT_RIGHT;//代表自己
                model.textContent = msgText;
                Message msg = mMyHandler.obtainMessage();
                msg.obj = model;
                msg.what = RoleUtils.TEXT_NET;
                mMyHandler.sendMessage(msg);
            }
        }
    }

    //相册路径
    private String IMAGE_PATH = "file:///storage/emulated/0/" + "CDCT_CHAT/";

    /**
     * 服务器返回消息
     *
     * @param bean
     */
    @Override
    public void receivedMsg(ChatDialogueBean bean) {
        String fileName;
        final Message msg =mMyHandler.obtainMessage();
        File file;
        switch (bean.itemType) {
            case RoleUtils.TEXT_LEFT:
                msg.obj = bean;
                msg.what = RoleUtils.TEXT_NET;
                mMyHandler.sendMessage(msg);
                break;
            case RoleUtils.IMG_LEFT:
                msg.obj = bean;
                msg.what = RoleUtils.IMG_LEFT;
                mMyHandler.sendMessage(msg);
                fileName = IMAGE_PATH + System.currentTimeMillis() + ".jpg";
                FileOutputStream fos = null;
                try {
                    file = new File(IMAGE_PATH, fileName);
                    fos = new FileOutputStream(file);
                    fos.write(bean.file, 0, bean.file.length);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case RoleUtils.VOICE_LEFT:
                writeSD(bean.file,RoleUtils.VOICE_LEFT,msg,bean);
                break;
            case RoleUtils.PLAN_LEFT:
                msg.obj = bean;
                msg.what = RoleUtils.PLAN_LEFT;
                mMyHandler.sendMessage(msg);
                break;
            default:
                break;
        }
    }

    public void writeSD(byte[] file,int type,Message msg,ChatDialogueBean bean) {
        boolean isMounted = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (isMounted) {
            try {
                File parent_path = Environment.getExternalStorageDirectory();
                // 可以建立一个子目录专门存放自己专属文件
                File dir = new File(parent_path.getAbsoluteFile(), "CDCT_CHAT");
                dir.mkdir();
                File files = new File(dir.getAbsoluteFile(), System.currentTimeMillis() + ".wav");
                Log.d("文件路径", files.getAbsolutePath());

                // 创建这个文件，如果不存在
                files.createNewFile();
                FileOutputStream fos = new FileOutputStream(files);
                byte[] buffer = file;
                // 开始写入数据到这个文件。
                fos.write(buffer, 0, buffer.length);
                fos.flush();
                fos.close();
                switch (type){
                    case RoleUtils.VOICE_LEFT:
                        bean.voiceUrl = files.getAbsolutePath();
                        msg = mMyHandler.obtainMessage();
                        msg.what = RoleUtils.VOICE_LEFT;
                        msg.obj = bean;
                        mMyHandler.sendMessage(msg);
                        break;
                    case RoleUtils.IMG_LEFT:

                }

                Log.d("receivedMsg", "成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("receivedMsg", "未安装SDCard！");
        }

    }

    /**
     * @param file 语音路劲
     */
    @Override
    public void phonetics_recording(String file) {
        File file1 = new File(file);
        mPresenter.sendVoiceMsg(RoleUtils.reserverId, RoleUtils.VOICE_NET, System.currentTimeMillis(), file1);
        String time = "";
        try {
            time = GetMp3Time.getAmrDuration(file1) - 19 + "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        ChatDialogueBean bean = new ChatDialogueBean();
        bean.voiceUrl = file;
        bean.textContent = time;
        bean.itemType = RoleUtils.VOICE_RIGHT;
        Message msg = mMyHandler.obtainMessage();
        msg.obj = bean;
        msg.what = RoleUtils.VOICE_RIGHT;
        mMyHandler.sendMessage(msg);
    }

    /**
     * @param file 图片路劲
     */
    @Override
    public void functionPanel_image(File file) {
        if (file != null) {
            Toast.makeText(this, "有数据", Toast.LENGTH_SHORT).show();
            ChatDialogueBean bean = new ChatDialogueBean();
            bean.itemType = RoleUtils.IMG_RIGHT;
            bean.imageUrl = file.getPath();
            Message msg = mMyHandler.obtainMessage();
            msg.obj = bean;
            msg.what = RoleUtils.IMG_RIGHT;
            mMyHandler.sendMessage(msg);
        }
    }

    public static final int HEALTHP_LAN = 0x001;//跳转健康管理方案

    /**
     * 健康计划
     */
    @Override
    public void functionPanel_plan() {
        //发送健康计划
//        ARouter.getInstance().build("/model_home/HealthManagerPlanListActivity").withString("type", "1").navigation(ChatScreenActivity.this, HEALTHP_LAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case HEALTHP_LAN:
                    String json = data.getStringExtra("json");
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(json);
                        ChatDialogueBean plan = new ChatDialogueBean();
                        plan.itemType = RoleUtils.PLAN_LEFT;//代表对方
                        plan.setCJHJJ(jsonObject.optString("CJHJJ"));
                        plan.setCJKFABT(jsonObject.optString("CJKFABT"));
                        plan.setCYSMC(jsonObject.optString("CYSMC"));
                        plan.setDCREATTIME(jsonObject.optString("DCREATTIME"));
                        plan.itemType = RoleUtils.PLAN_RIGHT;
                        Message msg = mMyHandler.obtainMessage();
                        msg.obj = plan;
                        msg.what = RoleUtils.PLAN_NET;
                        mMyHandler.sendMessage(msg);
                        mPresenter.sendPlanMsg(RoleUtils.reserverId, RoleUtils.PLAN_NET, "", json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void functionPanel_phrase() {
        mFragmentPersistenceUtils.switchFragment(2);
    }

    @Override
    public void phrase_language(String str) {
        mPresenter.sendTextMsg(RoleUtils.reserverId, RoleUtils.TEXT_NET, "", str);
        ChatDialogueBean model = new ChatDialogueBean();
        model.itemType = RoleUtils.TEXT_RIGHT;//代表自己
        model.textContent = str;
        Message msg = mMyHandler.obtainMessage();
        msg.obj = model;
        msg.what = RoleUtils.TEXT_NET;
        mMyHandler.sendMessage(msg);
    }
}
