package com.cdct.cmdim.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cdct.cmdim.R;
import com.cdct.cmdim.adapter.PhraseAdapter;
import com.cdct.cmdim.beans.ChatDialogueBean;
import com.cdct.cmdim.contract.ChatScreenContract;
import com.cdct.cmdim.fragment_interface.FragmentInterface;
//import com.cdct.library.api.UriUtils;
//import com.cdct.library.okhttp.callback.GenericsCallback;
//import com.cdct.library.okhttp.utils.JsonGenericsSerializator;
//import com.cdct.library.okhttp.utils.OkHttpUtils;
//import com.cdct.library.utils.UserInfoCache;
//import com.cdct.library.utils.toast.ResultUtils;


//import okhttp3.Call;

/**
 * Created by hxb on 2018/3/30.
 * 常用语
 */

public class PhraseFragment extends Fragment implements PhraseAdapter.OnItemClickListener ,ChatScreenContract.View{
    private FragmentInterface.PhraseInterface mPhraseInterface;
    public static PhraseFragment initialize(FragmentInterface.PhraseInterface phraseInterface) {
        PhraseFragment fragment = new PhraseFragment();
        fragment.mPhraseInterface = phraseInterface;
        return fragment;
    }

    private ListView mListView;
    private PhraseAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phrase,container,false);
        mListView = view.findViewById(R.id.listView);

        initData();
        return view;
    }

    private void initData() {
//        HashMap<Object,Object>hashMap = new HashMap<>();
//        hashMap.put("MSH.1","docCenter");
//        hashMap.put("MSH.2","selCommonWords");
//        hashMap.put("CYSBM",UserInfoCache.getUserInfo(UserInfoCache.CYSBM));
//        hashMap.put("pageSize","");
//        hashMap.put("pageNum","");
//        OkHttpUtils.postString()
//                .url(UriUtils.FUNCTION_API+ UserInfoCache.getUserInfo(UserInfoCache.TOKEN))
//                .content(new JSONObject(hashMap).toString())
//                .build()
//                .execute(new GenericsCallback<CommonLanguageBean>(new JsonGenericsSerializator()) {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//
//                    }
//                    @Override
//                    public void onResponse(CommonLanguageBean response, int id) {
//                        if (ResultUtils.SUCCESS.equals(response.getCode())){
//                            mAdapter = new PhraseAdapter(getActivity(),response);
//                            mListView.setAdapter(mAdapter);
//                            mAdapter.setOnItemClickListener(PhraseFragment.this);
//                        }else {
//
//                        }
//
//                    }
//                });
//

    }

    @Override
    public void onItemClick(View view, int position, String content) {
        mPhraseInterface.phrase_language(content);
    }

//    @Override
//    public Object[] serverAddressConfig() {
//        return new Object[]{RoleUtils.ip, RoleUtils.port};
//    }

//    @Override
//    public Object senderLogin() {
//        return RoleUtils.sendUserId;
//    }

    @Override
    public void msgSuccessStatus(Object message) {

    }

    @Override
    public void receivedMsg(ChatDialogueBean bean) {

    }
}
