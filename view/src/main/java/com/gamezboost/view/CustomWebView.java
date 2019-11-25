package com.gamezboost.view;

import android.content.Context;
import android.webkit.WebView;

public class CustomWebView extends WebView {
    Context mContext;

    public CustomWebView(Context context) {
        super(context);
        this.mContext = context;
    }

    public boolean onCheckIsTextEditor() {
        return true;
    }
}
