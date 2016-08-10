package com.partyideas.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.partyideas.Activity.MainActivity;
import com.partyideas.Adapter.Utils;
import com.partyideas.R;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFrag extends Fragment implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{

    final int REQ_GOOGLE_SIGNIN = 8000;
    private String BASE_API_SERVER ;
    private TextView info;
    private EditText usernameField;
    private EditText passwordField;
    private Button generalLoginBT;
    private SignInButton googleLoginBT;
    private GoogleApiClient mGoogleApiClient;
    private TextView noAcc;
    private SharedPreferences sPreferences;

    public LoginFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_login, container, false);
        BASE_API_SERVER  = getResources().getString(R.string.base_api_server);
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
                final String email = acct.getEmail();
                final String gid = acct.getId();
                final Bundle bundle = new Bundle();
                Log.e("google",gid);
                bundle.putString("name",name);
                bundle.putString("email",email);
                bundle.putString("gid",gid);

                final ProgressDialog pdialog = ProgressDialog.show(getContext(),"Connecting","Verifying account information",true);
                Ion.with(getContext())
                        .load(BASE_API_SERVER+"/api/user?type=google&email="+email)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                try {

                                    pdialog.dismiss();
                                    if (e == null) {
                                        JSONObject obj = new JSONObject(result.toString());
                                        boolean status = obj.getBoolean("status");
                                        if(status){
                                            Utils util = new Utils();
                                            String token = util.SHA256("PI_GTOKEN_"+gid);
                                            Login(null,null,email,token,false);
                                        }
                                        else{
                                            ((MainActivity)getActivity()).toggleRegister(bundle);
                                        }
                                    }
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                        });
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        } else {
            Log.e("Google","Signed out");
        }
    }
    private void Login(final String username,final String pass,final String email, final String token, final boolean isGeneral){
        final ProgressDialog pdialog = ProgressDialog.show(getContext(),"Authenticating","Logging you in",true);
        JsonObject jobj = new JsonObject();
        if(!isGeneral){
            jobj.addProperty("email", email);
            jobj.addProperty("token",token);
            jobj.addProperty("type","google");
        }
        else{
            jobj.addProperty("username", username);
            jobj.addProperty("pass",new Utils().SHA256(pass));
            jobj.addProperty("type","general");
        }
        Ion.with(getContext())
                .load(BASE_API_SERVER+"/api/user/login")
                .setJsonObjectBody(jobj)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        pdialog.dismiss();
                        try {
                            JSONObject res = new JSONObject(result.toString());
                            boolean status = res.getBoolean("status");
                            if(status){
                                JSONObject obj = res.getJSONObject("data");

                                SharedPreferences sPreference = getActivity().getSharedPreferences("account", Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = sPreference.edit();
                                edit.putBoolean("status",true);
                                edit.putString("type",isGeneral ? "general" : "google");
                                edit.putString("username", obj.getString("username"));
                                edit.putString("email",obj.getString("email"));
                                edit.putString("phone",obj.getString("phone"));
                                Utils utils = new Utils();
                                edit.putString("token",isGeneral ? utils.SHA256(pass) : token);
                                edit.apply();

                                ((MainActivity)getActivity()).toggleAccount();
                            }
                            else{
                                JSONObject err = res.getJSONObject("err");
                                Toast.makeText(getContext(),err.getString("msg"),Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception ex){
                            ex.printStackTrace();
                            Toast.makeText(getContext(),"Network error",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
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
                Login(usernameField.getText().toString(),passwordField.getText().toString(),null,null,true);
                usernameField.setText(null);
                passwordField.setText(null);
                break;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }
}
