package com.jasonko.servicetimer;

import android.os.Message;

/**
 * Created by kolichung on 5/6/15.
 */
import android.os.Handler;

public class UIHandler extends Handler {

    private MainActivity mActivity;
    public static final String MSG = "msg";
    public String finalReaminTime = "00" ;

    public UIHandler(MainActivity activity) {
        super();
        this.mActivity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        String text = msg.getData().getString(MSG);
        this.mActivity.setRemainTimeText(text);

        if (text.equals(finalReaminTime)){
            this.mActivity.changeButtonState();
            this.mActivity.makeFinishToast();
        }
    }
}

