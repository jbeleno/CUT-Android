package co.org.cut.cut_app;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import co.org.cut.cut_app.herramientas.App_cut;


public class InformacionFragment extends Fragment {

    public static Tracker mTracker;

    public InformacionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_informacion, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        String STR_VISTA = "Información";

        App_cut application = (App_cut) context.getApplicationContext();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(STR_VISTA);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
