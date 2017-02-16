package co.org.cut.cut_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import co.org.cut.cut_app.gcm.QuickstartPreferences;
import co.org.cut.cut_app.gcm.RegistrationIntentService;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;

import co.org.cut.cut_app.herramientas.MyVolley;
import co.org.cut.cut_app.modelos.MenuAdapter;
import co.org.cut.cut_app.modelos.MenuEntry;

public class Contenedor extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "Contenedor";

    private static final String ARG_TYPE = "tipo";
    private static final String ARG_ID = "id";

    private static final String ARG_TYPE_EVENT = "evento";
    private static final String ARG_TYPE_NEW = "noticia";

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "RQPE9mCfd664XDxEpSmXd7eIg";
    private static final String TWITTER_SECRET = "trDfSOZt2sCRJukHJVObou58m6eQjZOS6K2UxZBnMYADliNiia";

    private String[] mMenuTitles;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerContainer;
    private ActionBarDrawerToggle mDrawerToggle;

    private ArrayList<MenuEntry> entradas = new ArrayList<>();

    Integer[] imagesMenu={
            R.drawable.ic_newspaper,
            R.drawable.ic_calendar_text,
            R.drawable.ic_twitter,
            R.drawable.ic_help_circle,
            R.drawable.ic_information,
            R.drawable.ic_television_guide
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());
        setContentView(R.layout.activity_contenedor);

        MyVolley.init(this);

        // As we're using a Toolbar, we should retrieve it and set it
        // to be our ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        //El menú se carga por encima
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setDisplayShowTitleEnabled(true);
        }

        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("NOTICIAS");
        }

        // Se inician el GCM
        initGCM();

        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = new NoticiasFragment();

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, null)
                .commit();

        mMenuTitles = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        mDrawerContainer = (LinearLayout) findViewById(R.id.menu_container);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        for(int i = 0; i < mMenuTitles.length; i++){
            entradas.add(new MenuEntry(imagesMenu[i],mMenuTitles[i]));
        }

        MenuAdapter adaptador =new MenuAdapter(this,0, entradas);

        // Set the adapter for the list view
        mDrawerList.setAdapter(adaptador);

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.drawer_open, R.string.drawer_close);

        // Set the drawer toggle as the DrawerListener
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //Se reciben las notificaciones y se abre el fragment adecuado
        if(getIntent().getExtras()!=null){

            if(getIntent().getStringExtra(ARG_TYPE) != null){
                if(getIntent()
                        .getStringExtra(ARG_TYPE)
                        .equals(ARG_TYPE_EVENT)){

                    fragment = new EventoFragment();
                    Bundle args = new Bundle();
                    args.putString(EventoFragment.ARG_ID_EVENTO, getIntent().getStringExtra(ARG_ID));
                    fragment.setArguments(args);

                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment, null)
                            .addToBackStack(null)
                            .commit();
                }else if(getIntent()
                        .getStringExtra(ARG_TYPE)
                        .equals(ARG_TYPE_NEW)){

                    // Machete Alert: Aquí hay marcas de Scalibur, uso el ARG_ID
                    // para pasar la URL
                    fragment = new WebFragment();
                    Bundle args = new Bundle();
                    args.putString(WebFragment.ARG_URL, getIntent().getStringExtra(ARG_ID));
                    fragment.setArguments(args);

                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment, null)
                            .addToBackStack(null)
                            .commit();
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Se reciben las notificaciones y se abre el fragment adecuado
        if(intent.getExtras()!=null){

            if(intent.getStringExtra(ARG_TYPE) != null){
                if(intent
                        .getStringExtra(ARG_TYPE)
                        .equals(ARG_TYPE_EVENT)){

                    Fragment fragmento = new EventoFragment();
                    Bundle args = new Bundle();
                    args.putString(EventoFragment.ARG_ID_EVENTO, intent.getStringExtra(ARG_ID));
                    fragmento.setArguments(args);

                    FragmentManager fragmentoManager = getSupportFragmentManager();
                    fragmentoManager.beginTransaction()
                            .add(R.id.content_frame, fragmento, null)
                            .addToBackStack(null)
                            .commit();
                }else if(intent
                        .getStringExtra(ARG_TYPE)
                        .equals(ARG_TYPE_NEW)){

                    // Machete Alert: Aquí hay marcas de Scalibur, uso el ARG_ID
                    // para pasar la URL
                    Fragment fragmento = new WebFragment();
                    Bundle args = new Bundle();
                    args.putString(WebFragment.ARG_URL, intent.getStringExtra(ARG_ID));
                    fragmento.setArguments(args);

                    FragmentManager fragmentoManager = getSupportFragmentManager();
                    fragmentoManager.beginTransaction()
                            .add(R.id.content_frame, fragmento, null)
                            .addToBackStack(null)
                            .commit();
                }
            }
        }
    }

    public void initGCM(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.i(TAG, "Token enviado");
                } else {
                    Log.i(TAG, "Problema en token");
                }
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setTitle(CharSequence title) {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        // Create a new fragment and specify the planet to show based on position
        Fragment fragment;

        switch (position){
            case 0:
                fragment= new NoticiasFragment();
                break;
            case 1:
                fragment= new EventosFragment();
                break;
            case 2:
                /*
                This code is not working
                fragment= new HashTagTwitterFragment();
                Bundle args = new Bundle();
                args.putString(HashTagTwitterFragment.ARG_HASHTAG, "#twitterflock");
                fragment.setArguments(args);*/
                fragment= new UserAccountTwitterFragment();
                Bundle args = new Bundle();
                args.putString(UserAccountTwitterFragment.ARG_USER_ACCOUNT, "@cutcolombia");
                fragment.setArguments(args);
                break;
            case 3:
                fragment= new ConsultarFragment();
                break;
            case 4:
                fragment= new InformacionFragment();
                break;
            default:
                fragment = new TVFragment();
                Bundle arguments = new Bundle();
                arguments.putString(TVFragment.ARG_URL, "http://tv.cut.org.co/");
                fragment.setArguments(arguments);
                break;
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mMenuTitles[position]);
        }
        mDrawerLayout.closeDrawer(mDrawerContainer);
    }
}
