package hxb.xb_testandroidfunction.test_chat;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cdct.cmdim.activity.ChatScreenActivity;


import hxb.xb_testandroidfunction.R;

/**
 * Created by hxb on 2018/4/24.
 */

public class MainChatActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        findViewById(R.id.btn1).setOnClickListener(this);
        imageView = findViewById(R.id.image);
        Glide.with(this).load(R.drawable.loading).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn1:
                startActivity(new Intent(MainChatActivity.this,ChatScreenActivity.class));
                break;
        }
    }



}
