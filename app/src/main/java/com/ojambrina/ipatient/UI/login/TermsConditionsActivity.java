package com.ojambrina.ipatient.UI.login;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.TextView;

import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermsConditionsActivity extends AppCompatActivity {

    //Butterknife
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.general_conditions_text)
    TextView generalConditionsText;
    @BindView(R.id.intelectual_property_text)
    TextView intelectualPropertyText;
    @BindView(R.id.responsability_exclusion_text)
    TextView responsabilityExclusionText;
    @BindView(R.id.privacy_policy_text)
    TextView privacyPolicyText;

    private Context context;
    private AppCompatActivity contextForToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
        ButterKnife.bind(this);

        context = this;
        contextForToolbar = this;

        setToolbar();
        setTexts();
        listeners();
    }

    private void setToolbar() {
        Utils.configToolbar(contextForToolbar, toolbar);
    }

    private void setTexts() {
        generalConditionsText.setText(Html.fromHtml(getString(R.string.general_conditions_text)));
        intelectualPropertyText.setText(Html.fromHtml(getString(R.string.intelectual_property_text)));
        responsabilityExclusionText.setText(Html.fromHtml(getString(R.string.responsability_exclusion_text)));
        privacyPolicyText.setText(Html.fromHtml(getString(R.string.privacy_policy_text)));
    }

    private void listeners() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
}
