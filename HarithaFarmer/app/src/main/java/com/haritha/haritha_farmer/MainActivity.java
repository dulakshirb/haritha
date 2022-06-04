package com.haritha.haritha_farmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    private TextView loggedInUserName, loggedInUserEmail;
    private CircleImageView loggedInUserProfileImage;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();

        drawerLayout = findViewById(R.id.drawer);
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navHeaderView = navigationView.getHeaderView(0);
        loggedInUserName = (TextView) navHeaderView.findViewById(R.id.txt_loggedin_username);
        loggedInUserEmail = (TextView) navHeaderView.findViewById(R.id.txt_loggedin_email);
        loggedInUserProfileImage = (CircleImageView) navHeaderView.findViewById(R.id.img_loggedin_profile);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            switch (id) {
                case R.id.nav_task_and_schedule:
                    fragment = new TaskAndScheduleFragment();
                    loadFragment(fragment);
                    break;
                case R.id.nav_crops:
                    fragment = new CropsFragment();
                    loadFragment(fragment);
                    break;
                case R.id.nav_resources:
                    fragment = new ResourcesFragment();
                    loadFragment(fragment);
                    break;
                case R.id.nav_fertilizing:
                    break;
                case R.id.nav_seeding:
                    break;
                case R.id.nav_predictive_analytics:
                    break;
                case R.id.nav_security:
                    break;
                case R.id.nav_watering_and_soilHealth:
                    break;
                case R.id.nav_reports:
                    break;
                case R.id.nav_profile:
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    break;
                case R.id.nav_support:
                    break;
                case R.id.nav_logout:
                    if (item.getItemId() == R.id.nav_logout) {
                        mAuth.signOut();
                        sendUserToLoginActivity();
                    }
                    break;
                default:
                    return true;
            }
            return true;
        });

    }

    private void retrieveLoggedInUserInfo() {
        rootRef.child("Farmer").child("Users").child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if ((snapshot.exists()) && (snapshot.hasChild("farmName") && snapshot.hasChild("image"))) {
                            String retLoggedInUserName = Objects.requireNonNull(snapshot.child("farmName").getValue()).toString();
                            String retLoggedInUserProfileImage = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                            loggedInUserEmail.setText(currentUser.getEmail());
                            loggedInUserName.setText(retLoggedInUserName);
                            Picasso.get().load(retLoggedInUserProfileImage).into(loggedInUserProfileImage);
                        } else if ((snapshot.exists()) && (snapshot.hasChild("farmName"))) {
                            String retLoggedInUserName = Objects.requireNonNull(snapshot.child("farmName").getValue()).toString();
                            loggedInUserEmail.setText(currentUser.getEmail());
                            loggedInUserName.setText(retLoggedInUserName);
                        } else {
                            sendUserToProfileFragment();
                            Toast.makeText(MainActivity.this, "Please set & update your profile information.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        fragmentTransaction.addToBackStack(null);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null) {
            sendUserToLoginActivity();
        } else {
            verifyUserExistence();
        }
    }

    private void verifyUserExistence() {
        String currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        rootRef.child("Farmer").child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.child("farmName").exists())) {
                    //Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                    retrieveLoggedInUserInfo();
                } else {
                    sendUserToProfileFragment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendUserToProfileFragment() {
        Fragment fragment = new ProfileFragment();
        loadFragment(fragment);
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

}