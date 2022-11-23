package com.rafaelcosio.mathhelper.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import java.util.Random;

public class Utils {

    public static void openActivity(Context context, Class clazz) {
        Intent mIntent = new Intent(context, clazz);
        Bundle options = new Bundle();
        ActivityCompat.startActivity(context, mIntent, options);
    }

    public static int randomNumber(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    public static int randomNumberMinMax(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
}
