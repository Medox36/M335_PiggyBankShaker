package ch.giuntini.mobile.piggybankshaker.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.LocalDateTime;

import ch.giuntini.mobile.piggybankshaker.R;
import ch.giuntini.mobile.piggybankshaker.Symbol;
import ch.giuntini.mobile.piggybankshaker.service.DataManagerService;
import ch.giuntini.mobile.piggybankshaker.service.SlotMachineService;
import ch.giuntini.mobile.piggybankshaker.service.StockService;
import ch.giuntini.mobile.piggybankshaker.service.VibratorService;

public class SlotMachineActivity extends AppCompatActivity {
    private final ServiceConnection dataManagerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DataManagerService.ServiceManagerBinder binder = (DataManagerService.ServiceManagerBinder) service;
            dataManagerService = binder.getService();
            dataManagerService.setTextViews(coins, bitcoins);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {}
    };
    private final ServiceConnection slotMachineConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            SlotMachineService.SlotMachineServiceBinder binder = (SlotMachineService.SlotMachineServiceBinder) service;
            slotMachineService = binder.getService();
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
    private final ServiceConnection vibrationConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            VibratorService.VibratorServiceBinder binder = (VibratorService.VibratorServiceBinder) service;
            vibratorService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {}
    };
    private DataManagerService dataManagerService;
    private VibratorService vibratorService;
    private SlotMachineService slotMachineService;
    private StockService stockService;
    private TextView coins;
    private TextView bitcoins;
    private ImageView symbol1;
    private ImageView symbol2;
    private ImageView symbol3;
    private LocalDateTime lastSpin = LocalDateTime.now().minusSeconds(3);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_machine);

        coins = findViewById(R.id.slotMachine_coins);
        bitcoins = findViewById(R.id.slotMachine_bitcoins);

        symbol1 = findViewById(R.id.symbol1);
        symbol2 = findViewById(R.id.symbol2);
        symbol3 = findViewById(R.id.symbol3);

        EditText bettingCoins = findViewById(R.id.bettingCoins);

        bindToServices();

        Button spin = findViewById(R.id.slotMachine_spin);
        spin.setOnClickListener(view -> {
            final int coinsUsed = Integer.parseInt(bettingCoins.getText().toString());
            if (dataManagerService.getCoins() <= coinsUsed || coinsUsed < 1) {
                return;
            }
            if (lastSpin.plusSeconds(4).isAfter(LocalDateTime.now())) {
                return;
            }
            lastSpin = LocalDateTime.now();
            dataManagerService.decrementCoinsBy(coinsUsed);
            onSpin(slotMachineService.spin(), coinsUsed);
        });
        Button toBitcoinValue = findViewById(R.id.slotMachine_toBitcoinValue);
        toBitcoinValue.setOnClickListener(view -> {
            Intent intent = new Intent(this, BitcoinValueActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(dataManagerConnection);
        unbindService(slotMachineConnection);
        unbindService(stockConnection);
        unbindService(vibrationConnection);
    }

    private void bindToServices() {
        Intent intent = new Intent(this, DataManagerService.class);
        bindService(intent, dataManagerConnection, Context.BIND_AUTO_CREATE);

        Intent intent2 = new Intent(this, SlotMachineService.class);
        bindService(intent2, slotMachineConnection, Context.BIND_AUTO_CREATE);

        Intent intent3 = new Intent(this, StockService.class);
        bindService(intent3, stockConnection, Context.BIND_AUTO_CREATE);

        Intent intent4 = new Intent(this, VibratorService.class);
        bindService(intent4, vibrationConnection, Context.BIND_AUTO_CREATE);
    }

    private void onSpin(Symbol[] symbols, int coinsUsed) {
        setImageForImageView(symbol1, symbols[0]);
        setImageForImageView(symbol2, symbols[1]);
        setImageForImageView(symbol3, symbols[2]);

        checkForWin(symbols, coinsUsed);
    }

    private void setImageForImageView(ImageView imageView, Symbol symbol) {
        imageView.setBackground(AppCompatResources.getDrawable(this, symbol.getImageID()));
    }

    private void checkForWin(Symbol[] symbols, int coinsUsed) {
        boolean sameSymbols = true;
        Symbol relevantSymbol = symbols[0];
        for (Symbol symbol : symbols) {
            if (relevantSymbol != symbol) {
                sameSymbols = false;
                break;
            }
        }

        if (sameSymbols) {
            calculateWin(relevantSymbol, coinsUsed);
        }
    }

    private void calculateWin(Symbol winningSymbol, int coinsUsed) {
        switch (winningSymbol) {
            case CHERRY: {
                vibrateAndIncrementCoinsBy(2, coinsUsed);
                break;
            }
            case SPADES: {
                vibrateAndIncrementCoinsBy(5, coinsUsed);
                break;
            }
            case COIN: {
                vibrateAndIncrementCoinsBy(10, coinsUsed);
                break;
            }
            case DIAMOND: {
                vibrateAndIncrementCoinsBy(25, coinsUsed);
                break;
            }
            case STAR: {
                vibrateAndIncrementBitcoinsBy(50, coinsUsed);
                break;
            }
            case SEVEN: {
                vibrateAndIncrementBitcoinsBy(100, coinsUsed);
                break;
            }
        }
    }

    private void vibrateAndIncrementCoinsBy(int factor, int coinsUsed) {
        dataManagerService.incrementCoinsBy(coinsUsed * factor);
        vibratorService.vibrateLong();
    }

    private void vibrateAndIncrementBitcoinsBy(int factor, int coinsUsed) {
        String bitcoinsForUSD = stockService.getBitcoinsForUSD(factor * coinsUsed);
        float bitcoins = 0.0f;
        try {
            bitcoins = Float.parseFloat(bitcoinsForUSD);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        dataManagerService.incrementBitcoinsBy(bitcoins);
        vibratorService.vibrateLong();
    }
}
