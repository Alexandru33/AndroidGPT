package ro.pub.cs.systems.eim.androidgpt.network;

import android.media.Image;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.androidgpt.general.Constants;
import ro.pub.cs.systems.eim.androidgpt.general.Utilities;


public class ClientThread extends  Thread{

    private final String address = "localhost";
    private final int port;

    private final String numePokemon;
    private final TextView responseTextView;
    private final ImageView imageView;


    private Socket socket;

    public ClientThread(int port, String informationType, TextView responseTextView, ImageView imageView) {
        this.port = port;
        this.numePokemon = informationType;
        this.responseTextView = responseTextView;
        this.imageView = imageView;

    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            printWriter.println(numePokemon);
            printWriter.flush();

            String imageString = bufferedReader.readLine();
            imageView.post(() -> Picasso.get().load(imageString).into(imageView));


            String responseInformation;
            while ((responseInformation = bufferedReader.readLine()) != null) {
                final String finalizedResponseInformation = responseInformation;
                responseTextView.post(() -> responseTextView.setText(finalizedResponseInformation));
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }
}
