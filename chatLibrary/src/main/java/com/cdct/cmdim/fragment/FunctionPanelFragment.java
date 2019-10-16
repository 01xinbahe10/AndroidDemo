package com.cdct.cmdim.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cdct.cmdim.R;
import com.cdct.cmdim.beans.ChatDialogueBean;
import com.cdct.cmdim.contract.ChatScreenContract;
import com.cdct.cmdim.fragment_interface.FragmentInterface;
import com.cdct.cmdim.presenter.ChatScreenPresenter;
import com.cdct.cmdim.utils.PermissionUtils;
import com.cdct.cmdim.utils.RoleUtils;

/**
 * Created by hxb on 2018/3/30.
 * 包含图片，健康管理方案，常用语
 */

public class FunctionPanelFragment extends Fragment implements View.OnClickListener,ChatScreenContract.View {
    private FragmentInterface.FunctionPanelInterface mFunctionPanelInterface;
    private String imageUrl = "";
    private ChatScreenPresenter mPresenter;//操作类（Presenter）

    public static FunctionPanelFragment initialize(FragmentInterface.FunctionPanelInterface functionPanelInterface,ChatScreenPresenter mPresenter) {
        FunctionPanelFragment fragment = new FunctionPanelFragment();
        fragment.mFunctionPanelInterface = functionPanelInterface;
        fragment.mPresenter = mPresenter;
        return fragment;
    }


    private String path; //sd卡路径
    private String takePictures_photoPath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_function_panel, container, false);
        view.findViewById(R.id.llImage).setOnClickListener(this);
        view.findViewById(R.id.llPlan).setOnClickListener(this);
        view.findViewById(R.id.llPhrase).setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100://拍照
                if (resultCode == getActivity().RESULT_OK) {
                    if (takePictures_photoPath != null) {
                        //URL框架加载本地sd卡图片路径为 String imageFilePath = "file://" + image_from_sd_paizhao;
                        File file = new File(takePictures_photoPath);
                        mFunctionPanelInterface.functionPanel_image(file);
                        mPresenter.sendPicMsg(RoleUtils.reserverId,RoleUtils.IMG_NET,"image",file);
                    }
                } else {
                    Toast.makeText(getContext(), "放弃拍照", Toast.LENGTH_SHORT).show();
                }
                break;
            case 200://相册
                if (resultCode == getActivity().RESULT_OK){
                    //内容解析者来操作内容提供最对数据的4方法
                    if (null != data) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                            //选择的就只是一张图片，所以cursor只有一条记录
                            if (cursor != null) {
                                if (cursor.moveToFirst()) {
                                    //获取相册路径字段
                                    takePictures_photoPath = cursor.getString(cursor.getColumnIndex("_data"));
                                    File file = new File(takePictures_photoPath);
                                    mPresenter.sendPicMsg(RoleUtils.reserverId,RoleUtils.IMG_NET,System.currentTimeMillis(),file);
                                    mFunctionPanelInterface.functionPanel_image(file);
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "放弃从相册选择照片", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.llImage){//图片选取
            if (PermissionUtils.checkPermissionAllGranted(getContext())) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("请选择")
                        .setNegativeButton("相册", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                xiangCe(200);
                            }
                        })
                        .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                paiZhao(100);
                            }
                        })
                        .show();
            } else {
                Toast.makeText(getContext(), "请在手机设置中设置权限", Toast.LENGTH_SHORT).show();
            }
        }else if (v.getId()==R.id.llPlan){
            mFunctionPanelInterface.functionPanel_plan();
        }else if (v.getId()==R.id.llPhrase){
            mFunctionPanelInterface.functionPanel_phrase();
        }

    }


    private void xiangCe(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    private void paiZhao(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        path = Environment.getExternalStorageDirectory().getPath() + "/";
        //将当前的拍照时间作为图片的文件名称
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String filename = simpleDateFormat.format(new Date()) + ".jpg";
        takePictures_photoPath = path + filename;
        File file = new File(takePictures_photoPath);
        //******************************************************************
        Uri photoURI;
        //解决三星7.x或其他7.x系列的手机拍照失败或应用崩溃的bug.解决4.2.2(oppo)/4.x系统拍照执行不到设置显示图片的bug
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) { //7.0以下的系统用 Uri.fromFile(file)
            photoURI = Uri.fromFile(file);
        } else {                                            //7.0以上的系统用下面这种方案
            photoURI = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", file);
        }
        //******************************************************************
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);//将图片文件转化为一个uri传入
        startActivityForResult(intent, requestCode);
    }

    /**
     * 服务器地址
     * @return
     */
//    @Override
//    public Object[] serverAddressConfig() {
//        return new Object[]{RoleUtils.ip, RoleUtils.port};
//    }

    /**
     * 发送用户ID
     * @return
     */
//    @Override
//    public Object senderLogin() {
//        return RoleUtils.sendUserId;
//    }

    /**
     * 发送消息
     * @param message
     */
    @Override
    public void msgSuccessStatus(Object message) {
        if (message instanceof Integer) {

        } else {
            // TODO: 2018/3/29 这里服务端需要返回用户是否连接服务成功的状态
            if (!TextUtils.isEmpty(imageUrl)) {
                ChatDialogueBean model = new ChatDialogueBean();
                model.itemType = RoleUtils.IMG_NET;//代表自己
                model.textContent = imageUrl;
            }
        }

    }

    /**
     * 消息返回
     * @param bean
     */
    @Override
    public void receivedMsg(ChatDialogueBean bean) {

    }
}
