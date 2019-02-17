package com.brouken.wear.odorik.callback;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ConfigActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new ConfigPrefFragment())
                .commit();
    }

    public static class ConfigPrefFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs_config);

            Preference balancePreference = findPreference("balance");
            balancePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    preference.setEnabled(false);
                    try {
                        balance(getContext(), preference);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        }
    }

    private static void balance(final Context context, final Preference preference) throws UnsupportedEncodingException {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String user = sharedPreferences.getString("pref_user", "");
        final String password = sharedPreferences.getString("pref_password", "");

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.GET,
                "https://www.odorik.cz/api/v1/balance?user=" + URLEncoder.encode(user, StandardCharsets.UTF_8.name())
                        + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8.name())
                        + "&user_agent=" + URLEncoder.encode(BuildConfig.APPLICATION_ID, StandardCharsets.UTF_8.name()),
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                preference.setEnabled(true);
                if (!response.startsWith("error"))
                    response += " Kč";
                Toast.makeText(context, response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                preference.setEnabled(true);
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        queue.add(request);
    }
}
