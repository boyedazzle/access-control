package com.babbangona.accesscontrol;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.babbangona.bgfr.Database.BGFRInfo;

import java.util.HashMap;

public class ResumeActivity extends AppCompatActivity {

    TextView staff_id;
    TextView staff_name;
    TextView staff_role;
    TextView tvReturnToLogin;

    boolean firstCapture = false;
    public static final int REQUEST_CODE = 122;
    private Bitmap imagePerson = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);

        staff_name = findViewById(R.id.tvUserName);
        staff_role = findViewById(R.id.tvRole);
        staff_id = findViewById(R.id.tvStaffID);
        tvReturnToLogin = findViewById(R.id.tvReturnToLogin);

        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = sessionManagement.getUserDetails();

        staff_name.setText(user.get(SessionManagement.KEY_STAFF_NAME));
        staff_id.setText(user.get(SessionManagement.KEY_STAFF_ID));
        staff_role.setText("");

        tvReturnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Main2Activity.class));
            }
        });

    }

    public void AuthFace(View view) {
        startActivity(new Intent(this, CaptureActivity.class));
    }

    public void AuthPassword(View view) {
        view();


    }

    public String getTemplate() {
        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = sessionManagement.getUserDetails();
        String template = user.get(SessionManagement.KEY_TEMPLATE).split("___")[0];
        return template;
    }


    public void view() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ResumeActivity.this);
        // ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.password_card, null);
        dialogBuilder.setView(dialogView);


        final TextView tvUsername = dialogView.findViewById(R.id.tvUserName);
        final EditText edtPassword = dialogView.findViewById(R.id.edtPassword);
        final Button btnSignin = dialogView.findViewById(R.id.btn_save);


        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = sessionManagement.getUserDetails();
        tvUsername.setText(user.get(SessionManagement.KEY_STAFF_NAME) + "\n" + user.get(SessionManagement.KEY_STAFF_ID));


        final AlertDialog alertDialog = dialogBuilder.create();
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!edtPassword.getText().toString().trim().matches("")) {

                    SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
                    HashMap<String, String> user = sessionManagement.getUserDetails();
                    String staffid = user.get(SessionManagement.KEY_STAFF_ID);

                    ControlDB controlDB = ControlDB.getInstance(getApplicationContext());
                    controlDB.open();
                    int controller = controlDB.login_controller(staffid, edtPassword.getText().toString());
                    if (controller > 0) {
                        Intent intent = new Intent(getApplicationContext(), ApplicationDetails.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ResumeActivity.this, "please enter password", Toast.LENGTH_SHORT).show();
                }
            }
        });


        alertDialog.show();

    }


}
