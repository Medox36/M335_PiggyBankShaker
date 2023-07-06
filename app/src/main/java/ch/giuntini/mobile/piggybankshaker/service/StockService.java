package ch.giuntini.mobile.piggybankshaker.service;

import android.app.Service;
import android.content.Context;
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

import ch.giuntini.mobile.piggybankshaker.R;

public class StockService extends Service {
    private final IBinder binder = new StockService.StockServiceBinder();
    private static final String API_URL = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd,eur,chf&precision=2";
    private RequestQueue requestQueue;
    private double usdPrice;
    private double eurPrice;
    private double chfPrice;
    private final DecimalFormat df = new DecimalFormat();
    private SharedPreferences.Editor editor;
    private final String invalid = getString(R.string.textView_valueOfBitcoinsInCurrency_defaultText);

    @Override
    public void onCreate() {
        super.onCreate();
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
        Context context = (Context) intent.getExtras().get("context");
        requestQueue = Volley.newRequestQueue(context);
        return binder;
    }

    public class StockServiceBinder extends Binder {
        public StockService getService() {
            return StockService.this;
        }
    }

    public void fetchBitcoinPrices() {
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {

                });

        requestQueue.add(request);
    }

    public String getValueFor(String currency, double bitCoin) {
        double stockValue = -1.0;

        switch (currency) {
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

        if (stockValue == -1.0 || bitCoin == -1.0) return invalid;
        return df.format(bitCoin * stockValue);
    }
}
