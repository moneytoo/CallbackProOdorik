package com.brouken.wear.odorik.callback;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class CallbackActivity extends Activity {

    TextView mPhoneTextView;
    SharedPreferences mSharedPreferences;

    String mUser;
    String mPassword;
    String mCaller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhoneTextView = findViewById(R.id.phone);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String phone = mSharedPreferences.getString("phone", "");
        mPhoneTextView.setText(phone);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mUser = mSharedPreferences.getString("pref_user", "");
        mPassword = mSharedPreferences.getString("pref_password", "");
        mCaller = mSharedPreferences.getString("pref_caller", "");
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("phone", mPhoneTextView.getText().toString());
        editor.apply();
    }

    public void send(final View view) {
        view.setEnabled(false);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST,"https://www.odorik.cz/api/v1/callback", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                view.setEnabled(true);
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.setEnabled(true);
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("user_agent", BuildConfig.APPLICATION_ID);
                params.put("user", mUser);
                params.put("password", mPassword);
                params.put("caller", mCaller);
                params.put("recipient", mPhoneTextView.getText().toString());
                return params;
            }
        };

        queue.add(request);
    }

    public void openConfig(final View view) {
        Intent intent = new Intent(this, ConfigActivity.class);
        startActivity(intent);
    }
}
