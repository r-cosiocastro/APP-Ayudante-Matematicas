package com.rafaelcosio.mathhelper.views.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.nineoldandroids.view.ViewHelper;
import com.rafaelcosio.mathhelper.R;
import com.rafaelcosio.mathhelper.databinding.ActivityWelcomeBinding;
import com.rafaelcosio.mathhelper.views.fragments.WelcomeFragment;

import java.util.Objects;

public class WelcomeActivity extends AppCompatActivity {

    private static final int TOTAL_PAGES = 4;
    private boolean isOpaque = true;
    private SharedPreferences settings;
    private ActivityWelcomeBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        settings = getSharedPreferences("app_preferences", MODE_PRIVATE);

        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        Objects.requireNonNull(getSupportActionBar()).hide();

        binding.btnSkip.setOnClickListener(v -> closeWelcome());

        binding.btnNext.setOnClickListener(v -> binding.pager.setCurrentItem(binding.pager.getCurrentItem() + 1, true));

        binding.btnDone.setOnClickListener(v -> closeWelcome());

        PagerAdapter pagerAdapter = new ScreenSlideAdapter(getSupportFragmentManager());
        binding.pager.setAdapter(pagerAdapter);
        binding.pager.setPageTransformer(true, new CrossfadePageTransformer());
        binding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @SuppressLint("PrivateResource")
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == TOTAL_PAGES - 2 && positionOffset > 0) {
                    if (isOpaque) {
                        binding.pager.setBackgroundColor(Color.TRANSPARENT);
                        isOpaque = false;
                    }
                } else {
                    if (!isOpaque) {
                        binding.pager.setBackgroundColor(getResources().getColor(R.color.primary_material_light));
                        isOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                updateIndicator(position);
                if (position == TOTAL_PAGES - 2) {
                    binding.btnSkip.setVisibility(View.GONE);
                    binding.btnNext.setVisibility(View.GONE);
                    binding.btnDone.setVisibility(View.VISIBLE);
                } else if (position < TOTAL_PAGES - 2) {
                    binding.btnSkip.setVisibility(View.VISIBLE);
                    binding.btnNext.setVisibility(View.VISIBLE);
                    binding.btnDone.setVisibility(View.GONE);
                } else if (position == TOTAL_PAGES - 1) {
                    closeWelcome();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        buildCircles();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.pager.clearOnPageChangeListeners();
    }

    private void buildCircles() {

        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);

        for (int i = 0; i < TOTAL_PAGES - 1; i++) {
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.ic_checkbox_blank_circle_white_18dp);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            binding.circles.addView(circle);
        }

        updateIndicator(0);
    }

    private void updateIndicator(int index) {
        if (index < TOTAL_PAGES) {
            for (int i = 0; i < TOTAL_PAGES - 1; i++) {
                ImageView circle = (ImageView) binding.circles.getChildAt(i);
                if (i == index) {
                    circle.setColorFilter(getResources().getColor(R.color.text_selected));
                } else {
                    circle.setColorFilter(getResources().getColor(R.color.transparent_bg));
                }
            }
        }
    }

    private void closeWelcome() {
        settings.edit().putBoolean("first_start", true).apply();
        startActivity(new Intent(WelcomeActivity.this, SplashActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (binding.pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            binding.pager.setCurrentItem(binding.pager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlideAdapter extends FragmentStatePagerAdapter {

        ScreenSlideAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            WelcomeFragment welcomeScreenFragment = null;
            switch (position) {
                case 0:
                    welcomeScreenFragment = WelcomeFragment.newInstance(R.layout.fragment_screen1);
                    break;
                case 1:
                    welcomeScreenFragment = WelcomeFragment.newInstance(R.layout.fragment_screen2);
                    break;
                case 2:
                    welcomeScreenFragment = WelcomeFragment.newInstance(R.layout.fragment_screen3);
                    break;
                case 3:
                    welcomeScreenFragment = WelcomeFragment.newInstance(R.layout.fragment_screen4);
                    break;
            }

            return welcomeScreenFragment;
        }

        @Override
        public int getCount() {
            return TOTAL_PAGES;
        }
    }

    class CrossfadePageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(@NonNull View page, float position) {
            int pageWidth = page.getWidth();

            View backgroundView = page.findViewById(R.id.welcome_fragment);
            View text_head = page.findViewById(R.id.screen_heading);
            View text_content = page.findViewById(R.id.screen_desc);

            if (0 <= position && position < 1) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }
            if (-1 < position && position < 0) {
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }

            if (position <= -1.0f || position >= 1.0f) {

            } else if (position == 0.0f) {
            } else {
                if (backgroundView != null) {
                    ViewHelper.setAlpha(backgroundView, 1.0f - Math.abs(position));
                }

                if (text_head != null) {
                    ViewHelper.setTranslationX(text_head, pageWidth * position);
                    ViewHelper.setAlpha(text_head, 1.0f - Math.abs(position));
                }

                if (text_content != null) {
                    ViewHelper.setTranslationX(text_content, pageWidth * position);
                    ViewHelper.setAlpha(text_content, 1.0f - Math.abs(position));
                }
            }
        }
    }
}
