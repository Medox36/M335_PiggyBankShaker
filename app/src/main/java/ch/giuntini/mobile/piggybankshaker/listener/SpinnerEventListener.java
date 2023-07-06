package ch.giuntini.mobile.piggybankshaker.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import ch.giuntini.mobile.piggybankshaker.service.DataManagerService;
import ch.giuntini.mobile.piggybankshaker.service.StockService;

public class SpinnerEventListener implements AdapterView.OnItemSelectedListener {

    private final TextView bitcoinValue;
    private final StockService stockService;
    private final DataManagerService dataManagerService;

    public SpinnerEventListener(TextView bitcoinValue, StockService stockService, DataManagerService dataManagerService) {
        this.bitcoinValue = bitcoinValue;
        this.stockService = stockService;
        this.dataManagerService = dataManagerService;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedItem = (String) adapterView.getItemAtPosition(i);
        switch (selectedItem) {
            case "USD": {
                setText(stockService.getValueFor("USD", getBitcoinAmount()));
                break;
            }
            case "EUR": {
                setText(stockService.getValueFor("EUR", getBitcoinAmount()));
                break;
            }
            case "CHF": {
                setText(stockService.getValueFor("CHF", getBitcoinAmount()));
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        setText("Currency not supported");
    }

    private void setText(String str) {
        if (bitcoinValue != null) bitcoinValue.setText(str);
    }

    private double getBitcoinAmount() {
        return dataManagerService != null ? Double.parseDouble(dataManagerService.getBitcoinsString()) : -1.0;
    }
}
