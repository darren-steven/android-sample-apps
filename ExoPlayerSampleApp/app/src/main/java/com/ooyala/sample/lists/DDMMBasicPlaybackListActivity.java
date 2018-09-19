package com.ooyala.sample.lists;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ooyala.sample.R;
import com.ooyala.sample.players.OoyalaSkinPlayerActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

import java.util.LinkedHashMap;
import java.util.Map;

public class DDMMBasicPlaybackListActivity extends Activity implements OnItemClickListener {
    private static final int LOAD_REACT_BUNDLE_PERMISSION_REQ_CODE = 666;

    public final static String getName() {
        return "DDMM Playback";
    }

    private static Map<String, PlayerSelectionOption> selectionMap;
    ArrayAdapter<String> selectionAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getName());

        selectionMap = new LinkedHashMap<String, PlayerSelectionOption>();
        //Populate the embed map
        selectionMap.put("FFA Live H264 Channel 1", new PlayerSelectionOption("93anhoZjE6cgyuzR1q3BhnqM71ax-RVD", "xoNG0yOk4f4VR8pLKAKNukJ-gdEr", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
        selectionMap.put("FFA Live H264 Channel 2", new PlayerSelectionOption("hia3hoZjE6L6w4tgsYN6g2JcmC2yobE-", "xoNG0yOk4f4VR8pLKAKNukJ-gdEr", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
        selectionMap.put("FFA Live H264 Channel 3", new PlayerSelectionOption("ZlODR4ZjE6LkYNghRpIwwsDVfo4zuEO-", "xoNG0yOk4f4VR8pLKAKNukJ-gdEr", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
        selectionMap.put("FFA Live H264 Channel 4", new PlayerSelectionOption("50dGZ5ZjE6pb2DHmgBMnnDWUjtryEtSx", "xoNG0yOk4f4VR8pLKAKNukJ-gdEr", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
        selectionMap.put("FFA Live HEVC Channel 1", new PlayerSelectionOption("U3eWxyZjE6Aebq6DD3LDKt92zQ5Xh9BD", "xoNG0yOk4f4VR8pLKAKNukJ-gdEr", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
        selectionMap.put("FFA Live HEVC Channel 2", new PlayerSelectionOption("I5eWxyZjE6EwUR4ibV6nLpcVub0AG86u", "xoNG0yOk4f4VR8pLKAKNukJ-gdEr", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
        selectionMap.put("FFA Live HEVC Channel 3", new PlayerSelectionOption("p4dmx5ZjE6guyPIKJ34qqVk5UjsfBKS0", "xoNG0yOk4f4VR8pLKAKNukJ-gdEr", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
        selectionMap.put("FFA Live HEVC Channel 4", new PlayerSelectionOption("Ftd2x5ZjE6GXb0MVFhqT4_RpvYRfjprz", "xoNG0yOk4f4VR8pLKAKNukJ-gdEr", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));

        selectionMap.put("FFAmodel Live H264 Channel 1", new PlayerSelectionOption("puM21yZjE6yJaOroJBmVpJZ9U2FZxwTg", "ZyNG0yOm0wPDd56bSyHbTBLv4Nel", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
        selectionMap.put("FFAmodel Live H264 Channel 2", new PlayerSelectionOption("p4M21yZjE6sreaAhxxIQWsoFfUZP2myC", "ZyNG0yOm0wPDd56bSyHbTBLv4Nel", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
        selectionMap.put("FFAmodel Live HEVC Channel 1", new PlayerSelectionOption("YyNG1yZjE6JCOde9EEw_DMjd8lLnbM12", "ZyNG0yOm0wPDd56bSyHbTBLv4Nel", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
        selectionMap.put("FFAmodel Live HEVC Channel 2", new PlayerSelectionOption("w2NG1yZjE6oGVPluZ-7dtxOYqNRkLSAJ", "ZyNG0yOm0wPDd56bSyHbTBLv4Nel", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));

        selectionMap.put("2018 Grand Final: Fever v Lightning (Replay)", new PlayerSelectionOption("9sOGwyZzE62yvirCUvl-uHeOZou7LMBa", "duaGYxOuOYM6TOUkVF-lvVciGTP4", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));
        selectionMap.put("2017 Rd 5: Giants v Thunderbirds (Hls)", new PlayerSelectionOption("VtdWFlYTE6Hlu6LfhlY8t2VEjP62Rf9M", "duaGYxOuOYM6TOUkVF-lvVciGTP4", "http://www.ooyala.com", OoyalaSkinPlayerActivity.class));



        setContentView(R.layout.list_activity_layout);

        //Create the adapter for the ListView
        selectionAdapter = new ArrayAdapter<String>(this, R.layout.list_activity_list_item);
        for (String key : selectionMap.keySet()) {
            selectionAdapter.add(key);
        }
        selectionAdapter.notifyDataSetChanged();

        //Load the data into the ListView
        ListView selectionListView = (ListView) findViewById(R.id.mainActivityListView);
        selectionListView.setAdapter(selectionAdapter);
        selectionListView.setOnItemClickListener(this);

        //Force request android.permission.SYSTEM_ALERT_WINDOW
        // that is ignored in Manifest on Marshmallow devices.
        // Can be used if necessary to use native-skin source code
//    if(Build.VERSION_CODES.M == Build.VERSION.SDK_INT) {
//      showDevOptionsDialog();
//    }
    }

    private void showDevOptionsDialog() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, LOAD_REACT_BUNDLE_PERMISSION_REQ_CODE);
    }


    @Override
    public void onItemClick(AdapterView<?> l, View v, int pos, long id) {
        PlayerSelectionOption selection = selectionMap.get(selectionAdapter.getItem(pos));
        Class<? extends Activity> selectedClass = selection.getActivity();

        //Launch the correct activity with the embed code as an extra
        Intent intent = new Intent(this, selectedClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("embed_code", selection.getEmbedCode());
        intent.putExtra("selection_name", selectionAdapter.getItem(pos));
        intent.putExtra("pcode", selection.getPcode());
        intent.putExtra("domain", selection.getDomain());
        startActivity(intent);
        return;
    }
}
