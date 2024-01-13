package com.example.budgetbuddyapp;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetbuddyapp.Login.ForgotPassword;
import com.example.budgetbuddyapp.Login.Login;
import com.example.budgetbuddyapp.Profile.ChangePassword;
import com.example.budgetbuddyapp.Profile.Editprofile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView fullname, changePassword, txtViewEmail;
    private ConstraintLayout editprofile, changepassword, logout;
    String userID;
    public static final String SHARED_PREFS = "sharePrefs";
    private FirebaseAuth auth;
    private FirebaseFirestore fStore;
    private FirebaseUser user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, container, false);

        fullname = view.findViewById(R.id.txtViewUserName);
        changePassword = view.findViewById(R.id.txtView_changepassword);
        editprofile = view.findViewById(R.id.editprofile);
        changepassword = view.findViewById(R.id.changepassword);
        txtViewEmail = view.findViewById(R.id.txtViewEmail);
        logout = view.findViewById(R.id.logout);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();
        user = FirebaseAuth.getInstance().getCurrentUser();

        String Email = user.getEmail();
        txtViewEmail.setText(Email != null ? Email: "");

        DocumentReference documentReference = fStore.collection("users").document(userID);

        //Hiển thị tên, email
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    String fullNameText = value.getString("fullname");
                    fullname.setText(fullNameText != null ? fullNameText : "");
                }
            }
        });

        //Sửa thông tin
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Editprofile.class);
                startActivity(intent);
            }
        });

        //Đổi mật khẩu
        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ForgotPassword.class));
            }
        });

        //Đăng xuất
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignOutConfirmationDialog();
            }
        });


        return view;
    }
    private void showSignOutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Đăng xuất");
        builder.setMessage("Đăng xuất ra khỏi tài khoản hiện tại?");

        builder.setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", "");
                editor.apply();

                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
                Toast.makeText(getContext(), "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nếu người dùng không muốn đăng xuất, hoặc hủy bỏ hộp thoại
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}