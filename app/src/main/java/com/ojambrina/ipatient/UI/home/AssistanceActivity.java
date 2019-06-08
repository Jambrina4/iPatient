package com.ojambrina.ipatient.UI.home;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    @BindView(R.id.edit_issue)
    EditText editIssue;
    @BindView(R.id.edit_description)
    EditText editDescription;
    @BindView(R.id.button_send)
    Button buttonSend;


    private Context context;
    private AppCompatActivity contextForToolbar;

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
        //TODO hacer dialogo para seleccionar categoría
        //TODO botón send enviar un email

        layoutCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Seleccionar cateogría", Toast.LENGTH_SHORT).show();
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Enviado", Toast.LENGTH_SHORT).show();
            }
        });

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setToolbar() {
        Utils.configToolbar(contextForToolbar, toolbar);
        toolbar.setTitle(getResources().getString(R.string.assistance));
    }
}
