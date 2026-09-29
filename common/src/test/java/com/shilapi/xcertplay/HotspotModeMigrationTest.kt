package com.shilapi.xcertplay

import android.content.Context
import com.shilapi.xcertplay.orchestration.WirelessHotspotMode
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], manifest = Config.NONE)
class HotspotModeMigrationTest {
    private val context get() = RuntimeEnvironment.getApplication()
    private val prefs get() = context.getSharedPreferences("xcertplay_airplay", Context.MODE_PRIVATE)

    @Test fun localOnlyHotspotSelectionIsPreserved() {
        prefs.edit().putString("wireless_hotspot_mode", "LOCAL_ONLY_HOTSPOT").apply()

        assertEquals(
            WirelessHotspotMode.LOCAL_ONLY_HOTSPOT,
            AirPlayPersistence.loadWirelessHotspotMode(context),
        )
        assertEquals(
            "LOCAL_ONLY_HOTSPOT",
            prefs.getString("wireless_hotspot_mode", null),
        )
    }

    @Test fun freshInstallUsesWifiP2pOnModernAndroid() {
        prefs.edit().clear().apply()

        assertEquals(
            WirelessHotspotMode.WIFI_P2P,
            AirPlayPersistence.loadWirelessHotspotMode(context),
        )
    }

    @Test fun existingWifiDirectSelectionIsPreserved() {
        AirPlayPersistence.saveWirelessHotspotMode(
            context,
            WirelessHotspotMode.WIFI_P2P,
        )

        assertEquals(
            WirelessHotspotMode.WIFI_P2P,
            AirPlayPersistence.loadWirelessHotspotMode(context),
        )
    }

    @Test
    @Config(sdk = [28])
    fun olderAndroidFallsBackToLocalOnlyHotspot() {
        prefs.edit().putString("wireless_hotspot_mode", "WIFI_P2P").apply()

        assertEquals(
            WirelessHotspotMode.LOCAL_ONLY_HOTSPOT,
            AirPlayPersistence.loadWirelessHotspotMode(context),
        )
    }

    @Test
    @Config(sdk = [27])
    fun android81FallsBackToLocalOnlyHotspot() {
        prefs.edit().clear().apply()

        assertEquals(
            WirelessHotspotMode.LOCAL_ONLY_HOTSPOT,
            AirPlayPersistence.loadWirelessHotspotMode(context),
        )
    }
}