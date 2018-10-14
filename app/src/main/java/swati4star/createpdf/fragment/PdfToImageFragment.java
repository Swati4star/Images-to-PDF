package swati4star.createpdf.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import swati4star.createpdf.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PdfToImageFragment extends Fragment {


    public PdfToImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdf_to_image, container, false);
    }

}
