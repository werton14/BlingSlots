package com.laskstud.blingslot;

import android.app.Application;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.AppsFlyerProperties;
import com.laskstud.blingslot.api.OldGoodTalesApi;
import com.laskstud.blingslot.game.Game;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;
import com.yandex.metrica.push.YandexMetricaPush;

import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private static final String AF_DEV_KEY = "XBKBPqA5E6LAPZnPcR55CS";

    private static OldGoodTalesApi oldGoodTalesApi;
    private Retrofit retrofit;

    private static Game game;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://box.blingslot.club")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        oldGoodTalesApi = retrofit.create(OldGoodTalesApi.class);

        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder("375279cd-9dfc-44d9-860c-6cef826e2191").build();
        YandexMetrica.activate(getApplicationContext(), config);
        YandexMetrica.enableActivityAutoTracking(this);

        YandexMetricaPush.init(getApplicationContext());

        AppsFlyerLib.getInstance().init(AF_DEV_KEY, null, getApplicationContext());
        AppsFlyerLib.getInstance().startTracking(this);

        game = new Game();
    }

    public static OldGoodTalesApi getOldGoodTalesApi() {
        return oldGoodTalesApi;
    }

    public static Game getGame() { return game; }

}
