package com.laskstud.blingslot.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yandex.metrica.push.firebase.MetricaMessagingService;

public class FirebaseMessagingMasterService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        new MetricaMessagingService().processPush(this, message);

        Log.w("onMessageReceived", "New push received!");
    }
}