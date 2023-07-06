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

    private DataManagerService dataManagerService;
    private StockService stockService;
    private final ServiceConnection dataManagerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DataManagerService.ServiceManagerBinder binder = (DataManagerService.ServiceManagerBinder) service;
            dataManagerService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {}
    };
    private final ServiceConnection stockConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            StockService.StockServiceBinder binder = (StockService.StockServiceBinder) service;
            stockService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {}
    };

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

        TextView bitcoins = findViewById(R.id.bitcoinValue_bitcoins);
        dataManagerService.setTextViews(null, bitcoins);

        TextView bitcoinValue = findViewById(R.id.bitcoinValue);

        Spinner spinner = (Spinner) findViewById(R.id.currencies);
        spinner.setOnItemSelectedListener(new SpinnerEventListener(bitcoinValue, stockService, dataManagerService));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, DataManagerService.class);
        bindService(intent, dataManagerConnection, Context.BIND_AUTO_CREATE);

        Intent intent2 = new Intent(this, StockService.class);
        bindService(intent2, stockConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(dataManagerConnection);
        unbindService(stockConnection);
    }
}
