package com.cdct.cmdim.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;

import com.cdct.cmdim.R;
import com.cdct.cmdim.beans.ChatDialogueBean;
import com.cdct.cmdim.recording.PlayRecorderUtils;
import com.cdct.cmdim.utils.RoleUtils;
import com.cdct.cmdim.view.SelectableRoundedImageView;
//import com.cdct.library.utils.UserInfoCache;

/**
 * Created by hxb on 2017/10/31.
 * 聊天对话适配器
 * <p>
 * viewType:（1,2）文本左右布局
 * （3,4）图片左右布局
 * （5,6）。。。其它布局也是这样排列
 */

public class ChatDialogueAdapter extends RecyclerView.Adapter<ChatDialogueAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<ChatDialogueBean> mList;
//    private String headUrl = UserInfoCache.getUserInfo(UserInfoCache.CHZTX)+"";
//    private String headDocUrl = UserInfoCache.getUserInfo(UserInfoCache.CYSTX)+"";
    private String headUrl = "";
    private String headDocUrl = "";
    public PlayRecorderUtils mPlayRecorderUtils;

    public ChatDialogueAdapter(Context context) {
        this.mContext = context;
        if (mList == null) {
            mList = new ArrayList();
        }
        mPlayRecorderUtils = PlayRecorderUtils.init();
    }

    public void addChatDataList(ArrayList<ChatDialogueBean> list) {
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    public void addChatNum(ChatDialogueBean model) {
        this.mList.add(model);
        notifyDataSetChanged();
    }


    public void clearChatData() {
        this.mList.clear();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case RoleUtils.TEXT_LEFT:
                View view1 = LayoutInflater.from(mContext).inflate(R.layout.item_chat_dialogue_layout_left1, parent, false);
                return new MyViewHolder(view1, RoleUtils.TEXT_LEFT);
            case RoleUtils.TEXT_RIGHT:
                View view2 = LayoutInflater.from(mContext).inflate(R.layout.item_chat_dialogue_layout_right1, parent, false);
                return new MyViewHolder(view2, RoleUtils.TEXT_RIGHT);
            case RoleUtils.IMG_LEFT:
                View view3 = LayoutInflater.from(mContext).inflate(R.layout.item_chat_image_layout_left, parent, false);
                return new MyViewHolder(view3, RoleUtils.IMG_LEFT);
            case RoleUtils.IMG_RIGHT:
                View view4 = LayoutInflater.from(mContext).inflate(R.layout.item_chat_image_layout_right, parent, false);
                return new MyViewHolder(view4, RoleUtils.IMG_RIGHT);
            case RoleUtils.PLAN_LEFT:
                View view5 = LayoutInflater.from(mContext).inflate(R.layout.item_chat_plan_layout_left, parent, false);
                return new MyViewHolder(view5, RoleUtils.PLAN_LEFT);
            case RoleUtils.PLAN_RIGHT:
                View view6 = LayoutInflater.from(mContext).inflate(R.layout.item_chat_plan_layout_right, parent, false);
                return new MyViewHolder(view6, RoleUtils.PLAN_RIGHT);
            case RoleUtils.VOICE_LEFT:
                View view7 = LayoutInflater.from(mContext).inflate(R.layout.item_chat_voice_layout_left, parent, false);
                return new MyViewHolder(view7, RoleUtils.VOICE_LEFT);
            case RoleUtils.VOICE_RIGHT:
                View view8 = LayoutInflater.from(mContext).inflate(R.layout.item_chat_voice_layout_right, parent, false);
                return new MyViewHolder(view8, RoleUtils.VOICE_RIGHT);
        }
        return null;
    }

    public ArrayList<ChatDialogueBean> getmList() {
        return mList;
    }

    private String oldStr = "null";//默认

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        int itemType = getItemViewType(position);
        final ChatDialogueBean model = mList.get(position);
        final boolean[] is = {true};
        switch (itemType) {
            case RoleUtils.TEXT_LEFT:
                Glide.with(mContext).load(headUrl).placeholder(R.mipmap.ic_launcher).into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        holder.ivHeadPortrait.setImageDrawable(resource);
                    }
                });
                holder.tvContent.setText(model.textContent);
                break;
            case RoleUtils.TEXT_RIGHT:
                Glide.with(mContext).load(headDocUrl).placeholder(R.mipmap.ic_launcher).into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        holder.ivHeadPortrait.setImageDrawable(resource);
                    }
                });
                holder.tvContent.setText(model.textContent);
                break;
            case RoleUtils.IMG_LEFT:
                Log.e("TAG", "onBindViewHolder: "+model.bitmap );
                holder.ivImage.setImageBitmap(model.bitmap);
                Glide.with(mContext).load(headUrl).placeholder(R.mipmap.ic_launcher).into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        holder.ivHeadPortrait.setImageDrawable(resource);
                    }
                });
//                Glide.with(mContext).load(model.imageUrl).placeholder(R.mipmap.ic_launcher).override(100, 100).into(new SimpleTarget<GlideDrawable>() {
//                    @Override
//                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
//                        holder.ivImage.setImageDrawable(resource);
//                    }
//                });
                break;
            case RoleUtils.IMG_RIGHT:
                Glide.with(mContext).load(headDocUrl).placeholder(R.mipmap.ic_launcher).into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        holder.ivHeadPortrait.setImageDrawable(resource);
                    }
                });
                Glide.with(mContext).load(model.imageUrl).placeholder(R.mipmap.ic_launcher).override(100, 100).into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        holder.ivImage.setImageDrawable(resource);
                    }
                });
                break;

            case RoleUtils.PLAN_LEFT:
                Glide.with(mContext).load(headUrl).placeholder(R.mipmap.ic_launcher).into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        holder.ivHeadPortrait.setImageDrawable(resource);
                    }
                });
                holder.tvName.setText(model.getCYSMC());
                holder.tvTitle.setText(model.getCJKFABT());
                holder.tvTime.setText(model.getDCREATTIME());
                holder.tvContent.setText(model.getCJHJJ());
                break;
            case RoleUtils.PLAN_RIGHT:
                Glide.with(mContext).load(headDocUrl).placeholder(R.mipmap.ic_launcher).into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        holder.ivHeadPortrait.setImageDrawable(resource);
                    }
                });
                holder.tvName.setText(model.getCYSMC());
                holder.tvTitle.setText(model.getCJKFABT());
                holder.tvTime.setText(model.getDCREATTIME());
                holder.tvContent.setText(model.getCJHJJ());
                break;
            case RoleUtils.VOICE_LEFT:
                Glide.with(mContext).load(headUrl).placeholder(R.mipmap.ic_launcher).into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        holder.ivHeadPortrait.setImageDrawable(resource);
                    }
                });
//                holder.tvContent.setText(model.textContent);
                holder.tvContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newStr = model.voiceUrl;
                        if (oldStr.equals(newStr)) {
                            if (is[0] && PlayRecorderUtils.PlayerStatus != 1) {
                                mPlayRecorderUtils.startPlay(newStr);
                                is[0] = false;
                            } else {
                                mPlayRecorderUtils.stopPlayer();
                                is[0] = true;
                            }
                        } else {
                            mPlayRecorderUtils.startPlay(newStr);
                            is[0] = true;
                            oldStr = newStr;
                        }
                    }
                });
                break;
            case RoleUtils.VOICE_RIGHT:
                Glide.with(mContext).load(headDocUrl).placeholder(R.mipmap.ic_launcher).into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        holder.ivHeadPortrait.setImageDrawable(resource);
                    }
                });
                holder.tvContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newStr = model.voiceUrl;
                        if (oldStr.equals(newStr)) {
                            if (is[0] && PlayRecorderUtils.PlayerStatus != 1) {
                                mPlayRecorderUtils.startPlay(newStr);
                                is[0] = false;
                            } else {
                                mPlayRecorderUtils.stopPlayer();
                                is[0] = true;
                            }
                        } else {
                            mPlayRecorderUtils.startPlay(newStr);
                            is[0] = true;
                            oldStr = newStr;
                        }
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).itemType;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        SelectableRoundedImageView ivHeadPortrait, ivImage;
        TextView tvContent;
        TextView tvTitle;
        TextView tvTime;
        TextView tvName;

        public MyViewHolder(View itemView, int itemType) {
            super(itemView);
            switch (itemType) {
                case RoleUtils.TEXT_LEFT:
                case RoleUtils.TEXT_RIGHT:
                    ivHeadPortrait = itemView.findViewById(R.id.ivHeadPortrait);
                    tvContent = itemView.findViewById(R.id.tvContent);
                    break;
                case RoleUtils.IMG_LEFT:
                case RoleUtils.IMG_RIGHT:
                    ivHeadPortrait = itemView.findViewById(R.id.ivHeadPortrait);
                    ivImage = itemView.findViewById(R.id.ivImage);
                    break;
                case RoleUtils.PLAN_LEFT:
                case RoleUtils.PLAN_RIGHT:
                    ivHeadPortrait = itemView.findViewById(R.id.ivHeadPortrait);
                    tvContent = itemView.findViewById(R.id.tvContent);
                    tvTitle = itemView.findViewById(R.id.tvTitle);
                    tvTime = itemView.findViewById(R.id.tvTime);
                    tvName = itemView.findViewById(R.id.tvName);
                    break;
                case RoleUtils.VOICE_LEFT:
                case RoleUtils.VOICE_RIGHT:
                    ivHeadPortrait = itemView.findViewById(R.id.ivHeadPortrait);
                    tvContent = itemView.findViewById(R.id.tvContent);
                    break;
            }


        }
    }
}
