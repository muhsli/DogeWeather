package se.miun.dt125g.mape1133.dogeweather;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

    }

    // Overridar metoden onBackPressed för att kunna använda mina egna custom
    // animations
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Overridar transitionen vid tryck på "bakåt / back" till mina egna
        // custom animationer
        overridePendingTransition(R.anim.customfadein, R.anim.customfadeout);
    }
}