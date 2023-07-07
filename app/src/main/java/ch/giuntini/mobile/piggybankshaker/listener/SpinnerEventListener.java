package ch.giuntini.mobile.piggybankshaker.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import ch.giuntini.mobile.piggybankshaker.service.DataManagerService;
import ch.giuntini.mobile.piggybankshaker.service.StockService;

public class SpinnerEventListener implements AdapterView.OnItemSelectedListener {

    private final TextView bitcoinValue;
    private StockService stockService;
    private DataManagerService dataManagerService;

    public SpinnerEventListener(TextView bitcoinValue) {
        this.bitcoinValue = bitcoinValue;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedItem = (String) adapterView.getItemAtPosition(i);
        switch (selectedItem) {
            case "USD": {
                setText(getValueFor("USD", getBitcoinAmount()));
                break;
            }
            case "EUR": {
                setText(getValueFor("EUR", getBitcoinAmount()));
                break;
            }
            case "CHF": {
                setText(getValueFor("CHF", getBitcoinAmount()));
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

    private String getValueFor(String currency, double amount) {
        return stockService != null ? stockService.getValueFor(currency, amount) : StockService.INVALID_TEXT;
    }

    public void setDataManagerService(DataManagerService dataManagerService) {
        this.dataManagerService = dataManagerService;
    }

    public void setStockService(StockService stockService) {
        this.stockService = stockService;
    }
}
