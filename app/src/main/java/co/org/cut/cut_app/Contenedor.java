package co.org.cut.cut_app;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;

import co.org.cut.cut_app.herramientas.MyVolley;
import co.org.cut.cut_app.modelos.MenuAdapter;
import co.org.cut.cut_app.modelos.MenuEntry;

public class Contenedor extends AppCompatActivity {

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
            R.drawable.ic_information
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
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
            actionBar.setDisplayShowTitleEnabled(false);
        }

        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment;


        switch (position){
            case 0:
                fragment= new NoticiasFragment();
                break;
            case 1:
                fragment= new NoticiasFragment();
                break;
            case 2:
                fragment= new HashTagTwitterFragment();
                Bundle args = new Bundle();
                args.putString(HashTagTwitterFragment.ARG_HASHTAG, "#twitterflock");
                fragment.setArguments(args);
                break;
            case 3:
                fragment= new ConsultarFragment();
                break;
            default:
                fragment= new InformacionFragment();
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
