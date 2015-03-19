package de.eatgate.placessearch.helpers;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.eatgate.placessearch.R;
import de.eatgate.placessearch.entities.Review;

/**
 * Created by Shi on 14.01.2015.
 */
public class ListViewAdapterRev extends ArrayAdapter<Review> {
    private Context context;
    private List<Review> reviews = new ArrayList<Review>();

    public ListViewAdapterRev(Context context, List<Review> reviews) {
        super(context, R.layout.list_item, reviews);
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_item_rev, parent, false);
        TextView inhalt = (TextView) row.findViewById(R.id.inhalt);
        inhalt.setText(reviews.get(position).getText());
        // ImageView icon = (ImageView) row.findViewById(R.id.iv_gender);
        // LinearLayout linLayout = (LinearLayout) row.findViewById(R.id.listPlacesLayout);
        //  TextView name = (TextView) row.findViewById(R.id.name);
        //TextView age = (TextView) row.findViewById(R.id.age_value);
        // TextView separator = (TextView) row.findViewById(R.id.seperator);

        //  name.setText(reviews.get(position).getPlace_id());

        return row;
    }
}