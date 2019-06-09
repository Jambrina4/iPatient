package com.ojambrina.ipatient.UI.home;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfigurationActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Context context;
    private AppCompatActivity contextForToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        ButterKnife.bind(this);

        context = this;
        contextForToolbar = this;

        setToolbar();
        listeners();
    }

    private void setToolbar() {
        Utils.configToolbar(contextForToolbar, toolbar);
        toolbar.setTitle(getResources().getString(R.string.text_configuration));
    }

    private void listeners() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
}
