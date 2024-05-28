package ro.pub.cs.systems.eim.androidgpt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ro.pub.cs.systems.eim.androidgpt.general.Constants;
import ro.pub.cs.systems.eim.androidgpt.network.ClientThread;
import ro.pub.cs.systems.eim.androidgpt.network.ServerThread;

public class MainActivity extends AppCompatActivity {

    private EditText editTextServerPort = null;
    private Button buttonServer = null;
    private Button buttonClient = null;

    private TextView textViewResponse = null;
    private ImageView imageViewResponse = null;
    private EditText editTextPokemon = null;


    private ServerThread serverThread = null ;

    private class ClientButtonListener implements Button.OnClickListener {

    @Override
    public void onClick(View view) {

        String clientPort = editTextServerPort.getText().toString();
        String numePokemon  = editTextPokemon.getText().toString();


        if (clientPort.isEmpty()) {
            Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (serverThread == null || !serverThread.isAlive()) {
            Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (numePokemon.isEmpty()) {
            Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        textViewResponse.setText(Constants.EMPTY_STRING);

        ClientThread clientThread = new ClientThread(
                Integer.parseInt(clientPort), numePokemon, textViewResponse, imageViewResponse
        );
        clientThread.start();

    }
}
private class ServerButtonListener implements Button.OnClickListener {

    @Override
    public void onClick(View view) {

        String serverPort = editTextServerPort.getText().toString();

        if (serverPort.isEmpty()) {
            Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
            return;
        }

        serverThread = new ServerThread(Integer.parseInt(serverPort));

        if (serverThread.getServerSocket() == null) {
            Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
            return;
        }
        serverThread.start();

    }
}



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editTextServerPort = (EditText) findViewById(R.id.editTextPortServer);
        buttonClient = (Button) findViewById(R.id.buttonClient);
        buttonServer = (Button) findViewById(R.id.buttonServer);
        textViewResponse = (TextView) findViewById(R.id.textViewResponse);
        imageViewResponse = (ImageView) findViewById(R.id.imageViewResponse);
        editTextPokemon = (EditText) findViewById(R.id.editTextPokemon);


        buttonClient.setOnClickListener( new ClientButtonListener());
        buttonServer.setOnClickListener(new ServerButtonListener());





    }
}