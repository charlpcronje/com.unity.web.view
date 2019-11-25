package com.gamezboost.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.unity3d.player.UnityPlayer;

class WebView {
    private static final WebView Instance = new WebView();
    static WebView Instance() {
        return Instance;
    }

    public Activity activity;
    public Context context;
    public String webUrl;
    public boolean fullScreen;
    public int width = 1200;
    public int height = 800;
    public Style style = new Style();
    enum debugTag {
        DEBUG, ERROR, INFO, VERBOSE
    }
    public AlertDialog dialog;
    public AlertDialog.Builder alert;

    private WebView(Activity activity, String webUrl, boolean fullScreen, int width, int height, Style style) {
        this.activity = activity;
        this.webUrl = webUrl;
        this.fullScreen = fullScreen;
        this.width = width;
        this.height = height;
        this.style = style;
        this.OpenView();
    }

    /**
     * @param activity Activity  - Android Main Activity
     * @param webUrl String      - The website url to open in Unity
     * @param fullScreen boolean - Should the web view be full screen?
     * @param width int          - Width of the web view
     * @param height int         - Height of the web view
     * @param style Style        - Web View Styles [Text: {heading,description,close}, Color: {heading,background,description}]
     * @return WebView
     */
    public WebView params(Activity activity, String webUrl, boolean fullScreen, int width, int height, Style style) {
        this.activity   = activity;
        this.webUrl     = webUrl;
        this.fullScreen = fullScreen;
        this.width      = width;
        this.height     = height;
        this.style      = style;
        return this;
    }


    /**
     * @param text Text
     * @param color Color
     */
    public WebView style(Colors color, Text text) {
        this.style.color(color);
        this.style.text(text);
        return this;
    }

    /**
     * @param color Color
     * @return
     */
    public WebView style(Colors color) {
        this.style.color(color);
        return this;
    }

    /**
     * @param text Text
     * @return
     */
    public WebView style(Text text) {
        this.style.text(text);
        return this;
    }

    /**
     * @return Style
     */
    public Style style() {
        return this.style;
    }

    /**
     * @param activity Activity
     * @return
     */
    public WebView activity(Activity activity) {
        if (activity != null) {
            this.activity = activity;
        }
        return this;
    }

    /**
     * @return Activity
     */
    public Activity activity() {
        return this.activity;
    }

    /**
     * @param context Context
     * @return
     */
    public WebView context(Context context) {
        this.context = context;
        return this;
    }

    /**
     * @return Context
     */
    public Context context() {
        return this.context;
    }

    /**
     * @param webUrl String
     * @return
     */
    public WebView webUrl(String webUrl) {
        this.webUrl = webUrl;
        return this;
    }

    /**
     * @return String
     */
    public String webUrl() {
        return this.webUrl;
    }

    /**
     * @param fullScreen boolean
     * @return
     */
    public WebView fullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
        return this;
    }

    /**
     * @return boolean
     */
    public boolean fullScreen() {
        return this.fullScreen;
    }

    /**
     * @param width int
     * @return
     */
    public WebView width(int width) {
        this.width = width;
        return this;
    }

    /**
     * @return int
     */
    public int width() {
        return this.width;
    }

    /**
     * @param height int
     * @return
     */
    public WebView height(int height) {
        this.height = height;
        return this;
    }

    /**
     * @return int
     */
    public int height() {
        return this.height;
    }

    /**
     * @param message String
     * @param type debugTag
     */
    public void debug(String message,debugTag type) {
        switch (type) {
            case DEBUG:
                Log.d(type.toString(), message);
                break;
            case ERROR:
                Log.e(type.toString(), message);
                break;
            case VERBOSE:
                Log.v(type.toString(), message);
                break;
            case INFO:
                Log.i(type.toString(), message);
                break;
        }
    }

    public void OpenView() {
        this.alert = new AlertDialog.Builder(WebView.Instance().activity());
        WebView.Instance().activity().runOnUiThread(new Runnable() {
            public void run() {
                WebView.Instance().alert.setCancelable(false);

                RelativeLayout relativeLayout = new RelativeLayout(WebView.Instance().activity());
                RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(-1, -1);
                relativeLayout.setLayoutParams(relParams);
                relativeLayout.setBackgroundColor(Color.parseColor(WebView.Instance().style.color().background())); //-12303292

                final CustomWebView webView = new CustomWebView(WebView.Instance().activity());

                RelativeLayout.LayoutParams wvParams = new RelativeLayout.LayoutParams(-1, -1);
                webView.setLayoutParams(wvParams);
                webView.getSettings().setJavaScriptEnabled(true);

                webView.addJavascriptInterface(WebView.Instance(), "HTMLOUT");

                webView.setWebChromeClient(new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress) {
                        WebView.Instance().activity().setProgress(progress * 1000);
                    }
                });

                webView.setWebViewClient(new WebViewClient() {
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        webView.loadUrl(url);
                        try {
                            UnityPlayer.UnitySendMessage("Code WebView", "CurrentViewUrl", "" + webView.getUrl());
                        } catch (Exception exception) {
                            Throwable cause = exception.getCause();
                            debug(exception.getMessage()+". Cause: "+cause.toString(),debugTag.ERROR);
                        }
                        return true;
                    }

                    public void onPageFinished(WebView view, String url) {
                        webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                    }
                });

                webView.loadUrl(WebView.Instance().webUrl());
                relativeLayout.addView(webView);
                WebView.Instance().alert.setView(relativeLayout);

                WebView.Instance().dialog = WebView.Instance().alert.create();
                WebView.Instance().dialog.getWindow().setFlags(16777216, 16777216);

                WebView.Instance().dialog.show();

                Button myButton = new Button(WebView.Instance().activity());
                myButton.setText(WebView.Instance().style().text().close());
                myButton.setId(View.generateViewId());
                myButton.getBackground().setAlpha(0);

                myButton.setTypeface(null, 1);
                myButton.setTextColor(-1);
                myButton.setTextSize(16.0F);

                myButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            String url = webView.getUrl();
                            UnityPlayer.UnitySendMessage("Code WebView", "LastViewUrl", "" + url);
                        } catch (Exception exception) {
                            Throwable cause = exception.getCause();
                            debug(exception.getMessage()+". Cause: "+cause.toString(),debugTag.ERROR);
                        }
                        WebView.Instance().dialog.dismiss();
                    }
                });

                RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(-2, -2);
                btnParams.addRule(11);

                wvParams.addRule(3, myButton.getId());
                relativeLayout.addView(myButton, btnParams);

                WebView.Instance().dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                DisplayMetrics dm = new DisplayMetrics();
                WebView.Instance().activity().getWindowManager().getDefaultDisplay().getMetrics(dm);

                if (WebView.Instance().fullScreen()) {
                    WebView.Instance().dialog.getWindow().setLayout(dm.widthPixels, dm.heightPixels);
                } else if (WebView.Instance().width() > 0 && (WebView.Instance().height() > 0)) {
                    if (width > dm.widthPixels) {
                        WebView.Instance().dialog.getWindow().setLayout(dm.widthPixels, WebView.Instance().height());
                    } else {
                        WebView.Instance().dialog.getWindow().setLayout(WebView.Instance().width(), WebView.Instance().height());
                    }
                }
            }
        });
    }

    @Override
    public String toString() {
        return "Opening '"+ WebView.Instance().webUrl() +"'; Width: "+WebView.Instance().width()+"; Height: "+WebView.Instance().height()+"; Context: "+WebView.Instance().toString()+"; Activity: "+WebView.Instance().activity().toString();
    }

    /**
     * @param void html
     */
    @JavascriptInterface
    public void processHTML(String html) {
        UnityPlayer.UnitySendMessage("Code WebView", "CurrentHtmlCode", html);
    }
}
