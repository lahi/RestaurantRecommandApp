package com.test.recommand.view;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.test.recommand.app.R;
import com.test.recommand.app.RestaurantActivity;
import com.test.recommand.model.ItemType;
import com.test.recommand.model.RssType;

import java.util.List;

/**
 * Created by sooyoungbyun on 2014. 6. 9..
 */
public class RestaurantMapFragment extends Fragment implements MapsFragment.OnUpdate, MapsFragment.OnMarkerClicked {
    private MapsFragment mMapFragment;
    private ListView mListView;
    private List<ItemType> restaurantList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate( R.layout.map_fragment, container, false);

        if (mMapFragment == null) {
            //map
            mMapFragment = new MapsFragment().setUpdateListener(this);
            mMapFragment.setUpdateMarkerClickListener(this);

            getFragmentManager().beginTransaction()
                    .add(R.id.map_fragment_container, mMapFragment)
                    .commit();
        }

        return rootView;
    }

    @Override
    public void update(RssType rssType) {

        restaurantList = rssType.getChannel().getItemList();
        updateListView();

    }

    private void updateListView() {
        if (mListView == null) {
            mListView = (ListView) getActivity().findViewById(R.id.restaurant_list_view);
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
                        Intent intent = new Intent(getActivity(), RestaurantActivity.class);
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

        RestaurantListAdapter adapter = new RestaurantListAdapter(getActivity(), R.layout.restaurant_item, restaurantList);
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
