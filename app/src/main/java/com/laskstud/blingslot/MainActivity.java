package com.laskstud.blingslot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appsflyer.AppsFlyerLib;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;
import com.laskstud.blingslot.api.OldGoodTalesApi;
import com.laskstud.blingslot.models.oldgoodtales.FirstQueryModel;
import com.laskstud.blingslot.models.oldgoodtales.SecondQueryModel;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AppEventsLogger logger;

    private static final String ID_KEY = "id";
    private final static int INTERVAL = 1000 * 60 * 2; //2 minutes

    @BindView(R.id.webView) WebView webView;

    Handler handler = new Handler();

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private OldGoodTalesApi oldGoodTalesApi;

    Runnable secondQueryRunnable = new Runnable()
    {
        @Override
        public void run() {
            secondQuery();
            handler.postDelayed(secondQueryRunnable, INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        logger = AppEventsLogger.newLogger(this);

        preferences = getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor = preferences.edit();

        oldGoodTalesApi = App.getOldGoodTalesApi();

        AppLinkData.fetchDeferredAppLinkData(MainActivity.this, new AppLinkData.CompletionHandler() {
            @Override
            public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                try {
                    String[] params = appLinkData.getTargetUri().toString().split("://");
                    if (params.length > 0) {
                        editor.putString("parameters", params[1].replaceAll("\\?", "&"));
                        editor.apply();
                        editor.commit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                firstQuery();
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
    }

    void startRepeatingSecondQuery()
    {
        secondQueryRunnable.run();
    }

    void stopRepeatingSecondQuery()
    {
        handler.removeCallbacks(secondQueryRunnable);
    }

    private String getCountryCode() {
        String country = "";
        TelephonyManager telManager = (TelephonyManager)
                getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        country = telManager.getSimCountryIso().toUpperCase();
        if(country.length() == 0) {
            country = telManager.getNetworkCountryIso().toUpperCase();
            if(country.length() == 0) {
                country = getResources().getConfiguration().locale.getCountry().toUpperCase();
            }
        }
        return  country;
    }

    private int getTimeZone() {
        return Calendar.getInstance().getTimeZone().getRawOffset();
    }

    private void saveId(int id) {
        editor.putInt(ID_KEY, id);
        editor.apply();
        editor.commit();
    }

    private int getId() {
        return preferences.getInt(ID_KEY, -1);
    }

    private void firstQuery() {
        int id = getId();
        String country = getCountryCode();
        Log.w("Test 518, country", country);
        if(id == -1) {
            oldGoodTalesApi.getData(country, getTimeZone()).enqueue(new Callback<FirstQueryModel>() {
                @Override
                public void onResponse(Call<FirstQueryModel> call, Response<FirstQueryModel> response) {
                    processingFirstQueryResponse(response.body());
                }

                @Override
                public void onFailure(Call<FirstQueryModel> call, Throwable t) {
                    startGameActivity();
                }
            });
        } else {

            oldGoodTalesApi.getData(getCountryCode(), id, getTimeZone()).enqueue(new Callback<FirstQueryModel>() {
                @Override
                public void onResponse(Call<FirstQueryModel> call, Response<FirstQueryModel> response) {
                    processingFirstQueryResponse(response.body());
                }

                @Override
                public void onFailure(Call<FirstQueryModel> call, Throwable t) {
                    startGameActivity();
                }
            });
        }
    }

    private void processingFirstQueryResponse(FirstQueryModel firstQueryModel) {
        if(getId() == -1) saveId(firstQueryModel.getId());
        String result = firstQueryModel.getResult();
        Log.w("Test 518, sr id", String.valueOf(firstQueryModel.getId()));
        Log.w("Test 518, sr result", result);
        if(result.isEmpty()) {
            startGameActivity();
        } else {
            startRepeatingSecondQuery();
            String url = result + preferences.getString("parameters", "&source=organic&pid=1");
            Log.w("Test 518, wv url", url);
            webView.loadUrl(url);
        }
    }

    private void secondQuery() {
        int id = getId();
        if(id != -1) {
            oldGoodTalesApi.getData(id).enqueue(new Callback<List<SecondQueryModel>>() {
                @Override
                public void onResponse(Call<List<SecondQueryModel>> call, Response<List<SecondQueryModel>> response) {
                    processingSecondQueryResponse(response.body());
                }

                @Override
                public void onFailure(Call<List<SecondQueryModel>> call, Throwable t) {

                }
            });
        }
    }

    private void processingSecondQueryResponse(List<SecondQueryModel> secondQueryModels) {
        for(SecondQueryModel secondQueryModel : secondQueryModels) {
            logConversionEvent(secondQueryModel);
            trackConversionEvent(secondQueryModel);
        }
    }

    public void logConversionEvent (SecondQueryModel secondQueryModel) {
        Bundle bundle = new Bundle();
        bundle.putInt("goal", secondQueryModel.getGoal());
        bundle.putInt("payout", secondQueryModel.getPayout());
        logger.logEvent("conversion", bundle);
    }

    public void trackConversionEvent(SecondQueryModel secondQueryModel) {
        Map<String,Object> eventValues = new HashMap<>();
        eventValues.put("goal", secondQueryModel.getGoal());
        eventValues.put("payout", secondQueryModel.getPayout());
        AppsFlyerLib.getInstance().trackEvent(getApplicationContext(), "conversion", eventValues);
    }


    private void startGameActivity(){
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRepeatingSecondQuery();
    }
}
