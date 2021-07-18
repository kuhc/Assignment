package com.utar.assignment.Util;

import android.net.Uri;



public interface FirebaseCallback {
    public static final int QUESTION = 1;
    public static final int ANSWER = 2;

    default void onResponse() {};
    default void onResponse(String string) {};
    default void onResponse(Object object) {};
    default void onResponse(Uri uri) {};
}