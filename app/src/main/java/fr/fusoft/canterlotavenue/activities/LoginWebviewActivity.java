package fr.fusoft.canterlotavenue.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import fr.fusoft.canterlotavenue.R;
import fr.fusoft.canterlotavenue.controller.LoginClient;

public class LoginWebviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_login);

        WebView v = findViewById(R.id.webviewLogin);
        v.getSettings().setJavaScriptEnabled(true);

        v.setWebChromeClient(new WebChromeClient(){

        });

        v.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(LoginWebviewActivity.this, description, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String cookies = CookieManager.getInstance().getCookie(url);
                Log.d("Test", cookies);
            }
        });
        v.loadUrl(LoginClient.URL_CANTERLOT_AVENUE_PONAUTH);
    }
}
