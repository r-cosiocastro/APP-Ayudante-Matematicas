package com.rafaelcosio.mathhelper.views.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rafaelcosio.mathhelper.R;
import com.rafaelcosio.mathhelper.database.UserDBH;
import com.rafaelcosio.mathhelper.models.User;
import com.rafaelcosio.mathhelper.utils.Utils;
import com.rafaelcosio.mathhelper.views.fragments.CategoryChooserFragment;
import com.rafaelcosio.mathhelper.views.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CategoryChooserFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String NAME_KEY = "NAME_KEY";
    private static final String MAIL_KEY = "EMAIL_KEY";
    private static final String LEVEL_KEY = "USER_LEVEL";
    private static final String POINTS_KEY = "USER_POINTS";
    private static final String TYPE_KEY = "USER_TYPE";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private static User user = new User();

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences settings = getSharedPreferences("user_preferences", 0);
        String uid = settings.getString("last_user_uid", "local");
        user = UserDBH.getUser(this, uid);

        if (user == null) {
            user = new User("local", "Yo", "sincorreo@algo.com", 0, 0, 0);
            UserDBH.addOrUpdate(this, user);
        }

        if (mAuth.getCurrentUser() != null) {
            Log.d(TAG, "Actualizando datos...");
            final String userId = user.getUid();
            ValueEventListener userListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User newUser = dataSnapshot.getValue(User.class);
                    if (newUser == null) {
                        Log.e(TAG, "User " + userId + " is unexpectedly null");
                    } else {
                        // Recuperar sus datos
                        NavigationView navigationView = findViewById(R.id.nav_view);
                        TextView level = navigationView.getHeaderView(0).findViewById(R.id.textView);
                        level.setText(getResources().getString(R.string.nivel, user.getLevel()));
                        if (mAuth.getCurrentUser() != null) {
                            //Picasso.get().load(mAuth.getCurrentUser().getPhotoUrl()).resize(50, 50).into((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView));
                            Glide.with(getApplicationContext() /* context */)
                                    .load(mAuth.getCurrentUser().getPhotoUrl())
                                    .thumbnail(0.05f)
                                    .circleCrop()
                                    .into((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView));
                            TextView name = navigationView.getHeaderView(0).findViewById(R.id.name);

                            name.setText(user.getName());
                        }
                        updateUser(newUser);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                }
            };
            mDatabase.child("users").child(userId).addValueEventListener(userListener);
        } else {
            Log.d(TAG, "El usuario es local");
        }

        Log.d(TAG, user.toString());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            fragment = new CategoryChooserFragment();
            setTitle("Problemas");
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.main_activity_container, fragment, "FragmentoSeleccionado")
                    .commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_problems);
        TextView level = navigationView.getHeaderView(0).findViewById(R.id.textView);
        level.setText(getResources().getString(R.string.nivel, user.getLevel()));

        if (mAuth.getCurrentUser() != null) {
            //Picasso.get().load(mAuth.getCurrentUser().getPhotoUrl()).resize(50, 50).into((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView));
            Glide.with(this /* context */)
                    .load(mAuth.getCurrentUser().getPhotoUrl())
                    .thumbnail(0.05f)
                    .circleCrop()
                    .into((ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView));
            TextView name = navigationView.getHeaderView(0).findViewById(R.id.name);

            name.setText(user.getName());
        }
    }

    private void updateUser(User newUser) {
        user = newUser;
        UserDBH.addOrUpdate(this, user);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle activity_solve_navigation view item clicks here.

        int id = item.getItemId();
        boolean fragmentSelected = false;

        Bundle bundle = new Bundle();
        bundle.putString(NAME_KEY, user.getName());
        bundle.putString(MAIL_KEY, user.getEmail());
        bundle.putInt(LEVEL_KEY, user.getLevel());
        bundle.putInt(POINTS_KEY, user.getPoints());
        bundle.putInt(TYPE_KEY, user.getType());

        switch (id) {
            case R.id.nav_problems:
                fragment = new CategoryChooserFragment();
                fragmentSelected = true;
                setTitle(getResources().getString(R.string.drawer_problems));
                break;
            case R.id.nav_profile:
                fragment = new ProfileFragment();
                fragmentSelected = true;
                setTitle(getResources().getString(R.string.drawer_profile));
                break;
            case R.id.nav_manual:
                Uri uri = Uri.parse("https://www.mamutmatematicas.com/ejercicios/operaciones-basicas.php");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }
        if (fragmentSelected) {
            fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.main_activity_container, fragment, "FragmentoSeleccionado")
                    .commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private boolean doubleTap = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            if (doubleTap) {
                super.onBackPressed();
                finish();
                return;
            }
            Toast.makeText(this, getResources().getString(R.string.double_tap), Toast.LENGTH_SHORT).show();
            this.doubleTap = true;

            new Handler().postDelayed(() -> doubleTap = false, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Utils.openActivity(this, AboutActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void addPointsToUser(int points) {
        int neededPoints = (user.getLevel() + 1) * 400;
        user.setPoints(user.getPoints() + points);
        Log.d(TAG,"Needed points: "+neededPoints);
        while(user.getPoints()>=neededPoints){
            user.setPoints(user.getPoints()-neededPoints);
            user.setLevel(user.getLevel()+1);
            neededPoints = (user.getLevel() + 1) * 400;
            Log.d(TAG,"+1 level: "+user.getLevel());
        }
    }
}