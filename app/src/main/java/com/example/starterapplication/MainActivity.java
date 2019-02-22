package com.example.starterapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;


public class MainActivity extends Activity implements TimePickerDialog.OnTimeSetListener {
//    TimePicker alarmTime;

    Button saveIp, lightOn, lightOff, openTimeButton;
    SharedPreferences sharedPref;
    Context context;
    TextView ipAddress, alarmTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmTextView = (TextView) findViewById(R.id.alarm_text);
        openTimeButton  = (Button) findViewById(R.id.openTime);
        saveIp = (Button) findViewById(R.id.saveIp);
        lightOn = (Button) findViewById(R.id.lightOn);
        lightOff = (Button) findViewById(R.id.lightOff);
        ipAddress = (TextView) findViewById(R.id.ipField);
        context = this;
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_alarm), Context.MODE_PRIVATE);

        ipAddress.setText((sharedPref.getString("ip_address", "")));

    }


    public void onSaveIp(View arg0) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("ip_address", ipAddress.getText().toString());
        editor.commit();
        Toast.makeText(this, "Salvo com sucesso", Toast.LENGTH_LONG).show();


    }
    public void onTimeClicked(View v) {

        DialogFragment fragment = new TimePickerFragment();
        FragmentManager fm = this.getFragmentManager();
        fm.beginTransaction();
        fragment.show(fm, "ola");
    }

    public void onCancelAlarm(View v) {
        cancelAlarm();
    }

    public void onLightOn(View arg0) {
        final Context ctx = this;
        AsyncTask async = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    sharedPref = context.getSharedPreferences(
                            getString(R.string.preference_alarm), Context.MODE_PRIVATE);
                    String link = sharedPref.getString("ip_address", "");
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(link + "/on").build();
                    Response response = null;

                    try {
                        response = client.newCall(request).execute();
                        return request.body();
                    } catch (IOException e) {
                        Toast.makeText(ctx, "falha na requisição", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } catch (Error error){
                    error.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                Toast.makeText(ctx, "Feito", Toast.LENGTH_SHORT).show();
            }
        }.execute();



    }

    public void onLightOff(View arg0) {
        final Context ctx = this;
        AsyncTask async = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    sharedPref = context.getSharedPreferences(
                            getString(R.string.preference_alarm), Context.MODE_PRIVATE);
                    String link = sharedPref.getString("ip_address", "");
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(link + "/off").build();
                    Response response = null;

                    try {
                        response = client.newCall(request).execute();
                        return request.body();
                    } catch (IOException e) {
                        Toast.makeText(ctx, "falha na requisição", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } catch (Error error){
                    error.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                Toast.makeText(ctx, "Feito", Toast.LENGTH_SHORT).show();
            }
        }.execute();


    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        alarmTextView.setText("Hora: " + hourOfDay + " Minutos: " + minute);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        updateTimeText(c);
        startAlarm(c);
    }

    private void updateTimeText (Calendar c) {
        String timeText = "Alarm set for: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        Toast.makeText(this, timeText, Toast.LENGTH_SHORT).show();

    }

    private void startAlarm (Calendar c) {
        try {
            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlertReceiver.class);
//            Intent intent = new Intent();
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            Log.i("Script", "Alarme startado");
            Log.i("Script", DateFormat.getTimeInstance(DateFormat.SHORT).format(c.get(Calendar.DATE)));
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), alarmIntent);
        } catch (Exception error) {
            Log.e("Script", "Erro ao startar o alarm manager");
        }


    }

    private void cancelAlarm () {
        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("ALARME_DISPARADO");
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmMgr.cancel(alarmIntent);
        Toast.makeText(this, "Alarme cancelado", Toast.LENGTH_SHORT);


    }
}
