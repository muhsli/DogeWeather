package se.miun.dt125g.mape1133.dogeweather;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class JSONparser extends AsyncTask<String, Void, JSONObject> {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    private MyCallbackInterface mCallback;

    public interface MyCallbackInterface{
        public void onRequestComplete(JSONObject result);

    }


    public JSONparser(MyCallbackInterface callback){
        mCallback = callback;
    }


    public JSONObject getJSONFromURL(String url){

        try{
            DefaultHttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        } catch (ClientProtocolException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"),8000);
            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e){
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        try{
            jObj = new JSONObject(json);
        } catch (JSONException e){
            Log.e("JSON Parser", "Error parsing data (LOLL?)" + e.toString());
        }

        return jObj;
    }

    @Override
    protected JSONObject doInBackground(String... params){
        String url = params[0];
        return getJSONFromURL(url);
    }

    @Override
    protected void onPostExecute(JSONObject result){
        mCallback.onRequestComplete(result);
    }
}
