package ch.giuntini.mobile.piggybankshaker.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.security.SecureRandom;

import ch.giuntini.mobile.piggybankshaker.Symbol;

public class SlotMachineService extends Service {
    private final IBinder binder = new SlotMachineService.SlotMachineServiceBinder();


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public Symbol[] spin() {
        SecureRandom secureRandom = new SecureRandom();
        int bound = Symbol.values().length - 1;
        int symbol1 = secureRandom.nextInt(bound);
        int symbol2 = secureRandom.nextInt(bound);
        int symbol3 = secureRandom.nextInt(bound);

        Symbol[] symbols = new Symbol[3];
        symbols[0] = Symbol.ofOrdinal(symbol1);
        symbols[1] = Symbol.ofOrdinal(symbol2);
        symbols[2] = Symbol.ofOrdinal(symbol3);

        return symbols;
    }

    public class SlotMachineServiceBinder extends Binder {
        public SlotMachineService getService() {
            return SlotMachineService.this;
        }
    }
}