package com.test.recommand.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.test.recommand.model.ItemType;
import com.test.recommand.model.RssType;
import com.test.recommand.view.MapsFragment;
import com.test.recommand.view.RestaurantListAdapter;

import java.util.List;

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

    private void updateListView(final RssType rssType) {
        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.restaurant_list_view);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    List<ItemType> restaurantList = rssType.getChannel().getItemList();
                    ItemType item = restaurantList.get(position);
                    Log.d("ag", "link : " + item.getLink());

                    try {
                        String openLink = item.getLink();

                        if (openLink == null) {
                            openLink = Uri.parse("http://search.naver.com/search.naver")
                                    .buildUpon()
                                    .appendQueryParameter("query", item.getTitle())
                                    .build().toString();
                        }

                        // open
                        Intent intent = new Intent(MainActivity.this, RestaurantActivity.class);
                        intent.putExtra("openUrl", openLink);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        RestaurantListAdapter adapter = new RestaurantListAdapter(this, R.layout.restaurant_item, rssType.getChannel().getItemList());
        mListView.setAdapter(adapter);
    }
}
