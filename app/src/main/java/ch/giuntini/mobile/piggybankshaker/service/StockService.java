package ch.giuntini.mobile.piggybankshaker.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DecimalFormat;
import android.os.Binder;
import android.os.IBinder;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class StockService extends Service {
    private final IBinder binder = new StockService.StockServiceBinder();
    private static final String API_URL = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd,eur,chf&precision=2";
    private RequestQueue requestQueue;
    private LocalDateTime lastUpdate = LocalDateTime.now().minusMinutes(5);
    private double usdPrice;
    private double eurPrice;
    private double chfPrice;
    private final DecimalFormat df = new DecimalFormat();
    private SharedPreferences.Editor editor;
    public static final String INVALID_TEXT = "Can't calculate";

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        SharedPreferences preferences = getSharedPreferences("stock", MODE_PRIVATE);
        editor = preferences.edit();

        usdPrice = Double.parseDouble(preferences.getString("usd", "-1.0"));
        eurPrice = Double.parseDouble(preferences.getString("eur", "-1.0"));
        chfPrice = Double.parseDouble(preferences.getString("chf", "-1.0"));

        if (usdPrice == -1.0 || eurPrice == -1.0 || chfPrice == -1.0) {
            fetchBitcoinPrices();
        }

        df.setMaximumFractionDigits(2);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class StockServiceBinder extends Binder {
        public StockService getService() {
            return StockService.this;
        }
    }

    public Future<Boolean> fetchBitcoinPrices() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                response -> {
                    try {
                        double usdPrice = response.getJSONObject("bitcoin").getDouble("usd");
                        double eurPrice = response.getJSONObject("bitcoin").getDouble("eur");
                        double chfPrice = response.getJSONObject("bitcoin").getDouble("chf");

                        this.usdPrice = usdPrice;
                        this.eurPrice = eurPrice;
                        this.chfPrice = chfPrice;
                        editor.putString("usd", String.valueOf(this.usdPrice));
                        editor.putString("eur", String.valueOf(this.eurPrice));
                        editor.putString("chf", String.valueOf(this.chfPrice));
                        editor.apply();
                        lastUpdate = LocalDateTime.now();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {

                });

        Request<JSONObject> request1 = requestQueue.add(request);
        request1.hasHadResponseDelivered();
        return CompletableFuture.supplyAsync(() -> {
            if (request1.hasHadResponseDelivered()) {
                return request1.hasHadResponseDelivered();
            } else {
                return null;
            }
        });
    }

    public String getValueFor(String currency, double bitcoin) {
        updateStockPrices();

        double stockValue = getPriceForCurrency(currency);

        if (stockValue == -1.0 || bitcoin == -1.0) return INVALID_TEXT;
        return df.format(bitcoin * stockValue);
    }

    public String getBitcoinsForUSD(double USDs) {
        updateStockPrices();

        double stockValue = getPriceForCurrency("USD");

        if (stockValue == -1.0 || USDs == -1.0) return INVALID_TEXT;
        return df.format(USDs / stockValue);
    }

    private void updateStockPrices() {
        if (lastUpdate.plusMinutes(5).isAfter(LocalDateTime.now())) {
            Future<Boolean> future = fetchBitcoinPrices();
            try {
                future.get(5, TimeUnit.SECONDS);
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    public double getPriceForCurrency(String currency) {
        double stockValue = -1.0;
        switch (currency.toUpperCase(Locale.US)) {
            case "USD": {
                stockValue = usdPrice;
                break;
            }
            case "EUR": {
                stockValue = eurPrice;
                break;
            }
            case "CHF": {
                stockValue = chfPrice;
                break;
            }
        }
        return stockValue;
    }
}
