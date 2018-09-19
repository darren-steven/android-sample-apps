package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.ooyala.android.DefaultPlayerInfo;
import com.ooyala.android.Environment;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.item.Stream;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.sample.R;
import com.telstra.android.media.capabilities.CapabilityUtils;
import com.telstra.android.streaming.BroadcastSessionManager;
import com.telstra.android.streaming.StreamSessionManager;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

;

/**
 * This activity illustrates how you can play basic playback video using the Skin SDK
 * you can also play Ooyala and VAST advertisements programmatically
 * through the SDK
 * <p>
 * NOTE: AbstractHookActivity ust extend AppCompatActivity (for lifecycle)
 */
public class OoyalaSkinPlayerActivity extends AbstractHookActivity {
    private BroadcastSessionManager broadcastSessionManager;
    private StreamSessionManager streamSessionManager;

    @Override
    void completePlayerSetup(boolean asked) {
        if (asked) {
            // Get the SkinLayout from our layout xml
            OoyalaSkinLayout skinLayout = (OoyalaSkinLayout) findViewById(R.id.ooyalaSkin);
            PlayerDomain domain = new PlayerDomain(DOMAIN);

            String myCapabilities = CapabilityUtils.capabilitesToJson(CapabilityUtils.getCapabilities(this));
            String capabilities64 = null;
            try {
                byte[] capabilitiesData = myCapabilities.getBytes("UTF-8");
                capabilities64 = Base64.encodeToString(capabilitiesData, Base64.NO_WRAP);
            } catch (UnsupportedEncodingException e) {

            }

            // DDMM Setup
            // this map will contain the query params appended to the SAS request
            Map<String, String> myQueryParams = new HashMap<>();
            if (capabilities64 != null) {
                myQueryParams.put("ddmm_device_param", capabilities64);
            }


            MyPlayerInfo playerInfo = new MyPlayerInfo();
            playerInfo.setAdditionalParams(myQueryParams);

            // Create the OoyalaPlayer, with some built-in UI disabled
            Options options = new Options.Builder()
                    .setShowPromoImage(false)
                    .setShowNativeLearnMoreButton(false)
                    .setUseExoPlayer(true)
                    .setPlayerInfo(playerInfo)
                    .build();

            player = new OoyalaPlayer(pcode, domain, options);

            //Create the SkinOptions, and setup React
            JSONObject overrides = createSkinOverrides();
            SkinOptions skinOptions = new SkinOptions.Builder()
                    .setSkinOverrides(overrides)
                    .build();
            playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
            player.registerDataSourceFactory(streamSessionManager.getLtebDataSourceFactory());
            //Add observer to listen to fullscreen open and close events
            playerLayoutController.addObserver(this);
            player.addObserver(this);

            if (player.setEmbedCode(embedCode)) {

            } else {
                Log.e(TAG, "Asset Failure");
            }
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_skin_simple_layout);

        OoyalaPlayer.setEnvironment(Environment.EnvironmentType.STAGING);

        // LTE-B Setup. Relies on Android Lifecycle from AppCompatActivity to start/stop broadcast with activity/host
        // DDMM URL's will contain the LTE-B config
        broadcastSessionManager = new BroadcastSessionManager(getApplicationContext(), getLifecycle());
        // For DDMM, no need to startSession explicitly
        broadcastSessionManager.startSession(new String[]{"urn:oma:bcast:ext_bsc_embms:ServiceClass1:1.0"}, false);

        // TODO = use auto flag and get URL for DDMM startups
        streamSessionManager = new StreamSessionManager.Builder(new StreamSessionManager())
                .withCustomDataSource()
                .withLifeCycle(getLifecycle())
                .withLTEB(broadcastSessionManager, true) // TODO: perhaps autostart should come from remote config
                .build();

        streamSessionManager.setServiceId("urn:production:telstra:ProductionService3:SC3:UserService3");


        completePlayerSetup(asked);
    }

    /**
     * Create skin overrides to show up in the skin.
     * Default commented. Uncomment to show changes to the start screen.
     *
     * @return the overrides to apply to the skin.json in the assets folder
     */
    private JSONObject createSkinOverrides() {
        JSONObject overrides = new JSONObject();
//    JSONObject startScreenOverrides = new JSONObject();
//    JSONObject playIconStyleOverrides = new JSONObject();
//    try {
//      playIconStyleOverrides.put("color", "red");
//      startScreenOverrides.put("playButtonPosition", "bottomLeft");
//      startScreenOverrides.put("playIconStyle", playIconStyleOverrides);
//      overrides.put("startScreen", startScreenOverrides);
//    } catch (Exception e) {
//      Log.e(TAG, "Exception Thrown", e);
//    }
        return overrides;
    }

    class MyPlayerInfo extends DefaultPlayerInfo {
        Map<String, String> additionalParams;
        Set<String> supportedFormats = new HashSet<String>();

        public MyPlayerInfo() {
            super(null, null);
            supportedFormats.add(Stream.DELIVERY_TYPE_DASH);
            supportedFormats.add(Stream.DELIVERY_TYPE_HLS);
            supportedFormats.add(Stream.DELIVERY_TYPE_AKAMAI_HD2_VOD_HLS);
            supportedFormats.add(Stream.DELIVERY_TYPE_AKAMAI_HD2_HLS);
            supportedFormats.add(Stream.DELIVERY_TYPE_M3U8);
            supportedFormats.add(Stream.DELIVERY_TYPE_MP4);
        }

        public void setAdditionalParams(Map<String, String> additionalParams) {
            this.additionalParams = additionalParams;
        }

        @Override
        public Map<String, String> getAdditionalParams() {
            return additionalParams;
        }

        @Override
        public Set<String> getSupportedFormats() {
            return supportedFormats;
        }
    }
}
