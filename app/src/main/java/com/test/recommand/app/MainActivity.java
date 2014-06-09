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
public class MainActivity extends ActionBarActivity implements MapsFragment.OnUpdate, MapsFragment.OnMarkerClicked {

    private MapsFragment mFragment;
    private ListView mListView;
    private List<ItemType> restaurantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            // map
            mFragment = new MapsFragment().setUpdateListener(this);
            mFragment.setUpdateMarkerClickListener(this);

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

        restaurantList = rssType.getChannel().getItemList();
        updateListView();

    }

    private void updateListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.restaurant_list_view);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    ItemType item = restaurantList.get(position);
                    String openLink = item.getLink();

                    try {
                        if (openLink == null) {
                            openLink = Uri.parse("http://cafeblog.search.naver.com/search.naver")
                                    .buildUpon()
                                    .appendQueryParameter("query", item.getTitle() + " " + item.getCategory())
                                    .appendQueryParameter("where", "post")
                                    .appendQueryParameter("ie", "utf8")
                                    .build().toString();
                        }

                        // open
                        Intent intent = new Intent(MainActivity.this, RestaurantActivity.class);
                        intent.putExtra("openUrl", openLink);
                        startActivity(intent);

                        //reset
                        mListView.setSelection(-1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        RestaurantListAdapter adapter = new RestaurantListAdapter(this, R.layout.restaurant_item, restaurantList);
        mListView.setAdapter(adapter);
    }

    @Override
    public void updateList(String title) {
        Log.d("activity", "title : " + title);

        for (int i = 0; i < restaurantList.size(); i++) {
            ItemType item = restaurantList.get(i);
            if (item.getTitle().equals(title)) {

                ((RestaurantListAdapter)mListView.getAdapter()).setSelectedPosition(i);
                mListView.setSelection(((i-2) < 0 )? i : i-2);
                mListView.requestFocus();

                break;
            }
        }
    }
}
