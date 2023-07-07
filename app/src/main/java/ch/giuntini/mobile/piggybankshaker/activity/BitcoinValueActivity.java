package ch.giuntini.mobile.piggybankshaker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import ch.giuntini.mobile.piggybankshaker.R;
import ch.giuntini.mobile.piggybankshaker.listener.SpinnerEventListener;
import ch.giuntini.mobile.piggybankshaker.service.DataManagerService;
import ch.giuntini.mobile.piggybankshaker.service.StockService;

public class BitcoinValueActivity extends AppCompatActivity {

    private final ServiceConnection dataManagerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DataManagerService.ServiceManagerBinder binder = (DataManagerService.ServiceManagerBinder) service;
            DataManagerService dataManagerService = binder.getService();
            dataManagerService.setTextViews(null, bitcoins);
            spinnerEventListener.setDataManagerService(dataManagerService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {}
    };
    private final ServiceConnection stockConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            StockService.StockServiceBinder binder = (StockService.StockServiceBinder) service;
            StockService stockService = binder.getService();
            spinnerEventListener.setStockService(stockService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {}
    };
    private TextView bitcoins;
    private SpinnerEventListener spinnerEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitcoin_value);

        Button toPiggyBank = findViewById(R.id.bitcoinValue_toPiggyBank);
        toPiggyBank.setOnClickListener(view -> {
            Intent intent = new Intent(this, PiggyBankActivity.class);
            startActivity(intent);
        });
        Button toSlotMachine = findViewById(R.id.bitcoinValue_toSlotMachine);
        toSlotMachine.setOnClickListener(view -> {
            Intent intent = new Intent(this, SlotMachineActivity.class);
            startActivity(intent);
        });

        bitcoins = findViewById(R.id.slotMachine_bitcoins);

        TextView bitcoinValue = findViewById(R.id.bitcoinValue);

        spinnerEventListener = new SpinnerEventListener(bitcoinValue);

        bindToServices();

        Spinner spinner = findViewById(R.id.currencies);
        spinner.setOnItemSelectedListener(spinnerEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(dataManagerConnection);
        unbindService(stockConnection);
    }

    private void bindToServices() {
        Intent intent = new Intent(this, DataManagerService.class);
        bindService(intent, dataManagerConnection, Context.BIND_AUTO_CREATE);

        Intent intent2 = new Intent(this, StockService.class);
        bindService(intent2, stockConnection, Context.BIND_AUTO_CREATE);
    }
}
