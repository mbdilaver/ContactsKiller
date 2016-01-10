package lesson.mbd.com.contactskiller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by mbd on 6.01.2016.
 */
public class myDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Loading contacts from default phonebook...");

        return builder.create();
    }
}
