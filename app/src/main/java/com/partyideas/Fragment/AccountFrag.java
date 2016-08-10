package com.partyideas.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.partyideas.Activity.MainActivity;
import com.partyideas.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFrag extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
    private TextView usernameView;
    private TextView emailView;
    private TextView accTypeView;
    private GoogleApiClient mGoogleApiClient;

    public AccountFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_account, container, false);
        usernameView = (TextView) root.findViewById(R.id.username);
        emailView = (TextView) root.findViewById(R.id.email);
        accTypeView = (TextView) root.findViewById(R.id.accType);
        SharedPreferences spf = getActivity().getSharedPreferences("account", Context.MODE_PRIVATE);
        usernameView.setText(spf.getString("username","FOOBAR"));
        emailView.setText(spf.getString("email","foo@bar.com"));
        accTypeView.setText(spf.getString("type",""));
        setHasOptionsMenu(true);
        return root;
    }
    private void processLogout(){
        SharedPreferences sPreference = getActivity().getSharedPreferences("account", Context.MODE_PRIVATE);
        boolean isGoogleAccount = sPreference.getString("type","").equals("google");
        sPreference.edit().clear().apply();
        if(isGoogleAccount) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .enableAutoManage(getActivity(), this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status status) {
                                            ((MainActivity)getActivity()).toggleLogin();
                                        }
                                    });
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .build();
            mGoogleApiClient.connect();
        }
        else
            ((MainActivity)getActivity()).toggleLogin();
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.account, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                processLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient!=null) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }
}
