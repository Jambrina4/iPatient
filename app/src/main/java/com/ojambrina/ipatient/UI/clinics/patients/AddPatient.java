package com.ojambrina.ipatient.UI.clinics.patients;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ojambrina.ipatient.BuildConfig;
import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.entities.Patient;
import com.ojambrina.ipatient.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.ojambrina.ipatient.utils.Constants.CLINICS;
import static com.ojambrina.ipatient.utils.Constants.CLINIC_NAME;
import static com.ojambrina.ipatient.utils.Constants.NO_CLINIC_ADDED;
import static com.ojambrina.ipatient.utils.Constants.PATIENTS;
import static com.ojambrina.ipatient.utils.RequestCodes.IMAGE_FROM_CAMERA;
import static com.ojambrina.ipatient.utils.RequestCodes.IMAGE_FROM_GALLERY;

public class AddPatient extends AppCompatActivity {

    //Butterknife
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edit_patient_name)
    EditText editPatientName;
    @BindView(R.id.edit_patient_surname)
    EditText editPatientSurname;
    @BindView(R.id.edit_patient_born_date)
    EditText editPatientBornDate;
    @BindView(R.id.edit_patient_phone)
    EditText editPatientPhone;
    @BindView(R.id.edit_patient_email)
    EditText editPatientEmail;
    @BindView(R.id.edit_patient_profession)
    EditText editPatientProfession;
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
    @BindView(R.id.button_add_patient)
    Button buttonAddPatient;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Intent intent;
    private String clinic_name;
    private Patient patient;
    private Context context;
    private AppCompatActivity contextForToolbar;
    private String name, surname, bornDate, phone, email, profession;
    private boolean isValidPatientName, isValidPatientSurname, isValidPatientBornDate, isValidPatientPhone, isValidPatientEmail, isValidPatientProfession;
    private List<String> regularMedicationList = new ArrayList<>();
    private List<String> medicConditionsList = new ArrayList<>();
    private List<String> regularExerciseList = new ArrayList<>();
    private List<String> surgicalOperationsList = new ArrayList<>();
    private List<String> medicExaminationList = new ArrayList<>();
    private String cameraPath;
    private File profilePath;
    private Uri imageUri;
    private Uri getImageUri;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        ButterKnife.bind(this);

        contextForToolbar = this;
        context = this;
        firebaseStorage = FirebaseStorage.getInstance();

        setToolbar();
        setFirebase();
        getData();
        listeners();
    }

    private void listeners() {
        layoutProfilePhoto.setOnClickListener(v -> showPictureDialog());

        layoutBackgroundDeletePhoto.setOnClickListener(v -> {
            profilePhoto.setImageDrawable(null);
            imageCameraProfile.setVisibility(View.VISIBLE);
            layoutBackgroundDeletePhoto.setVisibility(View.GONE);
        });

        editPatientBornDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year1, month1, dayOfMonth) -> {
                month1 = month1 + 1;
                String date = dayOfMonth + "-" + month1 + "-" + year1;
                editPatientBornDate.setText(date);
            }, year, month, day);
            datePickerDialog.show();
        });


        buttonAddPatient.setOnClickListener(v -> {
            name = editPatientName.getText().toString().trim();
            surname = editPatientSurname.getText().toString().trim();
            bornDate = editPatientBornDate.getText().toString().trim();
            phone = editPatientPhone.getText().toString().trim();
            email = editPatientEmail.getText().toString().trim();
            profession = editPatientProfession.getText().toString().trim();

            validateProfession(editPatientProfession);
            validateEmail(editPatientEmail);
            validatePhone(editPatientPhone);
            validateBornDate(editPatientBornDate);
            validateSurname(editPatientSurname);
            validateName(editPatientName);

            if (isValidPatientName && isValidPatientSurname && isValidPatientBornDate && isValidPatientPhone && isValidPatientEmail && isValidPatientProfession) {
                dialog = Utils.showProgressDialog(context, "Añadiendo paciente", R.style.AppCompatAlertDialogStyle);
                dialog.show();

                if (clinic_name.equals(NO_CLINIC_ADDED)) {
                    dialog.dismiss();
                    Toast.makeText(context, "Debes registrar una clínica para poder agregar un paciente", Toast.LENGTH_SHORT).show();
                } else {
                    if (imageUri != null) {
                        StorageReference sr = storageReference.child(System.currentTimeMillis() + "." + getExtension(imageUri));
                        sr.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                            Task<Uri> uri = sr.getDownloadUrl();
                            do {
                                Log.d("INFO", "SUBIENDO IMAGEN DE PACIENTE");
                            } while (!uri.isComplete());
                            getImageUri = uri.getResult();

                            firebaseFirestore.collection(CLINICS).document(clinic_name).collection(PATIENTS).document(name).get().addOnCompleteListener(task -> {
                                if (task.getResult().exists()) {
                                    dialog.dismiss();
                                    Toast.makeText(context, "Ya existe un paciente con ese nombre", Toast.LENGTH_SHORT).show();
                                } else {
                                    dialog.dismiss();
                                    addPatient();
                                    firebaseFirestore.collection(CLINICS).document(clinic_name).collection(PATIENTS).document(name).set(patient);
                                    finish();
                                }
                            });
                        }).addOnFailureListener(e -> {
                            dialog.dismiss();
                            Toast.makeText(context, "Ha ocurrido un problema al agregar el paciente, inténtalo de nuevo", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", e.getMessage());
                        });
                    } else {
                        firebaseFirestore.collection(CLINICS).document(clinic_name).collection(PATIENTS).document(name).get().addOnCompleteListener(task -> {
                            if (task.getResult().exists()) {
                                dialog.dismiss();
                                Toast.makeText(context, "Ya existe un paciente con ese nombre", Toast.LENGTH_SHORT).show();
                            } else {
                                addPatient();
                                firebaseFirestore.collection(CLINICS).document(clinic_name).collection(PATIENTS).document(name).set(patient);
                                dialog.dismiss();
                                finish();
                            }
                        }).addOnFailureListener(e -> {
                            dialog.dismiss();
                            Toast.makeText(context, "Ha ocurrido un problema al agregar el paciente, inténtalo de nuevo", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", e.getMessage());
                        });
                    }
                }
            }
        });

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(CLINICS);
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void getData() {
        intent = getIntent();
        clinic_name = intent.getStringExtra(CLINIC_NAME);
    }

    public void addPatient() {
        patient = new Patient();

        patient.setName(name);
        patient.setSurname(surname);
        patient.setBornDate(bornDate);
        patient.setPhone(phone);
        patient.setEmail(email);
        patient.setProfession(profession);
        patient.setRegularMedication(regularMedicationList);
        patient.setMedicConditions(medicConditionsList);
        patient.setRegularExercise(regularExerciseList);
        patient.setSurgicalOperations(surgicalOperationsList);
        patient.setMedicExamination(medicExaminationList);
        if (imageUri != null) {
            patient.setProfileImage(String.valueOf(getImageUri));
        }
    }

    private void setToolbar() {
        Utils.configToolbar(contextForToolbar, toolbar);
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Selecciona una opción");
        String[] pictureDialogItems = {
                "Seleccionar foto desde galería",
                "Capturar foto desde camara"};
        pictureDialog.setItems(pictureDialogItems, (dialog, which) -> {
            switch (which) {
                case 0:
                    choosePhotoFromGallery();
                    break;
                case 1:
                    takePhotoFromCamera();
                    break;
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
        if (resultCode == RESULT_OK) {
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

    //VALIDATIONS
    private void validateName(EditText editPatientName) {
        name = editPatientName.getText().toString().trim();
        if (editPatientName.length() > 0) {
            isValidPatientName = true;
        } else {
            editPatientName.requestFocus();
            editPatientName.setError("El campo nombre no puede estar vacío");
            isValidPatientName = false;
        }
    }

    private void validateSurname(EditText editPatientSurname) {
        surname = editPatientSurname.getText().toString().trim();
        if (surname.length() > 0) {
            isValidPatientSurname = true;
        } else {
            editPatientSurname.requestFocus();
            editPatientSurname.setError("El campo apellidos no puede estar vacío");
            isValidPatientSurname = false;
        }
    }

    private void validateBornDate(EditText editPatientBornDate) {
        bornDate = editPatientBornDate.getText().toString().trim();
        if (bornDate.length() > 0) {
            isValidPatientBornDate = true;
        } else {
            editPatientBornDate.requestFocus();
            editPatientBornDate.setError("El campo fecha de nacimiento no puede estar vacío");
            isValidPatientBornDate = false;
        }
    }

    private void validatePhone(EditText editPatientPhone) {
        phone = editPatientPhone.getText().toString().trim();
        if (phone.length() > 0) {
            if (Patterns.PHONE.matcher(phone).matches()) {
                isValidPatientPhone = true;
            } else {
                editPatientPhone.requestFocus();
                editPatientPhone.setError("Número de teléfono no válido");
                isValidPatientPhone = false;
            }
        } else {
            editPatientPhone.requestFocus();
            editPatientPhone.setError("El campo teléfono no puede estar vacío");
            isValidPatientPhone = false;
        }
    }

    private void validateEmail(EditText editPatientEmail) {
        email = editPatientEmail.getText().toString().trim();
        if (email.length() > 0) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                isValidPatientEmail = true;
            } else {
                editPatientEmail.requestFocus();
                editPatientEmail.setError("Email no válido");
                isValidPatientEmail = false;
            }
        } else {
            editPatientEmail.requestFocus();
            editPatientEmail.setError("El campo email no puede estar vacío");
            isValidPatientEmail = false;
        }
    }

    private void validateProfession(EditText editPatientProfession) {
        profession = editPatientProfession.getText().toString().trim();
        if (profession.length() > 0) {
            isValidPatientProfession = true;
        } else {
            editPatientProfession.requestFocus();
            editPatientProfession.setError("El campo profesión no puede estar vacío");
            isValidPatientProfession = false;
        }
    }
}
