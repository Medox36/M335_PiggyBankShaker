package ch.giuntini.mobile.piggybankshaker.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DecimalFormat;
import android.os.Binder;
import android.os.IBinder;
import android.widget.TextView;

public class DataManagerService extends Service {

    private final IBinder binder = new ServiceManagerBinder();
    private int coins;
    private float bitcoins;
    private final DecimalFormat df = new DecimalFormat();
    private SharedPreferences.Editor editor;

    private TextView coinsInView;
    private TextView bitcoinsInView;


    public DataManagerService() {
        df.setMaximumFractionDigits(7);
    }

    public class ServiceManagerBinder extends Binder {
        public DataManagerService getService() {
            return DataManagerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        editor = preferences.edit();

        coins = preferences.getInt("coins", 0);
        bitcoins = preferences.getFloat("bitcoins", 0.0f);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        editor.commit();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        editor.commit();
        return super.onUnbind(intent);
    }

    public void incrementCoins() {
        incrementCoinsBy(1);
    }

    public void incrementCoinsBy(int coins) {
        this.coins += coins;
        editor.putInt("coins", this.coins);
        editor.commit();

        updateCoinsView();
    }

    public void decrementCoinsBy(int coins) {
        this.coins -= coins;
        editor.putInt("coins", this.coins);
        editor.commit();

        updateCoinsView();
    }

    public void incrementBitcoinsBy(float bitcoins) {
        this.bitcoins += bitcoins;
        editor.putFloat("bitcoins", this.bitcoins);
        editor.commit();

        updateBitcoinsView();
    }

    public int getCoins() {
        return coins;
    }

    public double getBitcoins() {
        return bitcoins;
    }

    public String getCoinsString() {
        return String.valueOf(coins);
    }

    public String getBitcoinsString() {
        return df.format(bitcoins);
    }

    public void setTextViews(TextView coinsInView, TextView bitcoinsInView) {
        this.coinsInView = coinsInView;
        this.bitcoinsInView = bitcoinsInView;

        updateCoinsView();
        updateBitcoinsView();
    }

    private void updateCoinsView() {
        if (coinsInView != null) coinsInView.setText(getCoinsString());
    }

    private void updateBitcoinsView() {
        if (bitcoinsInView != null) bitcoinsInView.setText(getBitcoinsString());
    }
}
