package com.rafaelcosio.mathhelper.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rafaelcosio.mathhelper.R;
import com.rafaelcosio.mathhelper.databinding.FragmentProblemsBinding;
import com.rafaelcosio.mathhelper.views.activities.SolveActivity;

import static com.rafaelcosio.mathhelper.utils.Constants.LEVEL;
import static com.rafaelcosio.mathhelper.utils.Constants.OPERATION;
import static com.rafaelcosio.mathhelper.utils.Constants.REQUEST_CODE;

import static com.rafaelcosio.mathhelper.utils.Constants.SUB;
import static com.rafaelcosio.mathhelper.utils.Constants.SUM;
import static com.rafaelcosio.mathhelper.utils.Constants.MULT;
import static com.rafaelcosio.mathhelper.utils.Constants.DIV;

public class CategoryChooserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String TAG = CategoryChooserFragment.class.getSimpleName();
    private int level = 0;
    private Spinner categorySpinner;

    private OnFragmentInteractionListener mListener;

    public CategoryChooserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryChooserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryChooserFragment newInstance(String param1, String param2) {
        CategoryChooserFragment fragment = new CategoryChooserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentProblemsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_problems, container, false);
        binding.btnSum.setOnClickListener(v -> openDialog(SUM));
        binding.btnRest.setOnClickListener(v -> openDialog(SUB));
        binding.btnMult.setOnClickListener(v -> openDialog(MULT));
        binding.btnDiv.setOnClickListener(v -> openDialog(DIV));

        return binding.getRoot();
    }

    private void openDialog(int operation) {


        MaterialDialog dialog =
                new MaterialDialog.Builder(getActivity())
                        .title("Selecciona una dificultad")
                        .customView(R.layout.dialog_difficulty, true)
                        .positiveText(android.R.string.ok)
                        .negativeText(android.R.string.cancel)
                        .onPositive(
                                (dialog1, which) -> {
                                    openActivityForResult(getActivity(), SolveActivity.class, operation, categorySpinner.getSelectedItemPosition());
                                })
                        .build();
        categorySpinner = dialog.getCustomView().findViewById(R.id.difficulty_category_spinner);

        dialog.show();
    }

    private void openActivityForResult(Activity activity, Class clazz, int operation, int level) {
        Intent mIntent = new Intent(activity, clazz);
        Bundle options = new Bundle();
        options.putInt(REQUEST_CODE, -1);
        mIntent.putExtra(LEVEL, level);
        mIntent.putExtra(OPERATION, operation);
        activity.startActivityForResult(mIntent, -1, options);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Toast.makeText(getActivity(), "Se terminaron los problemas", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Se terminaron los problemas");
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Los problemas no se terminaron", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Los problemas no se terminaron");
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }
}
