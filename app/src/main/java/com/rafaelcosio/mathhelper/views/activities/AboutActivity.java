package com.rafaelcosio.mathhelper.views.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.vansuita.materialabout.builder.AboutBuilder;

import com.rafaelcosio.mathhelper.R;

public class AboutActivity extends AppCompatActivity {
    //region Create & Listeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAboutView();
        initHomeButton();
    }

    private void initHomeButton() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region Fill About
    private void initAboutView() {
        View materialAboutView = AboutBuilder.with(this)
                .setAppName(R.string.app_name)
                .setAppIcon(R.mipmap.ic_launcher_round)
                .setVersionNameAsAppSubTitle()
                .setPhoto(R.drawable.profile)
                .setCover(R.drawable.cover)
                .setLinksAnimated(true)
                .setDividerDashGap(13)
                .setName(R.string.developer)
                .setSubTitle(R.string.developer_subtitle)
                .setLinksColumnsCount(2)
                .addGitHubLink(R.string.developer_username)
                .addFiveStarsAction()
                .addFacebookLink(R.string.facebook)
                .addTwitterLink(R.string.twitter)
                .addEmailLink(R.string.developer_email)
                .addWebsiteLink(R.string.web_uabcs)
                .addLinkedInLink(R.string.linkedin)
                .addShareAction(R.string.app_name)
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(true)
                .build();
        setContentView(materialAboutView);
    }
    //endregion
}

