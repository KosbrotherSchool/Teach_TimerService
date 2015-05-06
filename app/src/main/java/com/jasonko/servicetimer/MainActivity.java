package com.jasonko.servicetimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private TextView textTimer;
    private Button buttonToggle;
    private Context mContext;
    private UIHandler mUIHandler;
    private static final String initialRemainTime = "00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textTimer = (TextView) findViewById (R.id.text_timer);
        buttonToggle = (Button) findViewById (R.id.button_toggle);
        mContext = this;

        textTimer.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NewTimerService.isRunning() != true) {
                    showTimerDialog();
                } else {
                    Toast.makeText(mContext, "倒數中...", Toast.LENGTH_SHORT).show();
                }
            }


        });

        buttonToggle.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NewTimerService.isRunning() != true) {
                    if (textTimer.getText().equals(initialRemainTime)) {
                        Toast.makeText(mContext, "請先設置時間!!", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(MainActivity.this, NewTimerService.class);
                        startService(intent);
                        buttonToggle.setText("停止");
                    }
                } else {
                    NewTimerService.stopTimerService();
                    buttonToggle.setText("開始");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setRemainTimeText(String text){
        textTimer.setText(text);
    }

    public void changeButtonState(){
        buttonToggle.setText("開始");
    }

    public void makeFinishToast(){
        Toast.makeText(mContext, "倒數結束!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(NewTimerService.isRunning() == true){
            buttonToggle.setText("停止");
            textTimer.setText(NewTimerService.getStringRemainSeconds());

            mUIHandler = new UIHandler(MainActivity.this);
            NewTimerService.registerHandler(mUIHandler);

        }else{
            mUIHandler = new UIHandler(MainActivity.this);
            NewTimerService.registerHandler(mUIHandler);
            textTimer.setText(NewTimerService.getStringRemainSeconds());
        }

    }

    private void showTimerDialog() {
        // TODO Auto-generated method stub
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customDialog = inflater.inflate(R.layout.custom_dialog, null);
        final EditText editSec = (EditText) customDialog.findViewById(R.id.edit_sec);
        editSec.setRawInputType(Configuration.KEYBOARD_12KEY);

        AlertDialog.Builder settingDialog = new AlertDialog.Builder(mContext)
                .setTitle("設置倒數時間")
                .setView(customDialog)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String stringTimer = "";
                                int intSec = Integer.valueOf(editSec.getText().toString());
                                if (intSec == 0){
                                    stringTimer = stringTimer + "00";
                                }else if ( 0 < intSec && intSec < 10){
                                    stringTimer = stringTimer + "0"+ editSec.getText().toString();
                                }else{
                                    stringTimer =  stringTimer + editSec.getText().toString();
                                }
                                textTimer.setText(stringTimer);
                                int totalSec = intSec;
                                NewTimerService.setRemainSeconds(totalSec);
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        settingDialog.show();
    }
}
