package hxb.xb_testandroidfunction.test_different_display.presentation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import hxb.xb_testandroidfunction.R;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

    public static final int ITEM_MOVIE_LIST = 1;
    public static final int ITEM_APP_LIST = 2;

    @IntDef({ITEM_MOVIE_LIST, ITEM_APP_LIST})
    @Retention(RetentionPolicy.CLASS)
    @interface item_type {
    }

    private Context context;
    private int itemType = -1;

    private List<VideoVO.MovieListVO> movieListVOS;
    private List<VideoVO.AppListVO> appListVOS;

    public VideoAdapter(Context context, @item_type int itemType) {
        this.context = context;
        this.itemType = itemType;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (itemType) {
            case ITEM_MOVIE_LIST:
                View view1 = LayoutInflater.from(context).inflate(R.layout.item_presentation_video_list, parent, false);
                return new MyViewHolder(view1);
            case ITEM_APP_LIST:
                View view2 = LayoutInflater.from(context).inflate(R.layout.item_presentation_video_app, parent, false);
                return new MyViewHolder(view2);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        switch (itemType) {
            case ITEM_MOVIE_LIST:
                VideoVO.MovieListVO movieListVO = movieListVOS.get(position);
                holder.tvMovieTitle.setText(movieListVO.getMovieTitle());
                holder.tvMovieIntroduction.setText(movieListVO.getMovieIntroduction());
                break;
            case ITEM_APP_LIST:
                VideoVO.AppListVO appListVO = appListVOS.get(position);
                Log.e("TAG", "onBindViewHolder: =========  "+appListVO.getAppIcon() );
                holder.ivAppIcon.setImageResource(appListVO.getAppIcon());
                break;
        }
    }

    @Override
    public int getItemCount() {
        switch (itemType) {
            case ITEM_MOVIE_LIST:
                return null == movieListVOS ? 0 : movieListVOS.size();
            case ITEM_APP_LIST:
                return null == appListVOS ? 0 : appListVOS.size();
        }
        return 0;
    }


    public void addMovieList(List<VideoVO.MovieListVO> list) {

        if (null == movieListVOS) {
            movieListVOS = new ArrayList<>();
        } else {
            movieListVOS.clear();
        }

        movieListVOS.addAll(list);
        refresh();
    }

    public void addAppList(List<VideoVO.AppListVO> list) {
        if (null == appListVOS) {
            appListVOS = new ArrayList<>();
        } else {
            appListVOS.clear();
        }

        appListVOS.addAll(list);
        refresh();
    }


    public List<VideoVO.MovieListVO> getMovieListVOS() {
        return movieListVOS;
    }

    public List<VideoVO.AppListVO> getAppListVOS() {
        return appListVOS;
    }

    public void refresh() {
        notifyDataSetChanged();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        View llMovieItemParent,llAppItemParent;
        TextView tvMovieTitle, tvMovieIntroduction;
        ImageView ivAppIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            switch (itemType) {
                case ITEM_MOVIE_LIST:
                    llMovieItemParent = itemView.findViewById(R.id.llMovieItemParent);
                    tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
                    tvMovieIntroduction = itemView.findViewById(R.id.tvMovieIntroduction);
                    break;
                case ITEM_APP_LIST:
                    llAppItemParent = itemView.findViewById(R.id.llAppItemParent);
                    ivAppIcon = itemView.findViewById(R.id.ivAppIcon);
                    break;
            }
        }
    }
}
