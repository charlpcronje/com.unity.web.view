package com.gamezboost.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.unity3d.player.UnityPlayer;

public class Custom {
    private static final Custom ourInstance = new Custom();

    public static Custom getInstance() {
        return ourInstance;
    }

    private Custom(final Activity activity, final String WebUrl, final boolean isFullScreen, final int width, final int height) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder alert = new AlertDialog.Builder((Context)activity);
                alert.setCancelable(false);

                RelativeLayout relativeLayout = new RelativeLayout((Context)activity);
                RelativeLayout.LayoutParams relparams = new RelativeLayout.LayoutParams(-1, -1);
                relativeLayout.setLayoutParams((ViewGroup.LayoutParams)relparams);
                relativeLayout.setBackgroundColor(-12303292);

                final CustomWebView webView = new CustomWebView((Context)activity);

                RelativeLayout.LayoutParams wvparams = new RelativeLayout.LayoutParams(-1, -1);
                webView.setLayoutParams((ViewGroup.LayoutParams)wvparams);
                webView.getSettings().setJavaScriptEnabled(true);

                webView.addJavascriptInterface(new PopupWebview(), "HTMLOUT");

                webView.setWebChromeClient(new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress) {
                        activity.setProgress(progress * 1000);
                    }
                });

                webView.setWebViewClient(new WebViewClient() {
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        webView.loadUrl(url);

                        try {
                            UnityPlayer.UnitySendMessage("Code Webview", "CurrentViewUrl", "" + webView.getUrl());
                        } catch (Exception exception) {

                        }

                        return true;
                    }

                    public void onPageFinished(WebView view, String url) {
                        webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                    }
                });

                webView.loadUrl(WebUrl);
                relativeLayout.addView((View)webView);
                alert.setView((View)relativeLayout);

                final AlertDialog dialog = alert.create();
                dialog.getWindow().setFlags(16777216, 16777216);

                dialog.show();

                Button myButton = new Button((Context)activity);
                myButton.setText("X");
                myButton.setId(10);
                myButton.getBackground().setAlpha(0);

                myButton.setTypeface(null, 1);
                myButton.setTextColor(-1);
                myButton.setTextSize(16.0F);

                myButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            String url = webView.getUrl();
                            UnityPlayer.UnitySendMessage("Code Webview", "LastViewUrl", "" + url);
                        } catch (Exception exception) {

                        }

                        dialog.dismiss();
                    }
                });

                RelativeLayout.LayoutParams btnparams = new RelativeLayout.LayoutParams(-2, -2);
                btnparams.addRule(11);

                wvparams.addRule(3, myButton.getId());
                relativeLayout.addView((View)myButton, (ViewGroup.LayoutParams)btnparams);

                dialog.getWindow().setBackgroundDrawable((Drawable)new ColorDrawable(0));
                DisplayMetrics dm = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

                if (isFullScreen) {
                    dialog.getWindow().setLayout(dm.widthPixels, dm.heightPixels);
                } else if (width > 0 && height > 0) {
                    if (width > dm.widthPixels) {
                        dialog.getWindow().setLayout(dm.widthPixels, height);
                    } else {
                        dialog.getWindow().setLayout(width, height);
                    }
                }
            }
        });
    }

    @JavascriptInterface
    public void processHTML(String html) {
        UnityPlayer.UnitySendMessage("Code Webview", "CurrentHtmlCode", html);
    }
}
