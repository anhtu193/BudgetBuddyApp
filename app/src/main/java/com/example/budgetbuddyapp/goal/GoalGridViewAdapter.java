package com.example.budgetbuddyapp.goal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.budgetbuddyapp.R;

public class GoalGridViewAdapter extends BaseAdapter {
    Context context;
    int[] images;

    LayoutInflater inflater;

    public GoalGridViewAdapter(Context context, int[] images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null)
        {
            view = inflater.inflate(R.layout.goal_grid_item, null);
        }

        ImageView imageView = view.findViewById(R.id.grid_image);
        imageView.setImageResource(images[position]);

        return view;
    }
}
