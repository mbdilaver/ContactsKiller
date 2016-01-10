package lesson.mbd.com.contactskiller;

/**
 * Created by M on 24.12.2015.
 */
public class PhoneNumber {
    int type;
    String number;

    public PhoneNumber() {
    }

    public PhoneNumber(String number, int type) {
        this.number = number;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
