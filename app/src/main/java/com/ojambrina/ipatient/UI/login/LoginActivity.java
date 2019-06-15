package com.ojambrina.ipatient.UI.login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.UI.home.HomeActivity;
import com.ojambrina.ipatient.entities.Professional;
import com.ojambrina.ipatient.utils.AppPreferences;
import com.ojambrina.ipatient.utils.Utils;

import java.util.List;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ojambrina.ipatient.utils.Constants.PROFESSIONAL;
import static com.ojambrina.ipatient.utils.Constants.PROFESSIONALS;
import static com.ojambrina.ipatient.utils.Constants.USER_EMAIL;

public class LoginActivity extends AppCompatActivity {

    //Butterknife
    @BindView(R.id.edit_email)
    EditText editEmail;
    @BindView(R.id.login_password)
    EditText editPassword;
    @BindView(R.id.forgot_password)
    TextView forgotPassword;
    @BindView(R.id.checkbox_remember)
    CheckBox checkBoxRemember;
    @BindView(R.id.layout_login)
    LinearLayout layoutLogin;
    @BindView(R.id.login_no_account)
    TextView loginNoAccount;

    //Declarations
    private Context context;
    private AppPreferences appPreferences;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String email, password;
    private Dialog dialog;
    private Dialog progressDialog;
    private EditText editRecoverEmail;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private Professional professional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        context = this;
        appPreferences = new AppPreferences();
        firebaseAuth = FirebaseAuth.getInstance();

        if (appPreferences.getEmail() != null) {
            editEmail.setText(appPreferences.getEmail());
        }

        setFirebase();
        listeners();
    }

    private void setFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(PROFESSIONALS);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void listeners() {
        layoutLogin.setOnClickListener(v -> {
            if (validateEmail(editEmail) && validatePassword(editPassword)) {
                if (checkBoxRemember.isChecked()) {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    appPreferences.setCheckboxLogin(true);
                    login();
                } else {
                    appPreferences.setCheckboxLogin(false);
                    login();
                }
            }
        });

        forgotPassword.setOnClickListener(v -> {
            dialog = Utils.openDialog(context, R.layout.dialog_forgot_password);
            ImageView cancel = dialog.findViewById(R.id.close);
            TextView send = dialog.findViewById(R.id.send);
            cancel.setOnClickListener(v1 -> dialog.dismiss());

            send.setOnClickListener(v12 -> {
                editRecoverEmail = dialog.findViewById(R.id.edit_recover_email);
                email = editRecoverEmail.getText().toString().trim();

                if (email.length() > 0) {
                    progressDialog = Utils.showProgressDialog(context, "Enviando email de recuperación", R.style.AppCompatAlertDialogStyle);
                    progressDialog.show();
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("RECOVER PASSWORD", "Email sent.");
                            Toast.makeText(context, "Email de recuperación enviado", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            dialog.dismiss();
                        } else {
                            editRecoverEmail.setError("Email incorrecto");
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    editRecoverEmail.setError("El campo email no puede estar vacío");
                }
            });

            dialog.show();
        });

        loginNoAccount.setOnClickListener(v -> {
            Intent intent = new Intent(context, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void login() {
        progressDialog = Utils.showProgressDialog(context, "Iniciando sesión", R.style.AppCompatAlertDialogStyle);
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
            if (task.isSuccessful()) {

                appPreferences.setEmail(email);

                progressDialog.dismiss();
                Log.d("FIREBASE LOGIN", "signInWithEmail:success");

                Intent intent = new Intent(context, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                progressDialog.dismiss();
                Log.w("FIREBASE LOGIN", "signInWithEmail:failure", task.getException());
                Toast.makeText(context, "Datos de inicio de sesión incorrectos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //VALIDATIONS
    private boolean validateEmail(EditText editEmail) {
        email = editEmail.getText().toString().trim();
        if (email.length() > 0) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return true;
            } else {
                editEmail.setError("Email no válido");
                return false;
            }
        } else {
            editEmail.setError("El campo email no puede estar vacío");
            return false;
        }
    }

    private boolean validatePassword(EditText editPassword) {
        password = editPassword.getText().toString().trim();
        if (password.length() >= 8) {
            return true;
        } else {
            editPassword.setError("Al menos 8 caracteres");
            return false;
        }
    }
}