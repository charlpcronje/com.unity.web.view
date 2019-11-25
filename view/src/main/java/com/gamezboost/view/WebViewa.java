package com.gamezboost.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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

import java.util.HashMap;

public enum WebViewSingleton {

    INSTANCE("Initial class info");

    public WebViewSingleton getInstance() {
        return INSTANCE;
    }
}

public class WebView {
    private static WebView INSTANCE;
    private WebView(String info) {
        this.info = info;
    }
    public static WebView getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ClassSingleton();
        }
        return INSTANCE;
    }

    public Activity activity;
    public Context context;
    public String webUrl;
    public boolean fullScreen;
    public int width;
    public int height;
    public Style style;
    enum debugTag {
        DEBUG, ERROR, INFO, VERBOSE
    }


    /**
     * @param activity   - Android Main Activity
     * @param context    - The Context on which to instantiate in Unity
     * @param webUrl     - The website url to open in Unity
     * @param fullScreen - Should the web view be full screen?
     * @param width      - Width of the web view
     * @param height     - Height of the web view
     * @param style      - Web View Styles [Text: {heading,description,close}, Color: {heading,background,description}]
     * @return WebView
     */
    public WebView params(Activity activity,Context context, String webUrl, boolean fullScreen, int width, int height, Style style) {
        this.activity   = activity;
        this.context    = context;
        this.webUrl     = webUrl;
        this.fullScreen = fullScreen;
        this.width      = width;
        this.height     = height;
        this.style      = style;
        return this;
    }

    /**
     * Return all properties of WebView
     * @return
     */
    public HashMap<String, ?> params() {
        HashMap<String, ?> obj = new HashMap<>();
        obj.put("activity",this.activity);
           .put("context",this.context)
           .put("webUrl",this.webUrl)
           .put("fullScreen",this.fullScreen)
           .put("width",this.width)
           .put("height",this.height)
           .put("style",this.style);
        retrn obj;
    }

    /**
     * @param text
     * @param color
     */
    public WebView style(Color color, Text text) {
        this.style.color(color);
        this.style.text(text);
        return this;
    }

    /**
     * @param color
     * @return
     */
    public WebView style(Color color) {
        this.style.color(color);
        return this;
    }

    /**
     * @param text
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
     * @param activity
     * @return
     */
    public WebView activity(Activity activity) {
        if (activity != null) {
            this.activity = activity
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
     * @param context
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
     * @param webUrl
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
     * @param fullScreen
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
     * @param width
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
     * @param height
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
     * @param tag
     * @param Message
     */
    public void debug(String Message,debugTag type) {
        switch (type) {
            case DEBUG:
                Log.d(type, Message);
            break;
            case ERROR:
                Log.e(type, Message);
            break;
            case VERBOSE:
                Log.v(type, Message);
            break;
            case INFO:
                Log.i(type, Message);
            break;
        }
    }

    public static WebView init(){
        WebView view = new WebView();
        return view;
    }

    public void OpenView() {

        activity.runOnUiThread(new Runnable() {
            public void run() {
            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setCancelable(false);

            RelativeLayout relativeLayout = new RelativeLayout(activity);
            RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(-1, -1);
            relativeLayout.setLayoutParams(relParams);
            relativeLayout.setBackgroundColor(this.); //-12303292

            final CustomWebView webView = new CustomWebView(activity);

            RelativeLayout.LayoutParams wvParams = new RelativeLayout.LayoutParams(-1, -1);
            webView.setLayoutParams(wvParams);
            webView.getSettings().setJavaScriptEnabled(true);

            webView.addJavascriptInterface(new WebView(), "HTMLOUT");

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
                        Throwable cause = exception.getCause();
                        debug(exception.getMessage()+". Cause: "+cause.toString(),debugTag.ERROR);
                    }
                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                    webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                }
            });

            webView.loadUrl(webUrl);
            relativeLayout.addView(webView);
            alert.setView(relativeLayout);

            final AlertDialog dialog = alert.create();
            dialog.getWindow().setFlags(16777216, 16777216);

            dialog.show();

            Button myButton = new Button(activity);
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
                        UnityPlayer.UnitySendMessage("Code WebView", "LastViewUrl", "" + url);
                    } catch (Exception exception) {
                        Throwable cause = exception.getCause();
                        debug(exception.getMessage()+". Cause: "+cause.toString(),debugTag.ERROR);
                    }
                    dialog.dismiss();
                }
            });

            RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(-2, -2);
            btnParams.addRule(11);

            wvParams.addRule(3, myButton.getId());
            relativeLayout.addView(myButton, btnParams);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

            if (fullScreen) {
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

    @Override
    public String toString() {
        return "Opening '"+ webUrl +"'; Width: "+width+"; Height: "+height+"; Fullscreen: "+fullScreen+"; Context: "+context.toString()+"; Activity: "+activity.toString();
    }

    /**
     * @param String html
     */
    @JavascriptInterface
    public void processHTML(String html) {
        UnityPlayer.UnitySendMessage("Code WebView", "CurrentHtmlCode", html);
    }
}
