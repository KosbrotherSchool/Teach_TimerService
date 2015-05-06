package com.jasonko.servicetimer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by kolichung on 5/6/15.
 */
public class TimerService extends Service {

    private static UIHandler handler;
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_STOP = "STOP";

    public enum State{
        Stopped,
        Running
    };

    private static State mState = State.Stopped;

    private static String stringRemainTimer = "00";
    private static int remainSeconds;
    private int finalSeconds;
    private Boolean isRun = true;

    private static ServiceThread serviceThread;

    private Notification mNotification = null;
    final int NOTIFICATION_ID = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();
        if (action.equals(ACTION_PLAY)){
            isRun = true;
            startTimer();
        }else if(action.equals(ACTION_STOP)){
            stopTimer();
        }

        return START_NOT_STICKY;
        // START_STICKY: if service left "started" state, will later be restarted by system
        // START_NOT_STICKY: If service is canceled, it will wait for next command start

    }

    private void stopTimer() {
        // stop thread
        mState = State.Stopped;
        isRun = false;
        stopForeground(true);
    }

    private void startTimer() {
        Calendar c = Calendar.getInstance();
        int currentSeconds = (int) (c.getTimeInMillis()/1000);
        finalSeconds = currentSeconds + remainSeconds;

        mState = State.Running;
        serviceThread = new ServiceThread(handler);
        new Thread(serviceThread).start();

        setUpForegroundNotification("計時中...");
    }

    public class ServiceThread implements Runnable {

        private UIHandler threadHandler;

        public ServiceThread( UIHandler handler) {
            super();
            this.threadHandler = handler;
        }

        @Override
        public void run() {

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
                    }else if ( 0 < secs && secs <10){
                        stringRemainTimer = "0" + Integer.toString(secs);
                    }else {
                        stringRemainTimer = Integer.toString(secs);
                    }

                    Message msg = this.threadHandler.obtainMessage();
                    msg.getData().putString(UIHandler.MSG, stringRemainTimer);
                    threadHandler.sendMessage(msg);
                    Log.v("TEST", "still runing");
                }


            }
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    void setUpForegroundNotification(String text) {

        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        mNotification  = new Notification.Builder(this)
                .setContentTitle("ServiceTimer")
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent).build();

        startForeground(NOTIFICATION_ID, mNotification);
    }

    public static State getTimerState(){
        return mState;
    }

    public static void registerHandler(Handler uiHandler) {
        handler = (UIHandler) uiHandler;
    }

    public static void setRemainSeconds(int seconds) {
        remainSeconds = seconds;
    }

    public static int getRemainSeconds(){
        return remainSeconds;
    }

    public static String getStringRemainSeconds(){
        return stringRemainTimer;
    }

    public static UIHandler getUIHandler(){
        return handler;
    }

    public static void resetServiceThreadHandler(){
        serviceThread.threadHandler = handler;
    }

}
