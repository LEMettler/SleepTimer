package comlmeerlu.github.sleeptimer;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

   private Button btnCountdown;
   private TextView txtCountdown;
   private TextView txtOnOff;
   private EditText inputMinutes;
    private EditText inputHours;
    private EditText inputSeconds;

   private WifiManager wifiManager;
   private CountDownTimer countDownTimer;

    private long time;
   private boolean counting;
    private boolean stop;
    private boolean finished;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        time = 0;
        counting = false;
        stop = false;
        finished = true;


        btnCountdown = findViewById(R.id.btnCountdown);
        txtCountdown = findViewById(R.id.txtCountdown);
        txtCountdown.setText(String.format("%d", time));
        txtOnOff = findViewById(R.id.txtOnOff);
        inputHours = findViewById(R.id.inputHours);
        inputMinutes = findViewById(R.id.inputMinutes);
        inputSeconds = findViewById(R.id.inputSeconds);



        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled())
            txtOnOff.setText("Disable WIFI in");
        else
            txtOnOff.setText("Enable WIFI in");

        //initCountdownTimer();
    }

    private void initCountdownTimer(){


        countDownTimer = new CountDownTimer(1000 * time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (stop){
                    cancel();
                    stop = false;
                }else {
                    //Integer.toString can't be applied to textviews, bc it doesn't regard some specific formats
                    txtCountdown.setText(String.format("%d", (int) (millisUntilFinished / 1000)));
                }
            }

            @Override
            public void onFinish() {
                changeWifi();
                finished = true;
                btnCountdown.setText("Start");
            }
        };
    }

    public void changeWifi(){

        wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());

        if (wifiManager.isWifiEnabled())
            txtOnOff.setText("Disable WIFI in");
        else
            txtOnOff.setText("Enable WIFI in");

    }

    public void CountdownHandler(final View view) throws InterruptedException {

        time = Integer.parseInt(inputSeconds.getText().toString());
        time = time + Integer.parseInt(inputMinutes.getText().toString()) * 60;
        time = time + Integer.parseInt(inputHours.getText().toString()) *60 * 60;

        if (!finished) {
            if (!counting) {
                countDownTimer.start();
                btnCountdown.setText("Stop");
            } else {
                stop = true;
                btnCountdown.setText("Start");
            }
            counting = !counting;
        }else {
            if (time != 0) {
                initCountdownTimer();
                countDownTimer.start();
                finished = false;
            }

        }
    }
}
