package co.org.cut.cut_app;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;


public class HashTagTwitterFragment extends ListFragment {
    public static final String ARG_HASHTAG = "hashtag";
    private String hashtag;

    public HashTagTwitterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hashtag = getArguments().getString(ARG_HASHTAG);
        }

        /*TwitterAuthConfig authConfig =  new TwitterAuthConfig("S1MLsZpnXYWI0IOlP3wdxz1Q5", "KLxu6F6sObGLKYQzejD7G6UFRhbXCbrhw9V8duxRspSpI7DKO6");
        Fabric.with(getActivity(), new TwitterCore(authConfig), new TweetUi());*/


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_hash_tag_twitter, container, false);

        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_twitter);

        final SearchTimeline searchTimeline = new SearchTimeline.Builder()
                .query(hashtag)
                .build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(getActivity())
                .setTimeline(searchTimeline)
                .build();
        setListAdapter(adapter);

        Log.d("Hashtag", hashtag);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                adapter.refresh(new Callback<TimelineResult<Tweet>>() {
                    @Override
                    public void success(Result<TimelineResult<Tweet>> result) {
                        Log.d("Success", result.response.getBody().toString());
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
