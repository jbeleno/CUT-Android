package co.org.cut.cut_app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import co.org.cut.cut_app.herramientas.App_cut;

public class WebFragment extends Fragment {
    public static final String ARG_URL = "url";
    private String url;

    public static Tracker mTracker;

    public static WebFragment newInstance(String url) {
        WebFragment fragment = new WebFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    public WebFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_URL);

            String STR_VISTA = url;

            App_cut application = (App_cut) getActivity().getApplicationContext();
            mTracker = application.getDefaultTracker();
            mTracker.setScreenName(STR_VISTA);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_web, container, false);
        WebView vistaWeb = (WebView) view.findViewById(R.id.vista_web);
        final TextView mensaje = (TextView) view.findViewById(R.id.mensaje);

        vistaWeb.getSettings().setJavaScriptEnabled(true);
        vistaWeb .loadUrl(url);
        vistaWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mensaje.setVisibility(View.GONE);
            }
        });

        return view;
    }
}
