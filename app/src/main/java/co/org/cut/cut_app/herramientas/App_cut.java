package co.org.cut.cut_app.herramientas;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

public class App_cut extends Application {
    private static Context context;
    private static final String TAG ="CUT_DEBUG";
    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        App_cut.context = getApplicationContext();

        init();
    }
    public static String getTag(){ return App_cut.TAG; }

    public static Context getAppContext() {
        return App_cut.context;
    }

    private void init() {
        MyVolley.init(this);
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            analytics.setLocalDispatchPeriod(1800);

            mTracker = analytics.newTracker("UA-69824348-1");
            mTracker.enableExceptionReporting(true);
        }
        return mTracker;
    }
}
