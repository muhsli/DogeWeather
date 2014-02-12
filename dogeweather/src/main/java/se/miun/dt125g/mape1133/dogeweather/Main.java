package se.miun.dt125g.mape1133.dogeweather;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Main extends Activity {

    int symbolValue = 0;
    String weather = "much";
    String temperatureValue = "no conect";
    String windspeedValue = "internet pls";
    String cloudinessValue = "not online wow";
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

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        LocationListener locationListener;

        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/comicsans.ttf");

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

        try {
            lat = location.getLatitude();
            lon = location.getLongitude();
        } catch (Exception E) {
            E.printStackTrace();
            Log.d("Get Location",
                    "Could not retrieve a Last Known Location",
                    E.getCause());
            addressTV.setText("lookng for yuo.. wow ");
        }

        // Instantiates a LocationListener to listen for location changes
        locationListener = new LocationListener() {
            /*
             * Here we listen for location changes, such as moving the
             * device. When a location change is detected, we update the
             * variables with the new position data, and then by every location change we
             * naturally update also the textview.
             */
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
                UpdateUI();
            }

            // If GPS is disabled we output to the user that it is needed to
            // use this feature.
            public void onProviderDisabled(String provider) {
                addressTV.setText("GPS not onn!\nenabel GPS pls.. not wow");

            }

            public void onProviderEnabled(String provider) {
                // Not of use for us in this purpose
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // Not of use for us in this purpose
            }
        };

        /*
			 * A setting for the locationmanager which sets how often to
			 * retrieve location updates. Currently set to every minute
			 * (60000 milliseconds), OR if device has moved 500 meters (or more).
			 */
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 60000, 500, locationListener);
    }

    public void UpdateUI(){
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

        addressTV.setText("such Lat: "+lat+" much Lon: "+lon+" wow");
        windTV.setText(windspeedValue);
        rainTV.setText("so " + weather + " wow");
        temperatureTV.setText(temperatureValue);
        cloudyTV.setText(cloudinessValue);
    }

    class RetrieveWeatherData extends Thread {

        String coordinatesURL;

        public RetrieveWeatherData(double lat, double lon) {
        coordinatesURL = "http://api.yr.no/weatherapi/locationforecast/1.8/?lat="+ lat +";lon="+lon;
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
                temperatureValue = "wow " + temperature.getAttribute("value") + " degeres";

                NodeList windspeedList = location.getElementsByTagName("windSpeed");
                Element windspeed = (Element) windspeedList.item(0);
                windspeedValue = "very windy " + windspeed.getAttribute("mps") + "M/s";

                NodeList cloudinessList = location.getElementsByTagName("cloudiness");
                Element cloudiness = (Element) cloudinessList.item(0);
                cloudinessValue = "many cloud " + cloudiness.getAttribute("percent") + " precent";


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