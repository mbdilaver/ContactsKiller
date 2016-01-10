package lesson.mbd.com.contactskiller;

import android.content.Context;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by mbd on 30.12.2015.
 */
public class EditPhoneAdapter extends ArrayAdapter<PhoneNumber>{
    LayoutInflater mInflater;
    EditText mPhoneNumberEdit;
    PhoneNumber[] mNumbers;


    public EditPhoneAdapter(Context context, PhoneNumber[] numbers) {
        super(context, R.layout.edit_phone_layout, numbers);
        mNumbers = numbers;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.edit_phone_layout, parent, false);
        mPhoneNumberEdit = (EditText) view.findViewById(R.id.numberEditText);

        Spinner spinner = (Spinner) view.findViewById(R.id.typeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.phone_types, android.R.layout.simple_spinner_item);


        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final int type = getItem(position).getType();
        int home = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
        int work = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
        int mobile = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;

        int spinnerPosition;
        if (type == home)
            spinnerPosition = adapter.getPosition("Home");
        else if (type == work)
            spinnerPosition = adapter.getPosition("Work");
        else if (type == mobile)
            spinnerPosition = adapter.getPosition("Mobile");
        else spinnerPosition = 0;

        spinner.setAdapter(adapter);
        spinner.setSelection(spinnerPosition);

        mPhoneNumberEdit.setText(getItem(position).getNumber());

        mPhoneNumberEdit.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        return view;
    }

    public PhoneNumber[] getPhoneNumbers() {
        return mNumbers;
    }
}
