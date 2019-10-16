package hxb.xb_testandroidfunction.test_web;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_web.web.AdvancedWebView;

/**
 * Created by hxb on 2018/6/13.
 *
 */

public class TestWebActivity extends FragmentActivity implements AdvancedWebView.Listener{
    private AdvancedWebView mWebView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_web);
        mWebView = findViewById(R.id.webView);

        mWebView.setListener(this, this);
        mWebView.setGeolocationEnabled(false);
        mWebView.setMixedContentAllowed(true);
        mWebView.setCookiesEnabled(true);
        mWebView.setThirdPartyCookiesEnabled(true);

        mWebView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Toast.makeText(TestWebActivity.this, "Finished loading", Toast.LENGTH_SHORT).show();
            }
        });


        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Toast.makeText(TestWebActivity.this, title, Toast.LENGTH_SHORT).show();
            }
        });

//        mWebView.loadUrl();
        String info1 = "[{\"NAME\": \"未完成\", \"VALUE\": 12 }, {\"NAME\": \"已完成\", \"VALUE\": 30 }, {\"NAME\": \"超时完成\", \"VALUE\": 15 }, {\"NAME\": \"已取消\", \"VALUE\": 43 } ]";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mWebView.getSettings().setAllowFileAccess(true);
//            mWebView.loadUrl("file:///android_asset/statistics/workplan.html");
            mWebView.loadUrl("file:///android_asset/statistics/workplan.html:javascript:workplan('" + info1 + "')");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mWebView.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onBackPressed() {
        if (!mWebView.onBackPressed()) { return; }
        super.onBackPressed();

    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        mWebView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPageFinished(String url) {
        mWebView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        Toast.makeText(TestWebActivity.this, "onPageError(errorCode = "+errorCode+",  description = "+description+",  failingUrl = "+failingUrl+")", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
        Toast.makeText(TestWebActivity.this, "onDownloadRequested(url = "+url+",  suggestedFilename = "+suggestedFilename+",  mimeType = "+mimeType+",  contentLength = "+contentLength+",  contentDisposition = "+contentDisposition+",  userAgent = "+userAgent+")", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onExternalPageRequest(String url) {
        Toast.makeText(TestWebActivity.this, "onExternalPageRequest(url = "+url+")", Toast.LENGTH_SHORT).show();
    }
}
