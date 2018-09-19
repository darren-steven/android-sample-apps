package com.telstra.android.media;


import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ooyala.sample.R;
import com.telstra.android.media.capabilities.CapabilityUtils;
import com.telstra.android.streaming.lteb.streamingsdk.BuildConfig;
import com.telstra.android.streaming.lteb.streamingsdk.utils.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DDMMInfoActivity extends AppCompatActivity {

    @BindView(R.id.checkBoxHevc)
    CheckBox hevcCheckBox;

    @BindView(R.id.checkBoxLTEB)
    CheckBox lteCheckBox;

    @BindView(R.id.checkBoxTelstra)
    CheckBox telstraCheckBox;

    @BindView(R.id.textViewDetails)
    TextView detailsTextView;

    @BindView(R.id.constraintLayout)
    ConstraintLayout constraintLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capability_test);
        ButterKnife.bind(this);

        Logger.debug("Android Version %s (%s)", Build.DEVICE, Build.VERSION.CODENAME);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            hevcCheckBox.setChecked(CapabilityUtils.hasHevcMainProfileLevel31());
        }

        lteCheckBox.setChecked(CapabilityUtils.hasBroadcastCapability(this));

        telstraCheckBox.setChecked(CapabilityUtils.hasTelstraSIM(this));

        String output = String.format("Version %s\n%s", BuildConfig.VERSION_NAME, CapabilityUtils.capabilitesToJson(CapabilityUtils.getCapabilities(this)));

        detailsTextView.setText(output);

        constraintLayout.requestLayout();

    }

    public static String getName() {
        return "DDMM Capabilities Dump";
    }
}

