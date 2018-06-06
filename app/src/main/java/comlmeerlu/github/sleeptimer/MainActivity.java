package comlmeerlu.github.sleeptimer;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements NumberPicker.OnClickListener{

   private TextView txtCountdown;
   private TextView txtOnOff;
   private EditText inputHours;
   private EditText inputMinutes;
   private EditText inputSeconds;
   private Switch sWifi;
   private Switch sAir;
   private Switch sBlue;
   private Button btnStop;
   private Button btnReset;
   private Button btnStart;
   private Button btnTest;


    private WifiManager wifiManager;
    private BluetoothAdapter mBluetoothAdapter;
    private CountDownTimer countDownTimer;

    private long secUntilFinished;
    private int seconds;
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

        inputHours = findViewById(R.id.inputHours);
        inputMinutes = findViewById(R.id.inputMinutes);
        inputSeconds = findViewById(R.id.inputSeconds);
        seconds = getTime();

        btnStart = findViewById(R.id.btnCountdown);
        btnReset = findViewById(R.id.btnReset);
        btnStop = findViewById(R.id.btnStop);

        //* *****************************************************************************************
        btnTest = findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog d = new Dialog(MainActivity.this);
                d.setTitle("Countdown Picker");
                d.setContentView(R.layout.dialog);

                Button btnDelete = (Button) d.findViewById(R.id.btnDelete);
                Button btnSave = (Button) d.findViewById(R.id.btnSave);

                final NumberPicker pickMin = (NumberPicker) d.findViewById(R.id.pickMin);
                pickMin.setMaxValue(100);
                pickMin.setMinValue(1);
                pickMin.setWrapSelectorWheel(false);

                final NumberPicker pickSec = (NumberPicker) d.findViewById(R.id.pickSec);
                pickSec.setMaxValue(59);
                pickSec.setMinValue(0);
                pickSec.setWrapSelectorWheel(false);

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        seconds = pickSec.getValue();
                        seconds += pickMin.getValue()*60;
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
        });
        //******************************************************************************************

        txtCountdown = findViewById(R.id.txtCountdown);
        txtCountdown.setText(String.format("%d",seconds));
        txtOnOff = findViewById(R.id.txtOnOff);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        sWifi = findViewById(R.id.switchWIFI);
        sBlue = findViewById(R.id.switchBLUE);
        sAir = findViewById(R.id.switchAir);
    }

    private int getTime(){
        int r = Integer.parseInt(inputHours.getText().toString()) * 60 * 60;
        r = r + Integer.parseInt(inputMinutes.getText().toString()) * 60;
        r = r + Integer.parseInt(inputSeconds.getText().toString());

        return r;
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
                    txtCountdown.setText(String.format("%d", (int) secUntilFinished) + " sec");
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
            }
        };
    }

    public void changeStates(){
        if (sWifi.isChecked())
            wifiManager.setWifiEnabled(false);

        if (sBlue.isChecked())
                mBluetoothAdapter.disable();

        if (sAir.isChecked()) {

            // toggle airplane mode
            Settings.System.putInt(
                    getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0);

            // Post an intent to reload
            //Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            //intent.putExtra("state", !false);
            //sendBroadcast(intent);
        }
    }

    public void btnStopHandler(View view){
        if (counting) {
            stop = true;
            counting = false;
            btnStop.setText("CONTINUE");
        } else {
            counting = true;
            btnStop.setText("STOP");
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
        btnReset.setVisibility(View.GONE);
    }

    public void btnStartHandler(View view) throws InterruptedException {

        initCountdownTimer(getTime());
        countDownTimer.start();
        btnStart.setVisibility(View.GONE);
        btnStop.setVisibility(View.VISIBLE);
        btnReset.setVisibility(View.VISIBLE);
        counting = true;
    }

    @Override
    public void onClick(View v) {

    }
}
