package lesson.mbd.com.contactskiller;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ContactActivity extends AppCompatActivity {

    private MyDBHandler dbHandler;

    @Bind(R.id.phoneListView) ListView mList;

    @Bind(R.id.workLocation) View mWorkLocation;
    @Bind(R.id.homeLocation) View mHomeLocation;

    @Bind(R.id.editButton) Button mEditButton;

    @Bind(R.id.mailText) TextView mMailText;
    @Bind(R.id.nameText) TextView mNameText;
    @Bind(R.id.missingCallText) TextView mMissingCallText;
    @Bind(R.id.incomingCallText) TextView mIncomingCallText;
    @Bind(R.id.outgoingCallText) TextView mOutgoingCallText;
    @Bind(R.id.smsText) TextView mSmsText;


    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);

        dbHandler = new MyDBHandler(this, null, null, 1);
        Intent intent = getIntent();
        id = intent.getIntExtra(MainActivity.CONTACT_TAG, -1);
        Contact contact;
        if (id != -1) {
            contact = dbHandler.getSingleContact(id);

            int missingCallCount = contact.getMissingCallCount();
            int incomingCallDuration = contact.getIncomingCallTime();
            int incomingCallCount = contact.getIncomingCallCount();
            int outgoingCallDuration = contact.getOutgoingCallTime();
            int outgoingCallCount = contact.getOutgoingCallCount();
            int sendSmsCount = contact.getSendMessageCount();
            int receivedSmsCount = contact.getReceivedMessageCount();
            String name = contact.getName();
            String mail = contact.getMail();

            mNameText.setText(name);

            if (mail != null && isEmailValid(mail))
                mMailText.setText(mail);
            else
                mMailText.setVisibility(View.GONE);

            if (missingCallCount <= 0)
                mMissingCallText.setVisibility(View.GONE);
            else
                mMissingCallText.setText(missingCallCount + " missing call(s)");

            if (incomingCallCount <= 0)
                mIncomingCallText.setVisibility(View.GONE);
            else
                mIncomingCallText.setText("Incoming call duration is " + incomingCallDuration + " min(s) within " + incomingCallCount + " calls");

            if (outgoingCallCount <= 0)
                mOutgoingCallText.setVisibility(View.GONE);
            else
                mOutgoingCallText.setText("Outgouing call duration is " + outgoingCallDuration + " min(s) within " + outgoingCallCount + " calls");

            if (sendSmsCount <= 0 && receivedSmsCount <= 0)
                mSmsText.setVisibility(View.GONE);
            else
                mSmsText.setText("Sent messages: " + sendSmsCount + " Received messages: " + receivedSmsCount);


            if (contact.getNumbers() != null) {
                PhoneNumber[] numbers = new PhoneNumber[contact.getNumbers().size()];
                for (int i = 0; i < contact.getNumbers().size(); i++) {
                    numbers[i] = contact.getNumbers().get(i);
                }
                PhoneAdapter phoneAdapter = new PhoneAdapter(this, numbers);
                mList.setAdapter(phoneAdapter);
            }
        }

        mWorkLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri gmmIntentUri = Uri.parse("google.navigation:q=41.015137,28.979530");

                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
            }
        });

        mHomeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri gmmIntentUri = Uri.parse("google.navigation:q=41.015137,28.979530");

                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, EditActivity.class);
                intent.putExtra("CONTACT_ID", id);
                startActivity(intent);
            }
        });
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ContactActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
