package com.laskstud.blingslot;

import android.app.Application;

import com.laskstud.blingslot.api.OldGoodTalesApi;
import com.laskstud.blingslot.game.Game;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;
import com.yandex.metrica.push.YandexMetricaPush;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private static OldGoodTalesApi oldGoodTalesApi;
    private Retrofit retrofit;

    private static Game game;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://23myappserver.bid")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        oldGoodTalesApi = retrofit.create(OldGoodTalesApi.class);

        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder("375279cd-9dfc-44d9-860c-6cef826e2191").build();
        YandexMetrica.activate(getApplicationContext(), config);
        YandexMetrica.enableActivityAutoTracking(this);

        YandexMetricaPush.init(getApplicationContext());

        game = new Game();
    }

    public static OldGoodTalesApi getOldGoodTalesApi() {
        return oldGoodTalesApi;
    }

    public static Game getGame() { return game; }

}
