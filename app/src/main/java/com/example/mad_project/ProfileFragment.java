package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private CircleImageView profileImageView;
    private TextView profileName, profileLocation;
    private Button logoutButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initFirebase();
        initViews(view);
        setupListeners();

        loadUserProfile();

        return view;
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initViews(View view) {
        profileImageView = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profileLocation = view.findViewById(R.id.profile_location);
        logoutButton = view.findViewById(R.id.logout_button);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void loadUserProfile() {
        setInProgress(true);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnCompleteListener(task -> {
                        setInProgress(false);
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                User user = document.toObject(User.class);
                                if (user != null) {
                                    profileName.setText(user.getName());
                                    profileLocation.setText(user.getLocation());

                                    if (getContext() != null && user.getProfileImageUrl() != null) {
                                        Glide.with(getContext())
                                                .load(user.getProfileImageUrl())
                                                .into(profileImageView);
                                    }
                                }
                            } else {
                                Log.d(TAG, "No such document");
                                showSnackbar("Profile not found.");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            showSnackbar("Failed to load profile.");
                        }
                    });
        } else {
            setInProgress(false);
            navigateToLogin();
        }
    }

    private void logoutUser() {
        mAuth.signOut();
        navigateToLogin();
    }

    private void navigateToLogin() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), LoginScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void setInProgress(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
    }

    private void showSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }
}
