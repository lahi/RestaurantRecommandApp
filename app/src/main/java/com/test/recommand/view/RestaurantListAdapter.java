package com.test.recommand.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.recommand.app.R;
import com.test.recommand.model.ItemType;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by sooyoungbyun on 2014. 6. 3..
 */
public class RestaurantListAdapter extends BaseAdapter {

    private Context context;
    private int resource;
    private int selectedPosition;
    List<ItemType> items;

    public RestaurantListAdapter(Context context, int resource, List<ItemType> itemList) {
        this.context = context;
        this.resource = resource;
        this.items = itemList;
        this.selectedPosition = -1;
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

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder h;

        if (convertView == null) {
            h = new Holder();
            convertView = View.inflate(context, resource, null);
            convertView.setTag(h);

            h.name = (TextView) convertView.findViewById(R.id.restaurant_item_name);
            h.category = (TextView) convertView.findViewById(R.id.rescourant_category);
        } else {
            h = (Holder) convertView.getTag();
        }

        ItemType model = (ItemType) getItem(position);
        if (model == null) {
            return convertView;
        }

        h.name.setBackgroundResource(R.color.white);
        h.name.setTextColor(context.getResources().getColor(R.color.common_signin_btn_text_light));
        h.category.setBackgroundResource(R.color.white);
        h.category.setTextColor(context.getResources().getColor(R.color.common_action_bar_splitter));

        if (selectedPosition == position) {

            Integer colorFrom = context.getResources().getColor(R.color.white);
            Integer colorTo = context.getResources().getColor(R.color.darkorange);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    h.name.setBackgroundColor((Integer)animator.getAnimatedValue());
                    h.name.setTextColor(context.getResources().getColor(R.color.white));
                    h.category.setBackgroundColor((Integer)animator.getAnimatedValue());
                    h.category.setTextColor(context.getResources().getColor(R.color.white));
                }

            });
            colorAnimation.start();
        }

        h.name.setText(model.getTitle());
        h.category.setText(model.getCategory());


        return convertView;
    }

    private class Holder {
        TextView name;
        TextView category;
    }
}
