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
    private final ServiceConnection dataManagerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DataManagerService.ServiceManagerBinder binder = (DataManagerService.ServiceManagerBinder) service;
            DataManagerService dataManagerService = binder.getService();
            dataManagerService.setTextViews(coins, bitcoins);
            shakeListener.setDataManagerService(dataManagerService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {}
    };
    private final ServiceConnection vibratorConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            VibratorService.VibratorServiceBinder binder = (VibratorService.VibratorServiceBinder) service;
            VibratorService vibratorService = binder.getService();
            shakeListener.setVibratorService(vibratorService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {}
    };
    private ShakeListener shakeListener;
    private TextView coins;
    private TextView bitcoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piggy_bank);

        coins = findViewById(R.id.slotMachine_coins);
        bitcoins = findViewById(R.id.slotMachine_bitcoins);

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

        bindToServices();

        shakeListener = new ShakeListener();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSensorManager.registerListener(shakeListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mSensorManager.registerListener(shakeListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
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
        mSensorManager.unregisterListener(shakeListener);
        unbindService(dataManagerConnection);
        unbindService(vibratorConnection);
    }

    private void bindToServices() {
        Intent intent = new Intent(this, DataManagerService.class);
        bindService(intent, dataManagerConnection, Context.BIND_AUTO_CREATE);

        Intent intent2 = new Intent(this, VibratorService.class);
        bindService(intent2, vibratorConnection, Context.BIND_AUTO_CREATE);
    }
}
