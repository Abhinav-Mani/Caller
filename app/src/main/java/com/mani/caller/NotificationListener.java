package com.mani.caller;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;

import org.jetbrains.annotations.Nullable;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
@SuppressLint("OverrideAbstract")
public class NotificationListener extends NotificationListenerService {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Bundle extras = sbn.getNotification().extras;
        }

    }
}
