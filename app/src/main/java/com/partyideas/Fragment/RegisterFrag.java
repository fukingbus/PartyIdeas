package com.partyideas.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.partyideas.R;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFrag extends Fragment {
    private String BASE_API_SERVER;
    private boolean isHavingPreset = false;
    private String presetEmail;
    private String presetName;

    private EditText usernameField;
    private EditText passwordField;
    private EditText emailField;
    private EditText phoneField;
    private Button registerButton;

    public RegisterFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BASE_API_SERVER  = getResources().getString(R.string.base_api_server);
        if(getArguments()!=null){
            isHavingPreset = true;
            Bundle arg = getArguments();
            presetEmail = arg.getString("email");
            presetName = arg.getString("name");
        }
        View root = inflater.inflate(R.layout.fragment_register, container, false);
        usernameField = (EditText)root.findViewById(R.id.usernameField);
        passwordField = (EditText) root.findViewById(R.id.passwordField);
        emailField = (EditText)root.findViewById(R.id.emailField);
        phoneField = (EditText) root.findViewById(R.id.phoneField);
        registerButton = (Button) root.findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameField.getText().length()<5 && !isAlphanumeric(usernameField.getText().toString()))
                    usernameField.setError("Username must be alphanumeric and longer than 5 characters");
                else if(passwordField.getText().length()<8 && !isAlphanumeric(passwordField.getText().toString()))
                    passwordField.setError("Password must be alphanumeric and longer than 8 characters");
                else if(phoneField.getText().length()<8)
                    phoneField.setError("Phone number must longer than 8 characters");
                else if(emailField.getText().length()<8)
                    emailField.setError("Email must longer than 8 characters");
                else
                    processRegister();
            }
        });
        if(isHavingPreset){
            usernameField.setText(presetEmail.substring(0,presetEmail.indexOf("@")));
            emailField.setText(presetEmail);
        }
        return root;
    }
    private boolean isAlphanumeric(String str){
        return str.matches("[A-Za-z0-9]+");
    }
    private void processRegister(){
        JsonObject json = new JsonObject();
        json.addProperty("username", usernameField.getText().toString());
        json.addProperty("pass",passwordField.getText().toString());
        json.addProperty("email",emailField.getText().toString());
        json.addProperty("phone",phoneField.getText().toString());
        json.addProperty("accType",isHavingPreset ? "google" : "general");

        Ion.with(getContext())
                .load(BASE_API_SERVER+"/api/user")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        try {
                            JSONObject res = new JSONObject(result.toString());
                            boolean status = res.getBoolean("status");
                            if(status){
                                Log.e("Login","successful");
                            }
                            else{
                                JSONObject err = res.getJSONObject("err");
                                Toast.makeText(getContext(),err.getString("msg"),Toast.LENGTH_SHORT).show();
                                Log.e("Login",err.toString());
                            }
                        }
                        catch (Exception ex){
                            ex.printStackTrace();
                            Toast.makeText(getContext(),"Network error",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}
