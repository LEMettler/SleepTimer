package comlmeerlu.github.sleeptimer;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements NumberPicker.OnClickListener{

   private TextView txtOnOff;

   private Switch sWifi;
   private Switch sAir;
   private Switch sBlue;
   private Switch sSilent;
   private Button btnStop;
   private Button btnReset;
   private Button btnStart;
   private Button btnInput;


    private WifiManager wifiManager;
    private BluetoothAdapter mBluetoothAdapter;
    private CountDownTimer countDownTimer;
    private AudioManager audioManager;

    private long secUntilFinished;
    private int seconds;
    private int startSec;
    private boolean counting;
    private boolean stop;
    private boolean finished;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        counting = false;
        stop = false;
        finished = true;

        startSec = 30 * 60;
        seconds = startSec;

        btnStart = findViewById(R.id.btnCountdown);
        btnReset = findViewById(R.id.btnReset);
        btnStop = findViewById(R.id.btnStop);
        btnInput = findViewById(R.id.btnInput);
        btnInput.setText(secToUFCStandard(startSec));

        sWifi = findViewById(R.id.switchWIFI);
        sBlue = findViewById(R.id.switchBLUE);
        sAir = findViewById(R.id.switchAir);
        sSilent = findViewById(R.id.switchSilent);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        //******************************************************************************************
        //******************************************************************************************

        final Context context = this;

        sSilent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (sSilent.isChecked()){

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (notificationManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !notificationManager.isNotificationPolicyAccessGranted()) {

                        sSilentHandler();
                    }
                }
            }
        });

        //******************************************************************************************
        //******************************************************************************************
    }

    private void sSilentHandler(){
        final Dialog d = new Dialog(MainActivity.this);
        d.setTitle("Request Access");
        d.setContentView(R.layout.dialog_permission);

        Button btnCancel = d.findViewById(R.id.btnCancel);
        Button btnOK = d.findViewById(R.id.btnOK);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sSilent.setChecked(false);
                d.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                    intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(intent);
                }
                d.dismiss();
            }
        });
        d.show();
    }

    public void countdownInputHandler(View view){

        final Dialog d = new Dialog(MainActivity.this);
        d.setTitle("Countdown Picker");
        d.setContentView(R.layout.dialog_timeinput);

        Button btnDelete = (Button) d.findViewById(R.id.btnDelete);
        Button btnSave = (Button) d.findViewById(R.id.btnSave);

        final NumberPicker pickHours = (NumberPicker) d.findViewById(R.id.pickHours);
        pickHours.setMaxValue(9);
        pickHours.setMinValue(0);
        pickHours.setWrapSelectorWheel(false);

        final NumberPicker pickMin = (NumberPicker) d.findViewById(R.id.pickMin);
        pickMin.setMaxValue(59);
        pickMin.setMinValue(0);
        pickMin.setWrapSelectorWheel(false);
        pickMin.setValue(30);

        final NumberPicker pickSec = (NumberPicker) d.findViewById(R.id.pickSec);
        pickSec.setMaxValue(59);
        pickSec.setMinValue(1);
        pickSec.setWrapSelectorWheel(false);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSec = pickSec.getValue();
                startSec += pickMin.getValue()*60;
                startSec += pickHours.getValue()*60*60;
                btnInput.setText(secToUFCStandard(startSec));

                d.dismiss();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    private String secToUFCStandard(int totalSeconds){
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        String ufcTime = "";
        if (hours < 10) {
            ufcTime = "0";
        }
        ufcTime += Integer.toString(hours) + ":";
        if (minutes < 10) {
            ufcTime += "0";
        }
        ufcTime += Integer.toString(minutes) + ":";
        if (seconds < 10) {
            ufcTime += "0";
        }
        ufcTime += Integer.toString(seconds);

        return ufcTime;
    }

    private void initCountdownTimer(int time){
        countDownTimer = new CountDownTimer(1000 * time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (stop){
                    cancel();
                    stop = false;
                }else {
                    //Integer.toString can't be applied to textviews, bc it doesn't regard some specific formats
                    secUntilFinished = (millisUntilFinished / 1000);
                    btnInput.setText(secToUFCStandard((int) secUntilFinished));
                }
            }

            @Override
            public void onFinish() {
                changeStates();
                finished = true;
                counting = false;
                btnStart.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.GONE);
                btnReset.setVisibility(View.GONE);
                btnInput.setClickable(true);
            }
        };
    }

    public void changeStates(){
        if (sWifi.isChecked())
            wifiManager.setWifiEnabled(false);

        if (sBlue.isChecked())
                mBluetoothAdapter.disable();


        if (sSilent.isChecked())
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);


        if (sAir.isChecked()) {

            // read the airplane mode setting
            boolean isEnabled = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isEnabled = Settings.System.getInt(
                        getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
            }

            // toggle airplane mode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.System.putInt(
                        getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);
            }

            // Post an intent to reload
            Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            intent.putExtra("state", !isEnabled);
            sendBroadcast(intent);
        }
    }

    public void btnStopHandler(View view){
        if (counting) {
            stop = true;
            counting = false;
            btnStop.setText("CONTINUE");
            btnStop.setTextSize(14);
        } else {
            counting = true;
            btnStop.setText("STOP");
            btnStop.setTextSize(18);
            initCountdownTimer((int) secUntilFinished);
            countDownTimer.start();
        }
    }

    public void btnResetHandler(View view){
        if (counting) {
            stop = true;
        }

        counting = false;
        btnStart.setVisibility(View.VISIBLE);
        btnStop.setVisibility(View.GONE);
        btnStop.setText("STOP");
        btnStop.setTextSize(18);
        btnReset.setVisibility(View.GONE);
        btnInput.setClickable(true);
        btnInput.setText(secToUFCStandard(startSec));
    }

    public void btnStartHandler(View view) throws InterruptedException {

        initCountdownTimer(startSec);
        countDownTimer.start();
        btnStart.setVisibility(View.GONE);
        btnStop.setVisibility(View.VISIBLE);
        btnReset.setVisibility(View.VISIBLE);
        btnInput.setClickable(false);
        counting = true;

        pushNote();
    }

    //todo push notification
    private void pushNote(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification note = new Notification.Builder(getApplicationContext()).setContentTitle("ComfySleep").setContentText("Countdown running").setSmallIcon(R.drawable.ic_launcher_foreground).build();
            note.flags |= Notification.FLAG_AUTO_CANCEL;
            manager.notify(0, note);

        }
    }

    @Override
    public void onClick(View v) {

    }
}
