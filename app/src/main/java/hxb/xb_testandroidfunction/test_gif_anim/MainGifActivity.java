package hxb.xb_testandroidfunction.test_gif_anim;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import hxb.xb_testandroidfunction.R;


public class MainGifActivity extends AppCompatActivity implements View.OnClickListener{
    private Button normal, advance, progress;
    Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_gif);

        normal = (Button) findViewById(R.id.normal_anim);
        advance = (Button) findViewById(R.id.advance_anim);
        progress = (Button) findViewById(R.id.progressbar_anim);

        normal.setOnClickListener(this);
        advance.setOnClickListener(this);
        progress.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.normal_anim:
                intent = new Intent(MainGifActivity.this, TestActivity.class);
                intent.putExtra("mode", 1);
                break;
            case R.id.advance_anim:
                intent = new Intent(MainGifActivity.this, TestActivity.class);
                intent.putExtra("mode", 2);
                break;
            case R.id.progressbar_anim:
                intent = new Intent(MainGifActivity.this, TestProgressbarActivity.class);
                break;
        }
        startActivity(intent);
    }
}
