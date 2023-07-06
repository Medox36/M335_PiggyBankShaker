package ch.giuntini.mobile.piggybankshaker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;

import ch.giuntini.mobile.piggybankshaker.R;
import ch.giuntini.mobile.piggybankshaker.listener.ShakeListener;
import ch.giuntini.mobile.piggybankshaker.service.DataManagerService;
import ch.giuntini.mobile.piggybankshaker.service.VibratorService;

public class PiggyBankActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private VibratorService vibratorService;
    private DataManagerService dataManagerService;
    private final ServiceConnection dataManagerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DataManagerService.ServiceManagerBinder binder = (DataManagerService.ServiceManagerBinder) service;
            dataManagerService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {}
    };
    private final ServiceConnection vibratorConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            VibratorService.VibratorServiceBinder binder = (VibratorService.VibratorServiceBinder) service;
            vibratorService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {}
    };
    private ShakeListener shakeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piggy_bank);

        TextView coins = findViewById(R.id.piggyBank_coins);
        TextView bitcoins = findViewById(R.id.bitcoinValue_bitcoins);

        Button toSlotMachine = findViewById(R.id.piggyBank_toSlotMachine);
        toSlotMachine.setOnClickListener(view -> {
            Intent intent = new Intent(this, SlotMachineActivity.class);
            startActivity(intent);
        });
        Button toBitcoinValue = findViewById(R.id.piggyBank_toBitcoinValue);
        toBitcoinValue.setOnClickListener(view -> {
            Intent intent = new Intent(this, BitcoinValueActivity.class);
            startActivity(intent);
        });

        dataManagerService.setTextViews(coins, bitcoins);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        shakeListener = new ShakeListener(dataManagerService, vibratorService);

        mSensorManager.registerListener(shakeListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, DataManagerService.class);
        bindService(intent, dataManagerConnection, Context.BIND_AUTO_CREATE);

        Intent intent2 = new Intent(this, VibratorService.class);
        bindService(intent2, vibratorConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        mSensorManager.registerListener(shakeListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(shakeListener);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(dataManagerConnection);
        unbindService(vibratorConnection);
    }
}
