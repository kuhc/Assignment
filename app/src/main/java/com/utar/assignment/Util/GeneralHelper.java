package com.utar.assignment.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.utar.assignment.Model.User;

public class GeneralHelper {
    private static final String PUSH_KIT_TAG = "PushGetTokenActivity";
    private static String msg;

    public static void showMessage(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static void getToken(Context ctx, User user) {
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(ctx).getString("client/app_id");
                    String token = HmsInstanceId.getInstance(ctx).getToken(appId, "HCM");
                    Log.i(PUSH_KIT_TAG, "get token:" + token);
                    // 1. After a token is obtained by using the getToken method, null judgment must be performed.
                    // 2. In the outer code segment of the getToken method, you need to add exception capture processing.
                    if (!TextUtils.isEmpty(token)) {
                        sendRegTokenToServer(token, user);
                    }

                    msg = "\n" +"get token:" + token;
                } catch (ApiException e) {
                    Log.e(PUSH_KIT_TAG, "get token failed, " + e);
                    msg = "\n" +"get token failed, " + e;
                }
            }
        }.start();
    }

    private static void sendRegTokenToServer(String token, User user) {
        Log.i(PUSH_KIT_TAG, "sending token to server. token:" + token);

        user.setPushToken(token);
        FirestoreHelper.setUser(user, new FirebaseCallback() {
            @Override
            public void onResponse() {
                // Do nothing
            }
        });
    }
}
