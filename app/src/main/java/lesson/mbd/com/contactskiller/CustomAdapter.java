package lesson.mbd.com.contactskiller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by M on 23.12.2015.
 */
public class CustomAdapter extends ArrayAdapter<Contact> implements StickyListHeadersAdapter{

    LayoutInflater mbdInflater;
    private String[] contactNames;

    public CustomAdapter(Context context, Contact[] contacts) {
        super(context, R.layout.contact_row_layout ,contacts);
        mbdInflater = LayoutInflater.from(getContext());
        contactNames = new String[contacts.length];
        for (int i = 0; i<contacts.length; i++) {
            contactNames[i] = contacts[i].getName();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View customView = mbdInflater.inflate(R.layout.contact_row_layout, parent, false);

        String name = getItem(position).getName();
        String id = getItem(position).getId();
        TextView nameText = (TextView) customView.findViewById(R.id.contactName);
        TextView idText = (TextView) customView.findViewById(R.id.contactId);

        nameText.setText(name);
        idText.setText(id);

        return customView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mbdInflater.inflate(R.layout.header_layout, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.headerTextView);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        String headerText = "" + contactNames[position].subSequence(0, 1).charAt(0);
        holder.text.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return contactNames[position].subSequence(0, 1).charAt(0);
    }

    class HeaderViewHolder {
        TextView text;
    }
}
