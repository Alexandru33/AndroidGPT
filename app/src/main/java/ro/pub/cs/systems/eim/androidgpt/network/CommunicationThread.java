package ro.pub.cs.systems.eim.androidgpt.network;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.eim.androidgpt.general.Constants;
import ro.pub.cs.systems.eim.androidgpt.general.Utilities;


public class CommunicationThread extends Thread {

    private final Socket socket;

    public CommunicationThread( Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void run() {

        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }

        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (Pokemon name) !");
            String pokemonName = bufferedReader.readLine();
            if (pokemonName == null || pokemonName.isEmpty() ) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (pokemonName) !");
                return;
            }




            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Doing fetch...");

            HttpClient httpClient = new DefaultHttpClient();
            String pageSourceCode = "";
            HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + pokemonName);
            HttpResponse httpGetResponse = httpClient.execute(httpGet);
            HttpEntity httpGetEntity = httpGetResponse.getEntity();
            if (httpGetEntity != null) {
                pageSourceCode = EntityUtils.toString(httpGetEntity);
            }

            JSONObject content = new JSONObject(pageSourceCode);

            String types = content.getJSONArray("types").toString();
            String abilities = content.getJSONArray("abilities").toString();
            String  imageURL = content.getJSONObject("sprites").getString("front_default");


            Log.i(Constants.TAG, "[UPDATE THREAD] Fetch done!");


            printWriter.println(imageURL);
            printWriter.flush();

            printWriter.println(types);
            printWriter.flush();

            printWriter.println(abilities);
            printWriter.flush();


        } catch (Exception ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }
    }
}
