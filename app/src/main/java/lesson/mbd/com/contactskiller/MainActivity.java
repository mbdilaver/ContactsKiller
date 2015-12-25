package lesson.mbd.com.contactskiller;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Contact> mContacts = new ArrayList<Contact>();
    //private ListView mContactsListView;
    private StickyListHeadersListView mStickyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStickyList = (StickyListHeadersListView) findViewById(R.id.stickyListView);

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cursor.getCount() > 0) {
            // Get all contacts
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                // Get phone details for this contact
                Cursor phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id},
                        null);
                PhoneNumber number = new PhoneNumber();
                while (phoneCursor.moveToNext()) {
                    number.setNumber(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    number.setType(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
                }
                phoneCursor.close();
                // Add contact information to arrayList
                Contact contact = new Contact(id, name);
                contact.setMobileNumber(number);
                mContacts.add(contact);
            }
        }

        // ArrayList to array
        Contact[] mbdContacts = new Contact[mContacts.size()];
        for (int i = 0; i<mContacts.size(); i++) {
            Contact contact = mContacts.get(i);
            mbdContacts[i] = contact;
        }

        CustomAdapter mbdAdapter = new CustomAdapter(this, mbdContacts);
        mStickyList.setAdapter(mbdAdapter);

        mStickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView idText = (TextView) view.findViewById(R.id.contactId);
                String contactId = idText.getText() + "";
                String number = getPhoneNumber(contactId);
                Toast.makeText(MainActivity.this, number, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String getPhoneNumber(String id) {
        ContentResolver contentResolver = getContentResolver();
        Cursor phoneCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{id},
                null);
        String number = "";
        while (phoneCursor.moveToNext()) {
            number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        phoneCursor.close();
        return number;
    }
}
