package com.ojambrina.ipatient.UI.login;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ojambrina.ipatient.BuildConfig;
import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.entities.Proffesional;
import com.ojambrina.ipatient.utils.AppPreferences;
import com.ojambrina.ipatient.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.ojambrina.ipatient.utils.Constants.PROFESSIONALS;
import static com.ojambrina.ipatient.utils.RequestCodes.IMAGE_FROM_CAMERA;
import static com.ojambrina.ipatient.utils.RequestCodes.IMAGE_FROM_GALLERY;

public class RegisterActivity extends AppCompatActivity {

    //ButterKnife
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.edit_surname)
    EditText editSurname;
    @BindView(R.id.edit_identity_number)
    EditText editIdentityNumber;
    @BindView(R.id.edit_email)
    EditText editEmail;
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.edit_password)
    EditText editPassword;
    @BindView(R.id.edit_password_repeat)
    EditText editPasswordRepeat;
    @BindView(R.id.layout_register)
    LinearLayout layoutRegister;
    @BindView(R.id.have_account)
    TextView haveAccount;
    @BindView(R.id.profile_photo)
    CircleImageView profilePhoto;
    @BindView(R.id.image_camera_profile)
    ImageView imageCameraProfile;
    @BindView(R.id.delete_background_photo)
    ImageView deleteBackgroundPhoto;
    @BindView(R.id.layout_background_delete_photo)
    LinearLayout layoutBackgroundDeletePhoto;
    @BindView(R.id.layout_profile_photo)
    RelativeLayout layoutProfilePhoto;

    //Declarations
    Context context;
    AppPreferences appPreferences;
    Proffesional proffesional;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String username, name, surname, identityNumber, phone, email, password;
    boolean isValidName, isValidSurname, isValidIdentityNumber, isValidPhone, isValidEmail, isValidPassword, isValidPasswordRepeat;
    Dialog dialog;
    private String cameraPath;
    private File profilePath;
    private Uri imageUri;
    private Uri getImageUri;

    //TODO: Poner EditTextInputLayout en color blanco
    //TODO METODO AÑADIR FOTO A PROFESIONAL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        context = this;
        appPreferences = new AppPreferences();

        setToolbar();
        setFirebase();
        listeners();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("profesionales");
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void listeners() {
        layoutRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProffesional();

                validatePasswordRepeat(editPasswordRepeat);
                validatePassword(editPassword);
                validatePhone(editPhone);
                validateEmail(editEmail);
                validateIdentityNumber(editIdentityNumber);
                validateSurname(editSurname);
                validateName(editName);

                if (isValidName && isValidSurname && isValidIdentityNumber && isValidEmail && isValidPhone && isValidPassword && isValidPasswordRepeat) {
                    dialog = Utils.showProgressDialog(context, "Creando usuario");
                    dialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                appPreferences.setEmail(email);

                                Log.d("REGISTRO", "createUserWithEmail:onComplete:" + task.isSuccessful());
                                firebaseFirestore.collection(PROFESSIONALS).document(username).set(proffesional);
                                FirebaseAuth.getInstance().signOut();

                                Intent intent = new Intent(context, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(context, "Refistro fallido." + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void getProffesional() {
        proffesional = new Proffesional();

        name = editName.getText().toString().trim();
        surname = editSurname.getText().toString().trim();
        identityNumber = editIdentityNumber.getText().toString().trim();
        phone = editPhone.getText().toString().trim();
        email = editEmail.getText().toString().trim();

        username = name + " " + surname;

        proffesional.setName(name);
        proffesional.setSurname(surname);
        proffesional.setIdentityNumber(identityNumber);
        proffesional.setPhone(phone);
        proffesional.setEmail(email);
        //aproffesional.setImage(image);
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Selecciona una opción");
        String[] pictureDialogItems = {
                "Seleccionar foto desde galería",
                "Capturar foto desde camara"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, IMAGE_FROM_GALLERY);
    }

    private void takePhotoFromCamera() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, IMAGE_FROM_CAMERA);
        } else {
            cameraPath = openCamera(context, IMAGE_FROM_CAMERA);
        }
    }

    public String openCamera(Context context, int backgroundOrProfile) {
        String pictureImagePath;
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        pictureImagePath = storageDir.getAbsolutePath() + "/" + imageFileName;

        File file = new File(pictureImagePath);
        Uri outpuUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outpuUri);
        startActivityForResult(cameraIntent, backgroundOrProfile);

        return pictureImagePath;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case IMAGE_FROM_GALLERY:
                    if (data != null) {
                        imageUri = data.getData();
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Glide.with(context)
                                .asBitmap()
                                .load(bitmap)
                                .into(profilePhoto);
                        imageCameraProfile.setVisibility(View.GONE);
                        layoutBackgroundDeletePhoto.setVisibility(View.VISIBLE);
                    }
                    break;
                case IMAGE_FROM_CAMERA:
                    profilePath = new File(cameraPath);
                    imageUri = Uri.fromFile(profilePath);
                    Glide.with(context)
                            .load(profilePath)
                            .into(profilePhoto);
                    imageCameraProfile.setVisibility(View.GONE);
                    layoutBackgroundDeletePhoto.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_LONG).show();
            cameraPath = openCamera(context, IMAGE_FROM_CAMERA);
        } else {
            Toast.makeText(this, "Permisos denegados", Toast.LENGTH_LONG).show();
            Utils.permissionDialog(context);
        }
    }

    private String getExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //VALIDATIONS
    private void validateName(EditText editName) {
        name = editName.getText().toString().trim();
        if (editName.length() > 0) {
            isValidName = true;
        } else {
            editName.requestFocus();
            editName.setError("El campo nombre no puede estar vacío");
            isValidName = false;
        }
    }

    private void validateSurname(EditText editSurname) {
        surname = editSurname.getText().toString().trim();
        if (surname.length() > 0) {
            isValidSurname = true;
        } else {
            editSurname.requestFocus();
            editSurname.setError("El campo apellidos no puede estar vacío");
            isValidSurname = false;
        }
    }

    private void validateIdentityNumber(EditText editIdentityNumber) {
        identityNumber = editIdentityNumber.getText().toString().trim();
        Pattern pattern = Pattern.compile("(\\d{1,8})([TRWAGMYFPDXBNJZSQVHLCKEtrwagmyfpdxbnjzsqvhlcke])");
        Matcher matcher = pattern.matcher(identityNumber);

        if (matcher.matches()) {
            String letter = matcher.group(2);
            String patternLetters = "TRWAGMYFPDXBNJZSQVHLCKE";
            int index = Integer.parseInt(matcher.group(1));
            index = index % 23;
            String reference = patternLetters.substring(index, index + 1);
            if (reference.equalsIgnoreCase(letter)) {
                isValidIdentityNumber = true;
            } else {
                editIdentityNumber.requestFocus();
                editIdentityNumber.setError("Letra del DNI incorrecta");
                isValidIdentityNumber = false;
            }
        } else {
            editIdentityNumber.requestFocus();
            editIdentityNumber.setError("DNI no válido");
            isValidIdentityNumber = false;
        }
    }

    private void validatePhone(EditText editPhone) {
        phone = editPhone.getText().toString().trim();
        if (phone.length() > 0) {
            if (Patterns.PHONE.matcher(phone).matches()) {
                isValidPhone = true;
            } else {
                editPhone.requestFocus();
                editPhone.setError("Número de teléfono no válido");
                isValidPhone = false;
            }
        } else {
            editPhone.requestFocus();
            editPhone.setError("El campo teléfono no puede estar vacío");
            isValidPhone = false;
        }
    }

    private void validateEmail(EditText editEmail) {
        email = editEmail.getText().toString().trim();
        if (email.length() > 0) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                isValidEmail = true;
            } else {
                editEmail.requestFocus();
                editEmail.setError("Email no válido");
                isValidEmail = false;
            }
        } else {
            editEmail.requestFocus();
            editEmail.setError("El campo email no puede estar vacío");
            isValidEmail = false;
        }
    }

    private void validatePassword(EditText editPassword) {
        password = editPassword.getText().toString().trim();
        if (password.length() >= 8) {
            isValidPassword = true;
        } else {
            editPassword.requestFocus();
            editPassword.setError("Al menos 8 caracteres");
            isValidPassword = false;
        }
    }

    private void validatePasswordRepeat(EditText editPasswordRepeat) {
        password = editPassword.getText().toString().trim();
        String passwordRepeat = editPasswordRepeat.getText().toString().trim();

        if (passwordRepeat.length() >= 8) {
            if (passwordRepeat.equals(password)) {
                isValidPasswordRepeat = true;
            } else {
                editPasswordRepeat.requestFocus();
                editPasswordRepeat.setError("Las contraseñas deben coincidir");
                isValidPasswordRepeat = false;
            }
        } else {
            editPasswordRepeat.requestFocus();
            editPasswordRepeat.setError("Al menos 8 caracteres");
            isValidPasswordRepeat = false;
        }
    }
}
