package com.jasonko.servicetimer;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by kolichung on 5/6/15.
 */
public class NewTimerService extends IntentService {

    public static UIHandler handler;

    private static String stringRemainTimer = "00";
    private static int remainSeconds;
    private int finalSeconds;
    private static Boolean isRun = false;

    private Notification mNotification = null;
    final int NOTIFICATION_ID = 1;

    public NewTimerService(){
        super("TimerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        startTimer();

        while (isRun){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar c = Calendar.getInstance();
            int currentSeconds = (int) (c.getTimeInMillis()/1000);

            if (currentSeconds > finalSeconds){
                stopTimer();
            }else {
                remainSeconds = finalSeconds - currentSeconds;
                int secs = remainSeconds;

                if( secs == 0){
                    stringRemainTimer = "00";
                }else if ( 0 < secs && secs < 10){
                    stringRemainTimer = "0" + Integer.toString(secs);
                }else {
                    stringRemainTimer = Integer.toString(secs);
                }

                Message msg = handler.obtainMessage();
                msg.getData().putString(UIHandler.MSG, stringRemainTimer);
                handler.sendMessage(msg);
                Log.v("TimerService", "still runing");
            }

        }

        stopTimer();

    }



    private void startTimer() {
        isRun = true;
        Calendar c = Calendar.getInstance();
        int currentSeconds = (int) (c.getTimeInMillis()/1000);
        finalSeconds = currentSeconds + remainSeconds;

        setUpForegroundNotification("計時中...");
    }

    private void stopTimer() {
        isRun = false;
        stopForeground(true);
    }

    void setUpForegroundNotification(String text) {

        PendingIntent pIntent = PendingIntent.getActivity(
                getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        mNotification  = new Notification.Builder(this)
                .setContentTitle("ServiceTimer")
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent).build();

        startForeground(NOTIFICATION_ID, mNotification);
    }

    public static void registerHandler(Handler uiHandler) {
        handler = (UIHandler) uiHandler;
    }

    public static void setRemainSeconds(int seconds) {
        remainSeconds = seconds;
    }

    public static String getStringRemainSeconds(){
        return stringRemainTimer;
    }

    public static boolean isRunning(){
        return isRun;
    }

    public static void stopTimerService(){
        isRun = false;
    }

}
