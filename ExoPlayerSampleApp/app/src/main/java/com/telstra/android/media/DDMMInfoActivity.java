package com.telstra.android.media;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ooyala.sample.R;
import com.telstra.android.media.capabilities.CapabilityUtils;
import com.telstra.android.streaming.lteb.streamingsdk.BuildConfig;
import com.telstra.android.streaming.lteb.streamingsdk.utils.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

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

        String output = String.format("App Version: %s\nSDK Version: %s\nUser: %s\n%s",
                com.ooyala.sample.BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_NAME,
                FirebaseInstanceId.getInstance().getId(),
                CapabilityUtils.capabilitesToJson(CapabilityUtils.getCapabilities(this)));

        detailsTextView.setText(output);
        Logger.debug("Capabilities: %s", output);

        uploadCapabilitiesInstanceID("DDMM", output, getApplicationContext());


        constraintLayout.requestLayout();
    }

    public static String getName() {
        return "DDMM Capabilities Dump";
    }

    public static void uploadLogcatWithInstanceID(String appName, Context context) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Date date = new Date();
        Logger.debug("Uploading logs for user %s", FirebaseInstanceId.getInstance().getId());
        String path = String.format(Locale.ENGLISH, "ooyala/%s/%s-%s-%s/logcat.txt",
                FirebaseInstanceId.getInstance().getId(),
                date.getYear(),
                date.getMonth(),
                date.getDate(),
                appName);
        StorageReference logs = storageRef.child(path);

        Process process = null;
        try {
            process = Runtime.getRuntime().exec("logcat -d -v threadtime *:*");

            UploadTask uploadTask = logs.putStream(process.getInputStream());
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Logger.debug("Logcat Upload Failed");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Logger.debug("Logcat Upload Success: %s", taskSnapshot.getUploadSessionUri().toString());

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Logger.debug("Upload Progress: %d/%s", taskSnapshot.getTotalByteCount(), taskSnapshot.getBytesTransferred());
                }
            });
        } catch (IOException e) {
            Logger.error(e, "Unable to get logcat");
        }

    }

    public static void uploadCapabilitiesInstanceID(String appName, String caps, Context context) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Date date = new Date();
        Logger.debug("Uploading logs for user %s", FirebaseInstanceId.getInstance().getId());
        String path = String.format(Locale.ENGLISH, "ooyala/%s/%s-%s-%s/capabiliites.txt",
                FirebaseInstanceId.getInstance().getId(),
                date.getYear(),
                date.getMonth(),
                date.getDate(),
                appName);
        StorageReference logs = storageRef.child(path);

        StorageMetadata.Builder builder = new StorageMetadata.Builder();
        builder.setContentType("text/plain").setContentEncoding("UTF-8");
        UploadTask uploadTask = logs.putBytes(caps.getBytes());

        logs.updateMetadata(builder.build());
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Logger.debug("Logcat Upload Failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Logger.debug("Logcat Upload Success: %s", taskSnapshot.getUploadSessionUri().toString());

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Logger.debug("Upload Progress: %d/%s", taskSnapshot.getTotalByteCount(), taskSnapshot.getBytesTransferred());
            }
        });


    }
}

