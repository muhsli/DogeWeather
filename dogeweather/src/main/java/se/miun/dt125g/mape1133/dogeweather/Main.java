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

    String temperature =null;
    String windspeed =null;
    String cloudiness =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/comicsans.ttf");
        TextView tv = (TextView) findViewById(R.id.titleText);
        tv.setTypeface(tf);

        RetrieveWeatherData weather = new RetrieveWeatherData();
        weather.start();

        try {
            weather.join();
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }

    tv.setText(temperature);
    }

class RetrieveWeatherData extends Thread {

    public RetrieveWeatherData(){

    }
    @Override
    public void run() {
        String temperatureString=null;
        String windSpeedString=null;
        String cloudinessString=null;

        try {
            URL weatherCastUrl = new URL("http://api.yr.no/weatherapi/locationforecast/1.8/?lat=62.23534;lon=17.17203");

            InputStream weatherCastInputStream = weatherCastUrl.openStream();

            DocumentBuilderFactory weatherCastInstance = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder weatherCastDocumentBuilder = weatherCastInstance.newDocumentBuilder();
            InputSource weatherCastIs = new InputSource(weatherCastInputStream);
            Document weatherCastXmlDocument = weatherCastDocumentBuilder.parse(weatherCastIs);

            NodeList hourlyForecastList = weatherCastXmlDocument.getElementsByTagName("location");
            Element temperatureElement = (Element) hourlyForecastList.item(0);
            temperatureString = ((Node) temperatureElement).getTextContent();

            Element windSpeedElement = (Element) hourlyForecastList.item(2);
            windSpeedString = ((Node) windSpeedElement).getTextContent();

            Element cloudinessElement = (Element) hourlyForecastList.item(5);
            cloudinessString = ((Node) cloudinessElement).getTextContent();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        temperature = "Temp: "+temperatureString;
        windspeed = "Windspeed: " + windSpeedString;
        cloudiness = "Cloudiness: " + cloudinessString;
    }
}
}