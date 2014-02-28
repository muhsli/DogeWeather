/*
 * Den här klassen är till hundra procent identisk med klassen About,
 * bortsett från layout använd samt id't på textviewen.
 * Se klassen About för vidare referens.
 */

package se.miun.dt125g.mape1133.dogeweather;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class License extends Activity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        textView = (TextView) findViewById(R.id.licenseTextView);

        textView.setText(readTxt());

    }

    private String readTxt() {
        InputStream inputStream = getResources().openRawResource(R.raw.license);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            //Log.d("Big Boy Locator", "Read/Write error");
            e.printStackTrace();
        }

        return byteArrayOutputStream.toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.customfadein, R.anim.customfadeout);
    }
}