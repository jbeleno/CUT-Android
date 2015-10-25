package co.org.cut.cut_app.herramientas;

import android.app.Application;
import android.content.Context;

public class App_cut extends Application {
    private static Context context;
    private static final String TAG ="CUT_DEBUG";

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
}
