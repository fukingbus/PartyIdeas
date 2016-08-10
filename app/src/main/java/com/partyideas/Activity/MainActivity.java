package com.partyideas.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.partyideas.Fragment.AccountFrag;
import com.partyideas.Fragment.CustomMeetupFrag;
import com.partyideas.Fragment.LoginFrag;
import com.partyideas.Fragment.NewPublicMeetupFrag;
import com.partyideas.Fragment.NewsFrag;
import com.partyideas.Fragment.OfficialMeetupFrag;
import com.partyideas.Fragment.RegisterFrag;
import com.partyideas.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    final int REQ_GOOGLE_SIGNIN = 8000; // google sign in request code
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentTransact(new NewsFrag());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        return super.onOptionsItemSelected(item);
    }

    private void fragmentTransact(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment).commit();
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()){
            case R.id.nav_offical_meetup:
                OfficialMeetupFrag omfrag = new OfficialMeetupFrag();
                fragmentTransact(omfrag);
                setTitle(item.getTitle());
                break;
            case R.id.nav_news:
                NewsFrag newsfrag = new NewsFrag();
                fragmentTransact(newsfrag);
                setTitle(getResources().getString(R.string.app_name));
                break;
            case R.id.nav_account:
                SharedPreferences spf = getSharedPreferences("account",MODE_PRIVATE);
                boolean isLoggedIn = spf.getBoolean("status",false);
                Fragment frag;
                if(isLoggedIn)
                    frag = new AccountFrag();
                else
                    frag = new LoginFrag();
                fragmentTransact(frag);
                setTitle(item.getTitle());
                break;
            case R.id.nav_cust_schedule:
                CustomMeetupFrag cmFrag = new CustomMeetupFrag();
                fragmentTransact(cmFrag);
                setTitle(item.getTitle());
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQ_GOOGLE_SIGNIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            LoginFrag frag = (LoginFrag) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            frag.onGoogleLoginCompleteHandler(result);
        }
    }
    public void toggleRegister(Bundle args){
        RegisterFrag frag = new RegisterFrag();
        frag.setArguments(args);
        fragmentTransact(frag);
    }
    public void toggleLogin(){
        LoginFrag frag = new LoginFrag();
        fragmentTransact(frag);
    }
    public void toggleAccount(){
        AccountFrag frag = new AccountFrag();
        fragmentTransact(frag);
    }
    public void togglePublicMeetup(){
        CustomMeetupFrag cmFrag = new CustomMeetupFrag();
        fragmentTransact(cmFrag);
    }
    public void toggleCreatePublicMeetup(){
        NewPublicMeetupFrag frag = new NewPublicMeetupFrag();
        fragmentTransact(frag);
    }
}
