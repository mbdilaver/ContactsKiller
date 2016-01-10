package lesson.mbd.com.contactskiller;

import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * To Do:
 * - Make control mechanism for setter methods
 */
public class Contact {
    private int id;
    private String name;
    private ArrayList<PhoneNumber> numbers = new ArrayList<PhoneNumber>();
    private boolean isFromDefaultContacts;
    private String mail;
    private int missingCallCount;
    private int incomingCallTime;
    private int incomingCallCount;
    private int outgoingCallTime;
    private int outgoingCallCount;
    private int sendMessageCount;
    private int receivedMessageCount;

    public Contact() {

    }

    public Contact(int id) {
        this.id = id;
    }

    public Contact(String name) {
        this.name = name;
        isFromDefaultContacts = false;
    }

    public boolean isFromDefaultContacts() {
        return isFromDefaultContacts;
    }

    public void setIsFromDefaultContacts(boolean isFromDefaultContacts) {
        this.isFromDefaultContacts = isFromDefaultContacts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<PhoneNumber> getNumbers() {
        return numbers;
    }

    public void addNumber(PhoneNumber number) {
        numbers.add(number);
    }

    public void setNumbers(ArrayList<PhoneNumber> numbers) {
        this.numbers = numbers;
    }

    @Override
    public String toString() {
        String toPrint = "Contact{" + "\n" +
                         "id='" + id + '\'' + "\n" +
                         ", name='" + name + '\'' + "\n" +
                         ", isFromDefaultContacts=" + isFromDefaultContacts + "\n";
        toPrint += "numbers: \n";
        for (PhoneNumber pn:numbers) {
            toPrint += "> " + pn.getNumber();
        }
        return toPrint;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getMissingCallCount() {
        return missingCallCount;
    }

    public void setMissingCallCount(int missingCallCount) {
        this.missingCallCount = missingCallCount;
    }

    public int getIncomingCallTime() {
        return incomingCallTime;
    }

    public void setIncomingCallTime(int incomingCallTime) {
        this.incomingCallTime = incomingCallTime;
    }

    public int getIncomingCallCount() {
        return incomingCallCount;
    }

    public void setIncomingCallCount(int incomingCallCount) {
        this.incomingCallCount = incomingCallCount;
    }

    public int getOutgoingCallTime() {
        return outgoingCallTime;
    }

    public void setOutgoingCallTime(int outgoingCallTime) {
        this.outgoingCallTime = outgoingCallTime;
    }

    public int getOutgoingCallCount() {
        return outgoingCallCount;
    }

    public void setOutgoingCallCount(int outgoingCallCount) {
        this.outgoingCallCount = outgoingCallCount;
    }

    public int getSendMessageCount() {
        return sendMessageCount;
    }

    public void setSendMessageCount(int sendMessageCount) {
        this.sendMessageCount = sendMessageCount;
    }

    public int getReceivedMessageCount() {
        return receivedMessageCount;
    }

    public void setReceivedMessageCount(int receivedMessageCount) {
        this.receivedMessageCount = receivedMessageCount;
    }
}
