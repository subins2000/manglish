package subins2000.manglish;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContributorListAdapter extends ArrayAdapter<String> {

    public final Activity context;
    public final String[] name;
    public final String[] website;

    public ContributorListAdapter(Activity context, String[] name, String[] website) {
        super(context, R.layout.item_list_contributor, name);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.name = name;
        this.website = website;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.item_list_contributor, null,true);

        TextView contributorName = rowView.findViewById(R.id.contributor_name);
        contributorName.setText(name[position]);

        return rowView;

    };
}