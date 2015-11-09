/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.org.cut.cut_app.gcm;

        import android.app.IntentService;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.preference.PreferenceManager;
        import android.support.v4.content.LocalBroadcastManager;
        import android.util.Log;

        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.StringRequest;
        import com.google.android.gms.gcm.GcmPubSub;
        import com.google.android.gms.gcm.GoogleCloudMessaging;
        import com.google.android.gms.iid.InstanceID;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.IOException;
        import java.util.HashMap;
        import java.util.Map;

        import co.org.cut.cut_app.R;
        import co.org.cut.cut_app.herramientas.App_cut;
        import co.org.cut.cut_app.herramientas.MyVolley;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    private static final String URL_REGISTRO = "http://52.27.16.14/cut/usuarios/registrar";
    private static final String PLATAFORMA = "android";
    private static final String ARG_PLATAFORMA ="plataforma";
    private static final String ARG_TOKEN ="push_id";
    private static final String STR_ESTADO = "status";
    private static final String STR_ESTADO_OK = "OK";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            sendRegistrationToServer(token, sharedPreferences);

            // Subscribe to topic channels
            subscribeTopics(token);

            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token, SharedPreferences sharedPreferences) {
        final Boolean enviado = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);

        if(!enviado){
            RequestQueue queue = MyVolley.getRequestQueue();

            StringRequest myReq = new StringRequest(Request.Method.POST,
                    URL_REGISTRO,
                    successRegistro(sharedPreferences),
                    errorConexion()) {
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(ARG_PLATAFORMA, PLATAFORMA);
                    params.put(ARG_TOKEN, token);
                    return params;
                }
            };
            queue.add(myReq);
        }
    }

    private Response.Listener<String> successRegistro(final SharedPreferences sharedPreferences) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObj = new JSONObject(response);
                    Log.d(App_cut.getTag(), jsonObj.toString());
                    if(jsonObj.get(STR_ESTADO).equals(STR_ESTADO_OK)){
                        // You should store a boolean that indicates whether the generated token has been
                        // sent to your server. If the boolean is false, send the token to your server,
                        // otherwise your server should have already received the token.
                        sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
                    }
                }catch (JSONException e){
                    Log.e(App_cut.getTag(), e.getMessage());
                }
            }
        };
    }

    //Se reciben los errores que puedan ocurrir por parte del servidor
    private Response.ErrorListener errorConexion() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            //Se estalla por un error 404 o 500 en el servicio
            Log.e(App_cut.getTag(), error.toString());
            }
        };
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}
