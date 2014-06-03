package com.test.recommand.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.test.recommand.model.RssType;
import com.test.recommand.view.MapsFragment;
import com.test.recommand.view.RestaurantListAdapter;

/**
 * Created by sooyoungbyun on 2014. 6. 2..
 */
public class MainActivity extends ActionBarActivity implements MapsFragment.OnUpdate {

    private MapsFragment mFragment;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            // map
            mFragment = new MapsFragment().setUpdateListener(this);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.map_fragment_container, mFragment)
                    .commit();
        }

        setContentView(R.layout.main_activity);
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    public void update(RssType rssType) {

        updateListView(rssType);
    }

    private void updateListView(RssType rssType) {
        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.restaurant_list_view);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        }

        RestaurantListAdapter adapter = new RestaurantListAdapter(this, R.layout.restaurant_item, rssType.getChannel().getItemList());
        mListView.setAdapter(adapter);
    }
}
