package co.org.cut.cut_app;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import co.org.cut.cut_app.herramientas.App_cut;
import io.fabric.sdk.android.Fabric;


public class UserAccountTwitterFragment extends ListFragment {
    public static final String ARG_USER_ACCOUNT = "cuenta";
    private String cuenta;

    public static Tracker mTracker;

    public UserAccountTwitterFragment() {
        TwitterAuthConfig authConfig =  new TwitterAuthConfig("S1MLsZpnXYWI0IOlP3wdxz1Q5", "KLxu6F6sObGLKYQzejD7G6UFRhbXCbrhw9V8duxRspSpI7DKO6");
        Fabric.with(getContext(), new Twitter(authConfig));
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cuenta = getArguments().getString(ARG_USER_ACCOUNT);

            String STR_VISTA = cuenta;

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
        View view =  inflater.inflate(R.layout.fragment_twitter, container, false);

        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_twitter);

        final UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName(cuenta)
                .build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(getActivity())
                .setTimeline(userTimeline)
                .build();
        setListAdapter(adapter);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                adapter.refresh(new Callback<TimelineResult<Tweet>>() {
                    @Override
                    public void success(Result<TimelineResult<Tweet>> result) {
                        swipeLayout.setRefreshing(false);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.d("Error", exception.getMessage());
                    }
                });
            }
        });

        return view;
    }
}
