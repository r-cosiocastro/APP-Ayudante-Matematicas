package com.rafaelcosio.mathhelper.views.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rafaelcosio.mathhelper.R;
import com.rafaelcosio.mathhelper.databinding.FragmentProfileBinding;
import com.rafaelcosio.mathhelper.utils.Utils;
import com.rafaelcosio.mathhelper.views.activities.LoginActivity;
import com.rafaelcosio.mathhelper.views.activities.MainActivity;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private static final String NAME_KEY = "NAME_KEY";
    private static final String LEVEL_KEY = "USER_LEVEL";
    private static final String POINTS_KEY = "USER_POINTS";
    private static final String TYPE_KEY = "USER_TYPE";
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private static final String EMAIL_KEY = "EMAIL_KEY";
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth mAuth;
    private final StorageReference storageRef = storage.getReference();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FragmentProfileBinding binding;
    private StorageReference profile;
    // TODO: Rename and change types of parameters
    private String userName;
    private String userMail;
    private int userLevel;
    private int userPoints;
    private int userType;

    private OnFragmentInteractionListener mListener;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences settings;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userLevel  Parameter 1.
     * @param userPoints Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String userName, String userMail, int userLevel, int userPoints, int userType) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(NAME_KEY, userName);
        args.putString(EMAIL_KEY, userMail);
        args.putInt(LEVEL_KEY, userLevel);
        args.putInt(POINTS_KEY, userPoints);
        args.putInt(TYPE_KEY, userType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString(NAME_KEY);
            userMail = getArguments().getString(EMAIL_KEY);
            userLevel = getArguments().getInt(LEVEL_KEY);
            userPoints = getArguments().getInt(POINTS_KEY);
            userType = getArguments().getInt(TYPE_KEY);
        }
    }

    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        StorageReference profilePhotosRefs = storageRef.child("profile_photos");
        profile = profilePhotosRefs.child(mAuth.getUid() + "/" + "pp");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        try {
            if (Objects.requireNonNull(mAuth.getCurrentUser()).getPhotoUrl() != null) {
                Glide.with(this /* context */)
                        .load(mAuth.getCurrentUser().getPhotoUrl()).dontTransform().fitCenter().circleCrop()
                        .into(binding.imgProfile);
            }
        } catch (Exception ex) {
            Log.d(TAG, "El usuario no tiene URL de foto de perfil");
        }
        int neededPoints = (userLevel + 1) * 400;
        binding.textViewActualExperience.setText(getResources().getString(R.string.actual_points, userPoints));
        binding.textViewNivel.setText(getResources().getString(R.string.user_level, userLevel));
        binding.textViewNeededExperience.setText(getResources().getString(R.string.needed_points, neededPoints));
        binding.progressBar.setMax(neededPoints);
        binding.progressBar.setProgress(userPoints);
        binding.textViewName.setText(userName);
        if (userType == 0) {
            binding.textViewEmail.setVisibility(View.GONE);
        } else {
            binding.textViewEmail.setVisibility(View.VISIBLE);
            binding.textViewEmail.setText(userMail);
        }
        binding.fab.setOnClickListener(v -> PickImageDialog.build(new PickSetup())
                .setOnPickResult(r -> {
                    //TODO: do what you have to...
                    showProgressDialog();
                    Log.d(TAG, "Uri: " + r.getUri().toString());
                    UploadTask uploadTask;

                    Log.d(TAG, "Uri: " + r.getUri().getLastPathSegment());
                    uploadTask = profile.putFile(r.getUri());

// Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads
                        hideProgressDialog();
                        Log.d(TAG, exception.toString());
                    }).addOnSuccessListener(taskSnapshot -> profilePhotosRefs.child(mAuth.getUid() + "/" + "pp").getDownloadUrl().addOnSuccessListener(uri -> {
                        // Got the download URL for 'users/me/profile.png'
                        Log.d(TAG, uri.toString());
                        updateUserProfile(uri);
                    }).addOnFailureListener(exception -> {
                        // Handle any errors
                        hideProgressDialog();
                    }));
                })
                .setOnPickCancel(() -> {
                    //TODO: do what you have to if user clicked cancel

                }).show(getFragmentManager()));

        updateLoginButton();

        return binding.getRoot();
    }

    private void updateUserProfile(Uri photoUri) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(photoUri)
                .build();

        Objects.requireNonNull(mAuth.getCurrentUser()).updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                        Glide.with(this /* context */)
                                .load(mAuth.getCurrentUser().getPhotoUrl()).dontTransform().fitCenter().circleCrop()
                                .into(binding.imgProfile);
                        hideProgressDialog();
                    } else {
                        hideProgressDialog();
                    }
                });
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

    private void updateLoginButton() {
        if (mAuth.getCurrentUser() != null) {
            binding.fab.setVisibility(View.VISIBLE);
            binding.buttonLogin.setText(getResources().getString(R.string.logout_text));
            binding.buttonLogin.setOnClickListener(v -> {
                mAuth.signOut();
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getContext()), gso);
                mGoogleSignInClient.signOut();

                settings = Objects.requireNonNull(getActivity()).getSharedPreferences("user_preferences", 0);
                settings.edit().putString("last_user_uid", "local").apply();

                getActivity().finish();
                Utils.openActivity(getActivity(), MainActivity.class);
            });
        } else {
            binding.fab.setVisibility(View.GONE);
            binding.buttonLogin.setText(getResources().getString(R.string.login));
            binding.buttonLogin.setOnClickListener(v -> Utils.openActivity(getContext(), LoginActivity.class));
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
