package lesson.mbd.com.contactskiller;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditActivity extends AppCompatActivity {

    @Bind(R.id.editPhoneList) ListView mEditPhoneList;
    @Bind(R.id.nameEditText) EditText mNameEdit;
    @Bind(R.id.mailEditText) EditText mMailEdit;

    @Bind(R.id.deleteButton) Button mDeleteButton;

    private int id;
    private Contact mContact;
    private MyDBHandler dbHandler;
    private EditPhoneAdapter adapter;
    private ArrayList<PhoneNumber> mNumbersList;
    private PhoneNumber[] numbers;
    private boolean isNewContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        dbHandler = new MyDBHandler(this, null, null, 1);

        Intent intent = getIntent();
        id = intent.getIntExtra("CONTACT_ID", -1);
        if (id != -1)
            isNewContact = false;
        else
            isNewContact = true;

        // Existing contact
        if (!isNewContact) {
            mContact = dbHandler.getSingleContact(id);

            mNameEdit.setText(mContact.getName());
            if (mContact.getMail() != null)
                mMailEdit.setText(mContact.getMail());

            mNumbersList = mContact.getNumbers();
            numbers = new PhoneNumber[mContact.getNumbers().size()];
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = mContact.getNumbers().get(i);
            }
        }
        // New contact
        else {
            mContact = new Contact();
            PhoneNumber number = new PhoneNumber();
            numbers = new PhoneNumber[1];
            numbers[0] = number;
            mDeleteButton.setVisibility(View.GONE);
        }
        adapter = new EditPhoneAdapter(this, numbers);
        mEditPhoneList.setAdapter(adapter);

    }

    @OnClick(R.id.saveButton)
    public void onSaveButtonClick() {
        String newName = mNameEdit.getText() + "";
        if (newName.isEmpty())
            Toast.makeText(this, "Oops you forgot entering the name!", Toast.LENGTH_LONG).show();
        else {
            String newMail = mMailEdit.getText() + "";

            if (mContact.isFromDefaultContacts() == true) {
                mContact.setIsFromDefaultContacts(false);
            }

            ArrayList<PhoneNumber> numberList = getAllNumbers();
            if (numberList != null) {
                mContact.setNumbers(numberList);
            }
            newName = newName.substring(0,1).toUpperCase() + newName.substring(1);
            mContact.setName(newName);
            mContact.setMail(newMail);
            if (isNewContact) {
                dbHandler.addContact(mContact);
            }
            dbHandler.updateContact(mContact);
            Intent intent = new Intent(EditActivity.this, ContactActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(MainActivity.CONTACT_TAG, mContact.getId());
            startActivity(intent);
            //finish();
        }
    }

    @OnClick(R.id.addPhoneButton)
    public void onAddPhoneButtonClick() {
        mNumbersList = getAllNumbers();
        PhoneNumber number = new PhoneNumber();
        mNumbersList.add(0, number);
        numbers = new PhoneNumber[mNumbersList.size()];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = mNumbersList.get(i);
        }
        adapter = new EditPhoneAdapter(this, numbers);
        mEditPhoneList.setAdapter(adapter);
    }

    private ArrayList<PhoneNumber> getAllNumbers() {
        ArrayList<PhoneNumber> numberList = new ArrayList<PhoneNumber>();
        for (int i = 0; i < adapter.getCount(); i++) {
            View view = mEditPhoneList.getChildAt(i);
            EditText editText = (EditText) view.findViewById(R.id.numberEditText);
            Spinner spinner = (Spinner) view.findViewById(R.id.typeSpinner);
            String number = editText.getText() + "";
            if (!number.isEmpty()) {
                String type = (String) spinner.getSelectedItem();
                int typeCode;
                switch (type) {
                    case "Home":
                        typeCode = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
                        break;
                    case "Work":
                        typeCode = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
                        break;
                    case "Mobile":
                        typeCode = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
                        break;
                    default:
                        typeCode = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
                        ;
                        break;
                }
                PhoneNumber phoneNumber = new PhoneNumber(number, typeCode);
                numberList.add(phoneNumber);
            }
        }
        return numberList;
    }

    @OnClick(R.id.deleteButton)
    public void onDeleteButtonClick() {
        dbHandler.deleteContact(mContact);
        Intent intent = new Intent(EditActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Toast.makeText(this, "Contact deleted!", Toast.LENGTH_SHORT).show();
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


}
