package ch.giuntini.mobile.piggybankshaker.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import ch.giuntini.mobile.piggybankshaker.service.DataManagerService;
import ch.giuntini.mobile.piggybankshaker.service.VibratorService;

public class ShakeListener implements SensorEventListener {

    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    private VibratorService vibratorService;
    private DataManagerService dataManagerService;
    private long lastCoin = System.currentTimeMillis();
    private static final long INTERVAL = 500;


    public ShakeListener() {
        acceleration = 0;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        lastAcceleration = currentAcceleration;
        currentAcceleration = (float) Math.sqrt(x * x + y * y + z * z);
        float delta = currentAcceleration - lastAcceleration;
        acceleration = acceleration * 0.9f + delta;
        if (acceleration > 1) {
            if (dataManagerService != null) {
                long now = System.currentTimeMillis();
                if (now - INTERVAL > lastCoin) {
                    dataManagerService.incrementCoins();
                    if (vibratorService != null) {
                        vibratorService.vibrateShort();
                    }
                    lastCoin = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void setDataManagerService(DataManagerService dataManagerService) {
        this.dataManagerService = dataManagerService;
    }

    public void setVibratorService(VibratorService vibratorService) {
        this.vibratorService = vibratorService;
    }
}
