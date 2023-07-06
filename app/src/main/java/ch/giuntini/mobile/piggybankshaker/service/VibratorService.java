package ch.giuntini.mobile.piggybankshaker.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibratorService extends Service {
    private final IBinder binder = new VibratorService.VibratorServiceBinder();
    final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class VibratorServiceBinder extends Binder {
        public VibratorService getService() {
            return VibratorService.this;
        }
    }

    public void vibrateShort() {
        final VibrationEffect vibrationEffect;

        vibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE);

        vibrator.cancel();
        vibrator.vibrate(vibrationEffect);
    }

    public void vibrateLong() {
        final VibrationEffect vibrationEffect;

        vibrationEffect = VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE);

        vibrator.cancel();
        vibrator.vibrate(vibrationEffect);
    }
}
