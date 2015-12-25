package lesson.mbd.com.contactskiller;

import android.provider.ContactsContract;

/**
 * To Do:
 * - Make control mechanism for setter methods
 */
public class Contact {
    private String id;
    private String name;
    private PhoneNumber homeNumber;
    private PhoneNumber mobileNumber;
    private PhoneNumber workNumber;

    final static String HOME_TYPE = String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
    final static String WORK_TYPE = String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
    final static String MOBILE_TYPE = String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

    public Contact(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PhoneNumber getHomeNumber() {
        return homeNumber;
    }

    public void setNumber(String number, String type) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber(number);

        phoneNumber.setType(type);
        if (type == HOME_TYPE)
            setHomeNumber(phoneNumber);
        else if (type == WORK_TYPE)
            setWorkNumber(phoneNumber);
        else if (type == MOBILE_TYPE)
            setMobileNumber(phoneNumber);


//        switch (type) {
//            case homeType:
//                setHomeNumber(phoneNumber);
//                break;
//            case "Work":
//                setWorkNumber(phoneNumber);
//                break;
//            case "Mobile":
//                setMobileNumber(phoneNumber);
//        }
    }

    public void setHomeNumber(PhoneNumber homeNumber) {
        this.homeNumber = homeNumber;
    }

    public PhoneNumber getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(PhoneNumber mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public PhoneNumber getWorkNumber() {
        return workNumber;
    }

    public void setWorkNumber(PhoneNumber workNumber) {
        this.workNumber = workNumber;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' + "\n" +
                ", name='" + name + '\'' + "\n" +
                ", homeNumber=" + homeNumber + "\n" +
                ", mobileNumber=" + mobileNumber + "\n" +
                ", workNumber=" + workNumber + "\n" +
                '}';
    }
}
