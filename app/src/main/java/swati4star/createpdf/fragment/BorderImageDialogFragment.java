package swati4star.createpdf.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;

import swati4star.createpdf.R;

public class BorderImageDialogFragment extends DialogFragment {

    private OnFragmentInteractionListener mListener;

    public BorderImageDialogFragment(){
    }

    static BorderImageDialogFragment newInstance() {
        return new BorderImageDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Context context = getActivity();

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(R.string.border_images_question);
        alertDialogBuilder.setPositiveButton(R.string.border_images_choice_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.setBorder(true);
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.border_images_choice_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.setBorder(false);
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }

    public interface OnFragmentInteractionListener {
        void setBorder(boolean b);
    }
}
