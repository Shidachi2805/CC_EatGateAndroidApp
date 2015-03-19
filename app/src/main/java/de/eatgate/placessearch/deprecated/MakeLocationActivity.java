package de.eatgate.placessearch.deprecated;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import de.eatgate.placessearch.R;
import de.eatgate.placessearch.activities.SinglePlacesActivity;

public class MakeLocationActivity extends ActionBarActivity {

    private AlertDialog.Builder builder = null;
    private AlertDialog dialog = null;

    private void buildInfoDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("About");
        builder.setMessage("EatGate Version 1.0 2015");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Abbruch", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // nichts weiter tun; Dialog schließen
                dialog.dismiss();
            }
        });
        builder.setCancelable(false); // nicht schließen mit ZURÜCK-Button
        dialog = builder.create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_location);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_ex, menu);
        ActionBar bar = getActionBar(); // or MainActivity.getInstance().getActionBar()
        // Alternative, falls XML-Styles fuer Actionbar nicht funktionieren via Code
        // Style der Actionbar veraendern
        // bar.setBackgroundDrawable(new ColorDrawable(0xff00DDED));
        TextView titleView = (TextView) findViewById(R.id.action_bar_title);
        if (titleView == null) {
            int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
            titleView = (TextView) findViewById(titleId);
        }
        titleView.setText("EatGate");
        titleView.setTextColor(Color.WHITE);
        // another solution which did work
        // Spannable text = new SpannableString(bar.getTitle());
        // text.setSpan(new ForegroundColorSpan(Color.BLUE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        // bar.setTitle(text);
        // alternative Loesung, die funktioniert
        // bar.setTitle((Html.fromHtml("<font color=\"#FF4444\">" + "EatGate"+ "</font>")));
        bar.setDisplayShowTitleEnabled(false);  // required to force redraw, without, gray color
        bar.setDisplayShowTitleEnabled(true);
        return true;
    }

    @Override
    /**
     * reagiert auf die Klicks in der Actionbar
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        View menuItemView = findViewById(R.id.action_menu);
        if (id == R.id.action_menu) {
            onClickMainMenu(menuItemView);
        } else if (id == R.id.action_back) {
            Intent intent = new Intent(this, SinglePlacesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Erzeugt das Popup Menu und registriert den ClickListener fuer das Submenu
     *
     * @param v
     */
    public void onClickMainMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main_popsub, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_info:
                        dialog.show();
                        return true;
                    case R.id.action_close:
                        // toDo SinglePlacesActivity beenden zusätzlich
                        finish();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

}
