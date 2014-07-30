package com.test.recommand.view;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.test.recommand.app.R;
import com.test.recommand.app.RestaurantActivity;
import com.test.recommand.model.ItemType;
import com.test.recommand.model.RssType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sooyoungbyun on 2014. 6. 9..
 */
public class RestaurantMapFragment extends Fragment implements MapsFragment.OnUpdate, MapsFragment.OnReset, MapsFragment.OnMarkerClicked {
    private MapsFragment mMapFragment;
    private ListView mListView;
    private List<ItemType> restaurantList;

    private int currPageNum;
    private boolean loadingMore;
    private int currPosittion;
    private int currSearchTotalNum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate( R.layout.map_fragment, container, false);

        if (mMapFragment == null) {
            //map
            mMapFragment = new MapsFragment().setUpdateListener(this);
            mMapFragment.setResetListener(this);
            mMapFragment.setUpdateMarkerClickListener(this);

            getFragmentManager().beginTransaction()
                    .add(R.id.map_fragment_container, mMapFragment)
                    .commit();

            currPageNum = 0;
            currPosittion = 0;
            loadingMore = false;
            currSearchTotalNum = 0;
        }

        return rootView;
    }

    @Override
    public void update(RssType rssType) {

        if (currPageNum == 0) {
            restaurantList = rssType.getChannel().getItemList();
            currSearchTotalNum = Integer.parseInt(rssType.getChannel().getTotal());
        }else {

            restaurantList.addAll(rssType.getChannel().getItemList());
        }

        updateListView();
    }

    @Override
    public void reset() {

        restaurantList = new ArrayList<ItemType>();
        currPageNum = 0;
        currPosittion = 0;
        currSearchTotalNum = 0;

        updateListView();
    }

    private void updateListView() {
        if (mListView == null) {
            initializeListView();
        }

        RestaurantListAdapter adapter = new RestaurantListAdapter(getActivity(), R.layout.restaurant_item, restaurantList);
        mListView.setAdapter(adapter);
        mListView.setSelection(currPosittion);

        loadingMore = false;
    }

    private void initializeListView() {

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

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //리스트가 없으면 리턴
                if (restaurantList.size() <= 0)
                    return;

                // load more
                int lastInScreen = firstVisibleItem + visibleItemCount;

                if ((lastInScreen == totalItemCount) && (loadingMore == false)) {

                    if (currSearchTotalNum == lastInScreen) //already loaded all
                        return;

                    loadingMore = true;
                    currPageNum++;
                    currPosittion = lastInScreen;
                    mMapFragment.requestRestaurantMore(currPageNum);
                }
            }
        });
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
