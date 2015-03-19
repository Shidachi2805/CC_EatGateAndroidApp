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
import de.eatgate.placessearch.entities.Place;

/**
 * Created by Shi on 14.01.2015.
 */
public class ListViewAdapter extends ArrayAdapter<Place> {
    private Context context;
    private List<Place> places = new ArrayList<Place>();
    public ListViewAdapter(Context context, List<Place> places) {
        super(context, R.layout.list_item, places);
        this.context = context;
        this.places = places;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_item, parent, false);

       // ImageView icon = (ImageView) row.findViewById(R.id.iv_gender);
       // LinearLayout linLayout = (LinearLayout) row.findViewById(R.id.listPlacesLayout);
        TextView name = (TextView) row.findViewById(R.id.name);
        //TextView age = (TextView) row.findViewById(R.id.age_value);
        // TextView separator = (TextView) row.findViewById(R.id.seperator);

        name.setText(places.get(position).getPlace_id());

        return row;
    }
}