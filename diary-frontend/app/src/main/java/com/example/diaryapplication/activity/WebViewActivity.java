package com.example.diaryapplication.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.diaryapplication.R;
import com.example.diaryapplication.service.SharedPreferences.PreferenceManager;

public class WebViewActivity extends AppCompatActivity {
    private WebView myWebview;
    private WebSettings myWebSettings;
    public static final String INTENT_PROTOCOL_START = "intent:";
    public static final String INTENT_PROTOCOL_INTENT = "#Intent;";
    public static final String INTENT_PROTOCOL_END = ";end;";
    public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);


        myWebview = (WebView) findViewById(R.id.webView);
        myWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                String token = PreferenceManager.getString(getApplicationContext(), "ACCESS_TOKEN");
                LoginTokenSend(token);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(INTENT_PROTOCOL_START)) {
                    final int customUrlStartIndex = INTENT_PROTOCOL_START.length();
                    final int customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT);
                    if (customUrlEndIndex < 0) {
                        return false;
                    } else {
                        final String customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);
                        try {
                            getApplicationContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(customUrl)));
                        } catch (ActivityNotFoundException e) {
                            final int packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length();
                            final int packageEndIndex = url.indexOf(INTENT_PROTOCOL_END);

                            final String packageName = url.substring(packageStartIndex, packageEndIndex < 0 ? url.length() : packageEndIndex);
                            getApplicationContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)));
                        }
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }); // 새 창 띄우기 않기

        myWebSettings = myWebview.getSettings();
        myWebSettings.setJavaScriptEnabled(true); // JS 사용 설정
        myWebSettings.setUseWideViewPort(true); // wide viewport 사용 설정
        myWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // 브라우저 캐시 허용 여부
        myWebSettings.setDomStorageEnabled(true); // 로컬저장소 여부 설정
        myWebSettings.setSupportZoom(false); // 줌 허용 설정
        myWebSettings.setLoadWithOverviewMode(true); // 컨텐츠가 클 경우 스크린 크기에 맞게 조정
        myWebSettings.setJavaScriptCanOpenWindowsAutomatically(true); // javascript가 window.open()을 사용할 수 있도록 설정
        myWebSettings.setSupportMultipleWindows(true);

        myWebview.addJavascriptInterface(new WebAppInterface(this), "Android");
        myWebview.loadUrl("http://192.168.0.214:3000");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    public void LoginTokenSend(final String token) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            myWebview.evaluateJavascript("javascript:tokenSave('" + token + "');", null);
        } else {
            myWebview.loadUrl("javascript:tokenSave('" + token + "')");
        }
    }
}
