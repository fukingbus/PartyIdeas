package com.partyideas.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.partyideas.Activity.MainActivity;
import com.partyideas.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFrag extends Fragment implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{

    final int REQ_GOOGLE_SIGNIN = 8000;
    private TextView info;
    private EditText usernameField;
    private EditText passwordField;
    private Button generalLoginBT;
    private SignInButton googleLoginBT;
    private GoogleApiClient mGoogleApiClient;
    private TextView noAcc;

    public LoginFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_login, container, false);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(),this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        info = (TextView)root.findViewById(R.id.info);
        usernameField = (EditText) root.findViewById(R.id.usernameField);
        passwordField = (EditText) root.findViewById(R.id.passwordField);
        generalLoginBT = (Button) root.findViewById(R.id.general_login);
        googleLoginBT = (SignInButton) root.findViewById(R.id.g_signin);
        noAcc = (TextView)root.findViewById(R.id.register);
        noAcc.setOnClickListener(this);
        googleLoginBT.setOnClickListener(this);
        generalLoginBT.setOnClickListener(this);
        return root;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Fatal",connectionResult.getErrorMessage());
    }

    public void onGoogleLoginCompleteHandler(GoogleSignInResult result){
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            try {
                String name = acct.getDisplayName();
                String email = acct.getEmail();
                Bundle bundle = new Bundle();
                bundle.putString("name",name);
                bundle.putString("email",email);
                ((MainActivity)getActivity()).toggleRegister(bundle);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        } else {
            Log.e("Google","Signed out");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:
                ((MainActivity)getActivity()).toggleRegister(null);
                break;
            case R.id.g_signin:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                getActivity().startActivityForResult(signInIntent, REQ_GOOGLE_SIGNIN);

                break;
            case R.id.general_login:
                break;
        }
    }
}
