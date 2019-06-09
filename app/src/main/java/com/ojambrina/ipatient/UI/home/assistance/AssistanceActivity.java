package com.ojambrina.ipatient.UI.home.assistance;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssistanceActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.text_category)
    TextView textCategory;
    @BindView(R.id.layout_category)
    LinearLayout layoutCategory;
    @BindView(R.id.edit_topic)
    EditText editTopic;
    @BindView(R.id.edit_description)
    EditText editDescription;
    @BindView(R.id.button_send)
    Button buttonSend;

    private Context context;
    private AppCompatActivity contextForToolbar;
    private Dialog dialog;
    private String category, topic, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistance);
        ButterKnife.bind(this);

        context = this;
        contextForToolbar = this;

        setToolbar();
        listeners();
    }

    private void listeners() {
        layoutCategory.setOnClickListener(v -> {
            dialog = Utils.openDialog(context, R.layout.dialog_assist);
            dialog.show();

            TextView query = dialog.findViewById(R.id.text_query);
            TextView suggestion = dialog.findViewById(R.id.text_suggestion);
            TextView reportBug = dialog.findViewById(R.id.text_report_bug);

            query.setOnClickListener(v1 -> {
                dialog.dismiss();
                textCategory.setText(query.getText().toString());
            });

            suggestion.setOnClickListener(v12 -> {
                dialog.dismiss();
                textCategory.setText(suggestion.getText().toString());
            });

            reportBug.setOnClickListener(v13 -> {
                dialog.dismiss();
                textCategory.setText(reportBug.getText().toString());
            });
        });

        //TODO botón send me envía un mail con el contenido

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = textCategory.getText().toString();
                topic = editTopic.getText().toString().trim();
                message = editDescription.getText().toString().trim();

                String combinedTopic = "[" + category + "]" + " " + topic;

                if (category.equals(getResources().getString(R.string.category))) {
                    Toast.makeText(context, "Debes seleccionar una categoría", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jambrina4@gmail.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, combinedTopic);
                    intent.putExtra(Intent.EXTRA_TEXT, message);
                    try {
                        startActivity(Intent.createChooser(intent, "Enviar email..."));

                        //TODO cerrar activity cuando haya abierto la app de correo

                        finish();
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context, "No hay aplicaciones de email enviadas.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setToolbar() {
        Utils.configToolbar(contextForToolbar, toolbar);
        toolbar.setTitle(getResources().getString(R.string.assistance));
    }
}
