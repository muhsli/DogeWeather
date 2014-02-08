package se.miun.dt125g.mape1133.dogeweather;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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

    int symbolValue = 1337;
    String weather = "much";
    String temperatureValue ="wow";
    String windspeedValue ="such very";
    String cloudinessValue ="many so";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/comicsans.ttf");
        TextView titleTV = (TextView) findViewById(R.id.titleText);
        titleTV.setTypeface(tf);
        TextView windTV = (TextView) findViewById(R.id.windText);
        windTV.setTypeface(tf);
        TextView rainTV = (TextView) findViewById(R.id.rainText);
        rainTV.setTypeface(tf);
        TextView temperatureTV = (TextView) findViewById(R.id.temperatureText);
        temperatureTV.setTypeface(tf);
        TextView cloudyTV = (TextView) findViewById(R.id.cloudyText);
        cloudyTV.setTypeface(tf);

        RetrieveWeatherData retrieveWeatherDataThread = new RetrieveWeatherData();
        retrieveWeatherDataThread.start();

        try {
            retrieveWeatherDataThread.join();
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }

        if (symbolValue == 4){
            weather = "cloud";
        }
        titleTV.setText("wow");
        windTV.setText("very windy "+windspeedValue+"M/s");
        rainTV.setText("so "+weather+" wow");
        temperatureTV.setText("wow " + temperatureValue + " degeres");
        cloudyTV.setText("many cloud "+cloudinessValue+ " precent");
    }

class RetrieveWeatherData extends Thread {

    public RetrieveWeatherData(){

    }
    @Override
    public void run() {

        try {
            URL weatherCastUrl = new URL("http://api.yr.no/weatherapi/locationforecast/1.8/?lat=62.23534;lon=17.17203");

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
            temperatureValue = temperature.getAttribute("value");

            NodeList windspeedList = location.getElementsByTagName("windSpeed");
            Element windspeed = (Element) windspeedList.item(0);
            windspeedValue = windspeed.getAttribute("mps");

            NodeList cloudinessList = location.getElementsByTagName("cloudiness");
            Element cloudiness = (Element) cloudinessList.item(0);
            cloudinessValue = cloudiness.getAttribute("percent");


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