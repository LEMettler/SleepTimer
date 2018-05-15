package comlmeerlu.github.sleeptimer;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

   private Button btnCountdown;
   private TextView txtCountdown;
   private TextView txtOnOff;
   private WifiManager wifiManager;

   private CountDownTimer countDownTimer;
    private int seconds;
   private boolean counting;
    private boolean stop;
    private boolean finished;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seconds = 10;
        counting = false;
        stop = false;
        finished = false;


        btnCountdown = findViewById(R.id.btnCountdown);
        txtCountdown = findViewById(R.id.txtCountdown);
        txtCountdown.setText(String.format("%d",seconds));
        txtOnOff = findViewById(R.id.txtOnOff);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled())
            txtOnOff.setText("Disable WIFI in");
        else
            txtOnOff.setText("Enable WIFI in");

        initCountdownTimer(seconds);
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
            countDownTimer.start();
            finished = false;
        }
    }
}
