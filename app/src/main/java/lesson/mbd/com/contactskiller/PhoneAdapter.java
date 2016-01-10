package lesson.mbd.com.contactskiller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mbd on 29.12.2015.
 */
public class PhoneAdapter extends ArrayAdapter<PhoneNumber> {
    LayoutInflater mInflater;

    public PhoneAdapter(Context context, PhoneNumber[] numbers) {
        super(context, R.layout.phone_row_layout, numbers);
        mInflater = LayoutInflater.from(getContext());
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.phone_row_layout, parent, false);

        ImageButton callButton = (ImageButton) view.findViewById(R.id.callButton);
        ImageButton smsButton = (ImageButton) view.findViewById(R.id.smsButton);

        final String number = getItem(position).getNumber();
        int type = getItem(position).getType();

        final TextView pNumber = (TextView) view.findViewById(R.id.number);
        TextView pType = (TextView) view.findViewById(R.id.type);
        pNumber.setText(number);
        int home = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
        int work = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
        int mobile = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;

        if (type == home)
            pType.setText("Home");
        else if (type == work)
            pType.setText("Work");
        else if (type == mobile)
            pType.setText("Mobile");

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                getContext().startActivity(intent);
            }
        });

        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                        + number));
                //intent.setType("vnd.android-dir/mms-sms");
                getContext().startActivity(intent);
            }
        });

        return view;
    }
}
