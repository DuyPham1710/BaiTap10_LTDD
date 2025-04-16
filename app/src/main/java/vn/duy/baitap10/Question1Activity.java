package vn.duy.baitap10;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import vn.duy.baitap10.databinding.ActivityQuestion1Binding;

public class Question1Activity extends AppCompatActivity {
    private ActivityQuestion1Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestion1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //   getSupportActionBar().hide();
        binding.webview.getSettings().setLoadWithOverviewMode(true);
        binding.webview.getSettings().setUseWideViewPort(true);
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.setWebViewClient(new WebViewClient());
        binding.webview.getSettings().setBuiltInZoomControls(true);
        binding.webview.getSettings().setDomStorageEnabled(true);
        binding.webview.getSettings().setDatabaseEnabled(true);
        binding.webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        binding.webview.setWebChromeClient(new WebChromeClient());
        binding.webview.loadUrl("https://shopee.vn/");
    }
} 