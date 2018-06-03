package comlmeerlu.github.sleeptimer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity{

   private Button btnCountdown;
   private TextView txtCountdown;
   private TextView txtOnOff;
   private EditText inputHours;
   private EditText inputMinutes;
   private EditText inputSeconds;
   private Switch sWifi;
   private Switch sAir;
   private Switch sBlue;


    private WifiManager wifiManager;
    private BluetoothAdapter mBluetoothAdapter;
    private CountDownTimer countDownTimer;

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


        btnCountdown = findViewById(R.id.btnCountdown);
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
                    txtCountdown.setText(String.format("%d", (int) (millisUntilFinished / 1000)) + " sec");
                }
            }

            @Override
            public void onFinish() {
                changeStates();
                finished = true;
                btnCountdown.setText("Start");
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

    public void CountdownHandler(final View view) throws InterruptedException {
        if (!finished) {
            if (!counting) {
                countDownTimer.start();
                btnCountdown.setText("Stop");
            } else {
                initCountdownTimer(getTime());
                stop = true;
                btnCountdown.setText("Start");
            }
            counting = !counting;
        }else {
            initCountdownTimer(getTime());
            countDownTimer.start();
            finished = false;
        }
    }
}
