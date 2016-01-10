package lesson.mbd.com.contactskiller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by mbd on 26.12.2015.
 */
public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION         = 10;
    private static final String DATABASE_NAME         = "contacs.db";
    private static final String TABLE_CONTACTS        = "contacts";
    private static final String COLUMN_ID             = "_id";
    private static final String COLUMN_CONTACTNAME    = "contactname";
    private static final String COLUMN_FROM_DEFAULT   = "isfromdefault";
    private static final String COLUMN_MAIL           = "mail";
    private static final String COLUMN_MISSCALL_COUNT = "missingcallcount";
    private static final String COLUMN_INCALL_TIME    = "incomingcalltime";
    private static final String COLUMN_INCALL_COUNT   = "incomingcallcount";
    private static final String COLUMN_OUTCALL_TIME   = "outgoingcalltime";
    private static final String COLUMN_OUTCALL_COUNT  = "outgoingcallcount";
    private static final String COLUMN_SENT_MESSAGES  = "sentmessagecount";
    private static final String COLUMN_RECEIVED_MSGS  = "receivedmessagecount";


    private static final String TABLE_PHONES        = "phones";
    private static final String COLUMN_PHONE_NUMBER = "number";
    private static final String COLUMN_PHONE_TYPE   = "type";
    private static final String COLUMN_PHONE_CONTACT_ID = "contactid";
    private static final String COLUMN_PHONE_IS_DEFAULT = "phoneisfromdefault";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_CONTACTS + "( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CONTACTNAME + " TEXT, " +
                COLUMN_FROM_DEFAULT + " BOOLEAN, " +
                COLUMN_MAIL + " TEXT, " +
                COLUMN_MISSCALL_COUNT + " INTEGER DEFAULT 0, " +
                COLUMN_INCALL_TIME + " INTEGER DEFAULT 0, " +
                COLUMN_INCALL_COUNT + " INTEGER DEFAULT 0, " +
                COLUMN_OUTCALL_TIME + " INTEGER DEFAULT 0, " +
                COLUMN_OUTCALL_COUNT + " INTEGER DEFAULT 0, " +
                COLUMN_SENT_MESSAGES + " INTEGER DEFAULT 0, " +
                COLUMN_RECEIVED_MSGS + " INTEGER DEFAULT 0 " +
                ");";
        db.execSQL(query);
        query = "CREATE TABLE " + TABLE_PHONES + "( " +
                COLUMN_PHONE_NUMBER + " TEXT, " +
                COLUMN_PHONE_TYPE + " TEXT, " +
                COLUMN_PHONE_CONTACT_ID + " INTEGER, " +
                COLUMN_PHONE_IS_DEFAULT + " BOOLEAN" +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONES);
        onCreate(db);
    }

    // Add a new contact to the contacts table
    public void addContact(Contact contact) {
        ContentValues values = new ContentValues();

        String name = contact.getName();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        values.put(COLUMN_CONTACTNAME, name);
        values.put(COLUMN_FROM_DEFAULT, contact.isFromDefaultContacts());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_CONTACTS, null, values);
        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE 1";
        Cursor c = db.rawQuery(query, null);
        c.moveToLast();
        contact.setId(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_ID))));
        db.close();
    }
    
    public void updateContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        // Delete previous phone rows which belongs to this contact
        db.execSQL("DELETE FROM " + TABLE_PHONES + " WHERE " + COLUMN_PHONE_CONTACT_ID + "=" + contact.getId() + ";");
        //db.execSQL("DELETE FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_ID + "=\"" + contact.getId() + "\";");
        ContentValues values = new ContentValues();
        // Add row to 'contacts' table
        values.put(COLUMN_CONTACTNAME, contact.getName());
        values.put(COLUMN_FROM_DEFAULT, contact.isFromDefaultContacts());
        if (contact.getMail() != null)
            values.put(COLUMN_MAIL, contact.getMail());
        if (contact.getMissingCallCount() != 0)
            values.put(COLUMN_MISSCALL_COUNT, contact.getMissingCallCount());
        if (contact.getIncomingCallTime() != 0)
            values.put(COLUMN_INCALL_TIME, contact.getIncomingCallTime());
        if (contact.getIncomingCallCount() != 0)
            values.put(COLUMN_INCALL_COUNT, contact.getIncomingCallCount());
        if (contact.getOutgoingCallTime() != 0)
            values.put(COLUMN_OUTCALL_TIME, contact.getOutgoingCallTime());
        if (contact.getOutgoingCallCount() != 0)
            values.put(COLUMN_OUTCALL_COUNT, contact.getOutgoingCallCount());
        if (contact.getSendMessageCount() != 0)
            values.put(COLUMN_SENT_MESSAGES, contact.getSendMessageCount());
        if (contact.getReceivedMessageCount() != 0)
            values.put(COLUMN_RECEIVED_MSGS, contact.getReceivedMessageCount());
        db.update(TABLE_CONTACTS, values, "_id="+contact.getId(), null);
        // Get the id from table because it is auto incremented
        String query = "SELECT *  FROM " + TABLE_CONTACTS + " WHERE 1";

        // Add rows to 'phones' table
        for (int i = 0; i < contact.getNumbers().size(); i++) {
            values = new ContentValues();
            values.put(COLUMN_PHONE_CONTACT_ID, contact.getId());
            values.put(COLUMN_PHONE_NUMBER, contact.getNumbers().get(i).getNumber());
            values.put(COLUMN_PHONE_TYPE, contact.getNumbers().get(i).getType());
            if (contact.isFromDefaultContacts()) {
                values.put(COLUMN_PHONE_IS_DEFAULT, true);
            }
            db.insert(TABLE_PHONES, null, values);
        }
    }
    
    
    // Update phone numbers for given contact
    public void updatePhoneNumbers(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        // First delete all previous phone numbers
        db.execSQL("DELETE FROM " + TABLE_PHONES + " WHERE " + COLUMN_PHONE_CONTACT_ID + "=" + contact.getId() + ";");

        // Second update them
        if (contact.getNumbers() != null ) {
            for (PhoneNumber pn : contact.getNumbers()) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_PHONE_CONTACT_ID, contact.getId());
                values.put(COLUMN_PHONE_NUMBER, pn.getNumber());
                values.put(COLUMN_PHONE_TYPE, pn.getType());
                if (contact.isFromDefaultContacts()) {
                    values.put(COLUMN_PHONE_IS_DEFAULT, true);
                }
                db.insert(TABLE_PHONES, null, values);
            }

        }

        if (db.isOpen())
            db.close();
    }
    
    

    // Delete a contact from contacts table
    public void deleteContact(Contact contact) {
        String contactName = contact.getName();
        int contactId = contact.getId();
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_ID + "=\"" + contactId + "\";");
        db.execSQL("DELETE FROM " + TABLE_PHONES + " WHERE " + COLUMN_PHONE_CONTACT_ID + "=\"" + contactId + "\";");
        db.close();
    }

    // Get values from database, store them in an object and return the object for a given id
    public Contact getSingleContact(int id) {
        Contact contact = new Contact(id);
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_ID + "=\"" + id + "\";";
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {

            int cId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            contact.setId(cId);
            String cName = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACTNAME));
            contact.setName(cName);
            String cMail = cursor.getString(cursor.getColumnIndex(COLUMN_MAIL));
            contact.setMail(cMail);
            int cMissingCallCount = cursor.getInt(cursor.getColumnIndex(COLUMN_MISSCALL_COUNT));
            contact.setMissingCallCount(cMissingCallCount);
            int cIncomingCallCount = cursor.getInt(cursor.getColumnIndex(COLUMN_INCALL_COUNT));
            contact.setIncomingCallCount(cIncomingCallCount);
            int cIncomingCallTime = cursor.getInt(cursor.getColumnIndex(COLUMN_INCALL_TIME));
            contact.setIncomingCallTime(cIncomingCallTime);
            int cOutgoingCallCount = cursor.getInt(cursor.getColumnIndex(COLUMN_OUTCALL_COUNT));
            contact.setOutgoingCallCount(cOutgoingCallCount);
            int cOutgoingCallTime = cursor.getInt(cursor.getColumnIndex(COLUMN_OUTCALL_TIME));
            contact.setOutgoingCallTime(cOutgoingCallTime);
            int cSendMessageCount = cursor.getInt(cursor.getColumnIndex(COLUMN_SENT_MESSAGES));
            contact.setSendMessageCount(cSendMessageCount);
            int cReceivedMessageCount = cursor.getInt(cursor.getColumnIndex(COLUMN_RECEIVED_MSGS));
            contact.setReceivedMessageCount(cReceivedMessageCount);

            String isDefault = cursor.getString(cursor.getColumnIndex(COLUMN_FROM_DEFAULT));
            if (Objects.equals(isDefault, "1"))
                contact.setIsFromDefaultContacts(true);

            String pQuery = "SELECT * FROM " + TABLE_PHONES + " WHERE " + COLUMN_PHONE_CONTACT_ID + "=\"" + contact.getId() + "\";";
            Cursor pCursor = db.rawQuery(pQuery, null);
            pCursor.moveToFirst();

            while(!pCursor.isAfterLast()) {
                if (pCursor.getString(pCursor.getColumnIndex(COLUMN_PHONE_NUMBER)) != null) {
                    PhoneNumber pNumber = new PhoneNumber();
                    pNumber.setNumber(pCursor.getString(pCursor.getColumnIndex(COLUMN_PHONE_NUMBER)));
                    pNumber.setType(pCursor.getInt(pCursor.getColumnIndex(COLUMN_PHONE_TYPE)));
                    contact.addNumber(pNumber);
                }
                pCursor.moveToNext();
            }

            cursor.moveToNext();
        }

        db.close();

        return contact;
    }

    // Get database
    public ArrayList<Contact> databaseToArrayList() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACTS + " ORDER BY " + COLUMN_CONTACTNAME + " COLLATE UNICODE;";

        ArrayList<Contact> contacts = new ArrayList<Contact>();
        Contact contact = null;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_CONTACTNAME)) != null) {
                contact = new Contact();
                String name = c.getString(c.getColumnIndex(COLUMN_CONTACTNAME));
                contact.setName(name);
                int id = Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_ID)));
                contact.setId(id);
                int isFromDefault = c.getInt(c.getColumnIndex(COLUMN_FROM_DEFAULT));

                if (isFromDefault == 1)
                    contact.setIsFromDefaultContacts(true);
            }

            String pQuery = "SELECT * FROM " + TABLE_PHONES + " WHERE " + COLUMN_PHONE_CONTACT_ID + "=\"" + contact.getId() + "\";";
            Cursor pCursor = db.rawQuery(pQuery, null);
            pCursor.moveToFirst();

            while(!pCursor.isAfterLast()) {
                if (pCursor.getString(pCursor.getColumnIndex(COLUMN_PHONE_NUMBER)) != null) {
                    PhoneNumber pNumber = new PhoneNumber();
                    pNumber.setNumber(pCursor.getString(pCursor.getColumnIndex(COLUMN_PHONE_NUMBER)));
                    pNumber.setType(pCursor.getInt(pCursor.getColumnIndex(COLUMN_PHONE_TYPE)));
                    contact.addNumber(pNumber);
                }
                pCursor.moveToNext();
            }

            contacts.add(contact);
            c.moveToNext();
        }

        db.close();

        return contacts;
    }

    // Get printable database
    public String databaseToString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACTS + " ORDER BY " + COLUMN_CONTACTNAME + " collate UNICODE;";

        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        while(!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_CONTACTNAME)) != null) {
                dbString += c.getString(c.getColumnIndex(COLUMN_ID));
                dbString += " ";
                dbString += c.getString(c.getColumnIndex(COLUMN_CONTACTNAME));
                dbString += " ";
                dbString += c.getString(c.getColumnIndex(COLUMN_FROM_DEFAULT));
                dbString += "\n";
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }

    // Delete contacts which added from DEFAULT phonebook
    public void deleteDefaults() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PHONES + " WHERE " + COLUMN_PHONE_IS_DEFAULT + "=\"" + 1 + "\";");
        db.execSQL("DELETE FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_FROM_DEFAULT + "=\"" + 1 + "\";");
        db.close();
    }

    public void printAll() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PHONES + " WHERE 1;";
        Cursor c = db.rawQuery(query, null);
        String h = "";
        c.moveToFirst();
        while (!c.isAfterLast()) {
            h += c.getString(c.getColumnIndex(COLUMN_PHONE_CONTACT_ID)) + "\n";
            h += c.getString(c.getColumnIndex(COLUMN_PHONE_NUMBER)) + "\n";
            c.moveToNext();
        }
    }

    public void updateSmsReceived(String number) {
        number = number.replaceAll(" ", "");
        Log.e("MBD_MBD", number + "");
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " +
                COLUMN_ID + " IN " + "(SELECT " + COLUMN_PHONE_CONTACT_ID + " FROM " + TABLE_PHONES + " WHERE " +
                COLUMN_PHONE_NUMBER + " LIKE '%" + number + "%' GROUP BY " + COLUMN_PHONE_CONTACT_ID + ")";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            int receivedSmsCount;
            receivedSmsCount = c.getInt(c.getColumnIndex(COLUMN_RECEIVED_MSGS));
            Log.e("MBD_MBD", receivedSmsCount + "MBDMBD");
            receivedSmsCount++;
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_RECEIVED_MSGS, receivedSmsCount);
            String id = c.getString(c.getColumnIndex(COLUMN_ID));
            db.update(TABLE_CONTACTS, contentValues, "_id="+id, null);
            c.moveToNext();
        }

    }

    public ArrayList<Contact> searchName(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_CONTACTNAME + " LIKE '%" + name +  "%' " + " ORDER BY " + COLUMN_CONTACTNAME + " collate UNICODE;";
        ArrayList<Contact> results = new ArrayList<Contact>();
        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        while(!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_CONTACTNAME)) != null) {
                Contact contact = new Contact(c.getInt(c.getColumnIndex(COLUMN_ID)));
                contact.setName(c.getString(c.getColumnIndex(COLUMN_CONTACTNAME)));
                results.add(contact);
            }

            c.moveToNext();
        }
        db.close();
        return results;

    }
}
