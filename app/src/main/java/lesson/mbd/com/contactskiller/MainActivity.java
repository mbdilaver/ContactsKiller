package lesson.mbd.com.contactskiller;

import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.stickyListView) StickyListHeadersListView mStickyList;
    @Bind(R.id.searchEditText) EditText mSearchEditText;

    public static final String CONTACT_TAG = "CONTACT_TAG";
    private ArrayList<Contact> mContacts = new ArrayList<Contact>();



    // Database
    public MyDBHandler dbHandler;
    private boolean isFirstOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        dbHandler = new MyDBHandler(this, null, null, 1);
        dbHandler.updateSmsReceived("532 323 3232");

        updateList();

        mStickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView idText = (TextView) view.findViewById(R.id.contactId);
                String contactId = idText.getText() + "";
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                intent.putExtra(CONTACT_TAG, Integer.parseInt(contactId));
                startActivity(intent);
            }
        });

        mStickyList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView idText = (TextView) view.findViewById(R.id.contactId);
                if (idText.getVisibility() == View.INVISIBLE)
                    idText.setVisibility(View.VISIBLE);
                else
                    idText.setVisibility(View.INVISIBLE);

                return false;
            }
        });

        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0)
                    updateListWithSearchResults(s + "");
                else {
                    updateList();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void updateListWithSearchResults(String name) {
        mContacts = dbHandler.searchName(name);
        // ArrayList to array
        Contact[] mbdContacts = new Contact[mContacts.size()];
        for (int i = 0; i < mContacts.size(); i++) {
            Contact contact = mContacts.get(i);
            mbdContacts[i] = contact;
        }
        CustomAdapter mbdAdapter = new CustomAdapter(MainActivity.this, mbdContacts);
        mStickyList.setAdapter(mbdAdapter);

    }

    private void updateList() {
        mContacts = dbHandler.databaseToArrayList();
        // ArrayList to array
        final Contact[] mbdContacts = new Contact[mContacts.size()];
        for (int i = 0; i < mContacts.size(); i++) {
            Contact contact = mContacts.get(i);
            mbdContacts[i] = contact;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomAdapter mbdAdapter = new CustomAdapter(MainActivity.this, mbdContacts);
                mStickyList.setAdapter(mbdAdapter);
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

    public void updateDefaultContacts() {
        // Delete contacts which added from default phone book
        dbHandler.deleteDefaults();
        // Get contacts from default phone book
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Contact contact = new Contact(name);

                // Get phone details for this contact
                Cursor cur = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id},
                        null);

                while (cur.moveToNext()) {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) != null) {
                        int type = cur.getInt(cur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.TYPE));
                        String number = cur.getString(cur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        PhoneNumber phoneNumber = new PhoneNumber(number, type);
                        contact.addNumber(phoneNumber);
                    }
                }
                cur.close();
                contact.setIsFromDefaultContacts(true);

                // Add contact to database
                dbHandler.addContact(contact);
                // Update contact to store other information except name and id
                dbHandler.updateContact(contact);
                //dbHandler.deleteContact(contact);
            }
        }
        cursor.close();
    }

    @OnClick(R.id.newButton)
    public void newButtonClick() {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("CONTACT_ID", -1);
        startActivity(intent);
    }

    @OnClick(R.id.refreshButton)
    public void refreshButtonClick() {

        Handler mainHandler = new Handler(getMainLooper());

        final DialogFragment myDialogFragment = new myDialogFragment();
        myDialogFragment.show(getFragmentManager(), "loading");
        myDialogFragment.setCancelable(false);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                updateDefaultContacts();
                updateList();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Contacts updated from default phonebook", Toast.LENGTH_SHORT).show();
                        myDialogFragment.dismiss();
                    }
                });

            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("IS_FIRST_OPEN", isFirstOpen);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isFirstOpen = savedInstanceState.getBoolean("IS_FIRST_OPEN");
    }


}
