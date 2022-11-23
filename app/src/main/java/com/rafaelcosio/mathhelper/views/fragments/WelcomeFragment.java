package com.rafaelcosio.mathhelper.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class WelcomeFragment extends Fragment {
    private final static String LAYOUT_ID = "layoutId";

    public static WelcomeFragment newInstance(int layoutId) {
        WelcomeFragment pane = new WelcomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(LAYOUT_ID, layoutId);
        pane.setArguments(bundle);
        return pane;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(Objects.requireNonNull(getArguments()).getInt(LAYOUT_ID, -1), container, false);
    }
}