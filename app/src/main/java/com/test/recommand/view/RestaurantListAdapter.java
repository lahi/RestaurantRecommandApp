package com.test.recommand.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.recommand.app.R;
import com.test.recommand.model.ItemType;

import java.util.List;

/**
 * Created by sooyoungbyun on 2014. 6. 3..
 */
public class RestaurantListAdapter extends BaseAdapter {

    private Context context;
    private int resource;
    List<ItemType> items;

    public RestaurantListAdapter(Context context, int resource, List<ItemType> itemList) {
        this.context = context;
        this.resource = resource;
        this.items = itemList;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder h;

        if (convertView == null) {
            h = new Holder();
            convertView = View.inflate(context, resource, null);
            convertView.setTag(h);

            h.name = (TextView) convertView.findViewById(R.id.restaurant_item_name);
        } else {
            h = (Holder) convertView.getTag();
        }

        ItemType model = (ItemType) getItem(position);
        if (model == null) {
            return convertView;
        }

        h.name.setText(model.getTitle());

        return convertView;
    }

    private class Holder {
        TextView name;
    }
}
