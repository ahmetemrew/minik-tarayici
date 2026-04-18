package com.minik.tarayici;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private WebView webView;
    private EditText urlInput;
    private LinearLayout addressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Main Container
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        mainLayout.setFitsSystemWindows(true);

        // Address Bar Container
        addressBar = new LinearLayout(this);
        addressBar.setOrientation(LinearLayout.HORIZONTAL);
        addressBar.setBackgroundColor(Color.LTGRAY);
        addressBar.setPadding(8, 8, 8, 8);
        addressBar.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // URL Input
        urlInput = new EditText(this);
        urlInput.setSingleLine(true);
        urlInput.setImeOptions(EditorInfo.IME_ACTION_GO);
        urlInput.setBackgroundColor(Color.WHITE);
        urlInput.setTextColor(Color.BLACK);
        urlInput.setPadding(16, 16, 16, 16);
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        inputParams.setMargins(0, 0, 8, 0);
        urlInput.setLayoutParams(inputParams);

        // Go Button
        Button goButton = new Button(this);
        goButton.setText("Go");
        goButton.setTextColor(Color.BLACK);
        goButton.setPadding(16, 0, 16, 0);
        goButton.setMinimumWidth(0);
        goButton.setMinWidth(0);
        goButton.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // WebView
        webView = new WebView(this);
        webView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // Assemble UI
        addressBar.addView(urlInput);
        addressBar.addView(goButton);
        mainLayout.addView(addressBar);
        mainLayout.addView(webView);

        setContentView(mainLayout);

        // Configure WebView
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                urlInput.setText(url);
                return true;
            }
        });
        
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                 // Could add a progress bar here if size permits
            }
        });

        // Hiding Address Bar Logic (Simple scroll listener)
        webView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY && scrollY > 100) {
                    // Scrolling down
                    if (addressBar.getVisibility() == View.VISIBLE) {
                        addressBar.setVisibility(View.GONE);
                    }
                } else if (scrollY < oldScrollY) {
                    // Scrolling up
                    if (addressBar.getVisibility() == View.GONE) {
                        addressBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        // Actions
        View.OnClickListener goAction = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = urlInput.getText().toString().trim();
                
                if (url.startsWith("http://")) {
                    url = url.replace("http://", "https://");
                } else if (!url.startsWith("https://")) {
                    url = "https://" + url;
                }
                
                webView.loadUrl(url);
                hideKeyboard();
                webView.requestFocus();
            }
        };

        goButton.setOnClickListener(goAction);

        urlInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    goAction.onClick(v);
                    return true;
                }
                return false;
            }
        });

        // Initial Load
        webView.loadUrl("https://www.google.com");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(urlInput.getWindowToken(), 0);
        }
    }
}
