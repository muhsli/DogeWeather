package se.miun.dt125g.mape1133.dogeweather;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class Main extends Activity {

    int symbolValue = 0;
    String weather = "much";
    String temperatureValue = "no conect";
    String windspeedValue = "internet pls";
    String cloudinessValue = "not online wow";
    String adress = "elgant doge";
    double lat;
    double lon;
    LinearLayout background;
    ImageView dogeImage;
    TextView addressTV;
    TextView windTV;
    TextView rainTV;
    TextView temperatureTV;
    TextView cloudyTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Location manager används för att hämta användarens position. Vi använder oss av NETWORK_PROVIDER
        // som kollar location baserat på nätverket.
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener;

        // Här sätter vi vårat typsnitt som vi har hämtat.
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/comicsans.ttf");

        // Initierar TextViews, Layouts, ImageViews och sätter typsnittet på TextViews.
        background = (LinearLayout) findViewById(R.id.background);
        dogeImage = (ImageView) findViewById(R.id.imageView);

        addressTV = (TextView) findViewById(R.id.addressText);
        addressTV.setTypeface(tf);

        windTV = (TextView) findViewById(R.id.windText);
        windTV.setTypeface(tf);

        rainTV = (TextView) findViewById(R.id.rainText);
        rainTV.setTypeface(tf);

        temperatureTV = (TextView) findViewById(R.id.temperatureText);
        temperatureTV.setTypeface(tf);

        cloudyTV = (TextView) findViewById(R.id.cloudyText);
        cloudyTV.setTypeface(tf);

        // Initierar en LocationListener som lyssnar efter förändringar i positionen
        locationListener = new LocationListener() {

            // Här lyssnar vi efter förändringar i positionen, t.ex. om användaren rör på sig
            // När positionen har ändrats uppdateras variablerna "lat" och "lon" med den nya gps-datan.
            // När de har uppdaterats så körs metoderna getMyLocationAddress() och UpdateUI().
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();

                RetrieveWeatherData retrieveWeatherDataThread = new RetrieveWeatherData(lat, lon);
                retrieveWeatherDataThread.start();
                try {
                    retrieveWeatherDataThread.join();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                getMyLocationAddress();
                UpdateUI();
            }

            // Ifall GPSen är avstängd på enheten uppdateras en textView
            public void onProviderDisabled(String provider) {
                addressTV.setText("no conect to netwroke!\nenabel netwroke pls.. not wow");

            }

            public void onProviderEnabled(String provider) {
                // Den här behöver vi inte ta i beaktning i denna applikation.
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // Den här behöver vi inte ta i beaktning i denna applikation.
            }
        };

        /*
        requestLocationUpdates gör en inställning till locationManager, som styr uppdaterings-intervallet av din position.
        Här kör vi på inställningen att den hämtar positionen var 15:e min (900000 millisekunder), ELLER om enheten har flyttats mer än 1 kilometer (1000 meter).
         */
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 900000, 1000, locationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflates/Skapar menyn; detta lägger till objekt till objektet till
        // titlebaren om den finns.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Skapar en custom animation activityoptions med mina custom animations
        ActivityOptions myAnim = ActivityOptions.makeCustomAnimation(this,
                R.anim.customfadein, R.anim.customfadeout);
        // Hanterar ett klick på / val av objekt i menyn
        switch (item.getItemId()) {
            // Om man klickar på "Settings"
            case R.id.menu_settings:
                // Skapar ett intent som startar Settings activityn, och startar detta
                // med animationen myAnim.
                startActivity(new Intent(Main.this, Settings.class),
                        myAnim.toBundle());
                return true;

            case R.id.menu_license:
                startActivity(new Intent(Main.this, License.class),
                        myAnim.toBundle());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    getMyLocationAddress använder Geocoder för reversed geocoding.
    Vad vi gör här är att vi tar koordinaterna, som har hämtats utifrån network.provider,
    och konverterar således dessa till adresser.
    Detta kan resultera i flera adresser, i synnerhet eftersom vi använder oss av network.provider med låg nogrannhet, och därför måste man spara dessa i en lista.
    Sedan tar vi helt enkelt första adressen i listan, och sparar ner dennes "locality" (stad) till Stringen address.
     */
    public void getMyLocationAddress() {

        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

        try {

            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

            if (addresses != null) {

                Address fetchedAddress = addresses.get(0);

                adress = fetchedAddress.getLocality().toLowerCase();

            }
        } catch (IOException e) {
            Log.d("Geocoder", "Failed to retrieve adress");
            e.printStackTrace();
        }
    }

    /*
    UpdateUI uppdaterar activityns bakgrund, doge och textview, som speglar det nuvarande väderförhållandet baserat på ett symbolvärde som hämtas från api.yr.no
    Sedan uppdaterar den resterande textviews med övrig väderdata som hämtas från api:t.
     */
    public void UpdateUI() {
        if (symbolValue == 0) {
            weather = "disconect not";
        } else {
            switch (symbolValue) {

                case 1:
                    weather = "sunny";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_sun));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_sun));
                    break;

                case 2:
                    weather = "little clouds";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_lightcloud));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_lightcloud));
                    break;

                case 3:
                    weather = "partly cloud";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_partlycloud));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_partlycloud));
                    break;

                case 4:
                    weather = "cloudy";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_verycloud));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_cloud));
                    break;

                case 5:
                    weather = "littel rain and sun";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_sun));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_lightrain));
                    break;

                case 6:
                    weather = "rain and THUNDER and sun";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_rain_thunder));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_thunder));
                    break;

                case 7:
                    weather = "sun and sleet";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_lightcloud));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_rain));
                    break;

                case 8:
                    weather = "snowy and sun";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_sun_winter));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_sun));
                    break;

                case 9:
                    weather = "little rain";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_rain));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_lightrain));
                    break;

                case 10:
                    weather = "rainy not niec";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_rain));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_rain));
                    break;

                case 11:
                    weather = "rain and THUNDER";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_rain_thunder));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_thunder));
                    break;

                case 12:
                    weather = "sleet not";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_sleet));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_snow));
                    break;

                case 13:
                    weather = "very snow";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_snow));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_snow));
                    break;

                case 14:
                    weather = "snow and THUNDER";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_thunder));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_snow));
                    break;

                case 15:
                    weather = "foggy such spooky";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_fog));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_fog));
                    break;

                case 16:
                    weather = "winter sun";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_snow));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_sun));
                    break;

                case 17:
                    weather = "little cloud";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_partlycloud_night));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_lightcloud_winter));
                    break;

                case 18:
                    weather = "winter sun little rain doe";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_snow));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_lightrain));
                    break;

                case 19:
                    weather = "winter snow and SUN";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_sun_winter));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_sun));
                    break;

                case 20:
                    weather = "sleet and sun and THUNDER vry";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_sleet));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_thunder));
                    break;

                case 21:
                    weather = "snowy sun vry THUNDER";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_sun_winter));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_thunder));
                    break;

                case 22:
                    weather = "litle rain och THUNDER such not";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_rain_thunder));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_thunder));
                    break;

                case 23:
                    weather = "sleet and thunder very not";
                    background.setBackground(getResources().getDrawable(R.drawable.bg_sleet));
                    dogeImage.setImageDrawable(getResources().getDrawable(R.drawable.doge_thunder));
                    break;
            }
        }

        addressTV.setText(adress);
        windTV.setText(windspeedValue);
        rainTV.setText("so " + weather + " wow");
        temperatureTV.setText(temperatureValue);
        cloudyTV.setText(cloudinessValue);
    }

    /*
    Klassen RetrieveWeatherData körs i en egen tråd, och startas med konstruktorn som kräver argumenten lat och lon.
    För att köra tråden krävs således att lat och lon har hämtats med locationmanagern.
    Det som händer här är att konstruktorn bygger Stringen coordinatesURL till en giltig URL för en förfrågan mot api.yr.no.
    När tråden exekverar konverteras Stringen coordinatesURL till en URL med namnet coordinatesURL.
    Denna används sedan för att hämta väderdata via XML, och vi hämtar sedan ur denna ut valda värden baserat på attribut, ifrån utvalda element baserat på TagName.
     */
    class RetrieveWeatherData extends Thread {

        String coordinatesURL;

        public RetrieveWeatherData(double lat, double lon) {
            coordinatesURL = "http://api.yr.no/weatherapi/locationforecast/1.9/?lat=" + lat + ";lon=" + lon;
        }

        @Override
        public void run() {

            try {
                URL weatherCastUrl = new URL(coordinatesURL);

                InputStream weatherCastInputStream = weatherCastUrl.openStream();

                DocumentBuilderFactory weatherCastInstance = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder weatherCastDocumentBuilder = weatherCastInstance.newDocumentBuilder();
                InputSource weatherCastIs = new InputSource(weatherCastInputStream);
                Document weatherCastXmlDocument = weatherCastDocumentBuilder.parse(weatherCastIs);

                NodeList locationsList = weatherCastXmlDocument.getElementsByTagName("location");
                Element location = (Element) locationsList.item(0);
                Element location2 = (Element) locationsList.item(1);

                NodeList symbolList = location2.getElementsByTagName("symbol");
                Element symbol = (Element) symbolList.item(0);
                symbolValue = Integer.parseInt(symbol.getAttribute("number"));

                NodeList temperatureList = location.getElementsByTagName("temperature");
                Element temperature = (Element) temperatureList.item(0);
                temperatureValue = "wow " + temperature.getAttribute("value") + "°C";

                NodeList windspeedList = location.getElementsByTagName("windSpeed");
                Element windspeed = (Element) windspeedList.item(0);
                windspeedValue = "very windy " + windspeed.getAttribute("mps") + " m/s";

                NodeList cloudinessList = location.getElementsByTagName("cloudiness");
                Element cloudiness = (Element) cloudinessList.item(0);
                cloudinessValue = "many cloud " + String.format("%.0f", Double.parseDouble(cloudiness.getAttribute("percent"))) + "%";


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }
    }
}