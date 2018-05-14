package comlmeerlu.github.sleeptimer;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

   private Button button;
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled())
            button.setText("AUS");
        else
            button.setText("AN");


    }

    public void changeWifi(View view){

        wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());

        if (wifiManager.isWifiEnabled())
            button.setText("AUS");
        else
            button.setText("AN");

    }
}
