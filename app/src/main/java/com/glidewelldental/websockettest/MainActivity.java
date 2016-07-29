package com.glidewelldental.websockettest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    WebSocketClient mWebSocketClient;
    ScrollView scrollView;
    TextView output;
    ImageView imageView;
    EditText url;

    String message = "Hello World!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        output = (TextView) findViewById(R.id.output);
        url = (EditText) findViewById(R.id.websockerurl);
        imageView = (ImageView) findViewById(R.id.icon);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        url.setText("ws://echo.websocket.org");
        changeIcon(false);
    }

    public void connectWebSocket(View view) {
        if(mWebSocketClient == null || !(mWebSocketClient.getReadyState() == WebSocket.READYSTATE.OPEN)) {

            URI uri;
            try {
                uri = new URI(url.getText().toString());
                mWebSocketClient = new WebSocketClient(uri) {
                    @Override
                    public void onOpen(ServerHandshake serverHandshake) {
                        display("WebSocket Opened!");
                        changeIcon(true);
                    }

                    @Override
                    public void onMessage(String s) {
                        display("Received : "+s);
                    }

                    @Override
                    public void onClose(int i, String s, boolean b) {
                        display("WebSocket closed!");
                        changeIcon(false);
                    }

                    @Override
                    public void onError(Exception e) {
                        display("Error "+e.getMessage());
                    }
                };
                mWebSocketClient.connect();
                display("Trying to connect...");
            } catch (URISyntaxException e) {
                display(e.getMessage());
                e.printStackTrace();
            }
        }else{
            display("You already have an open connevtion");
        }
    }

    public void sendWebSocket(View view) {
        if(mWebSocketClient.getReadyState() == WebSocket.READYSTATE.OPEN) {
            display("Sending : "+message);
            mWebSocketClient.send(message);
        }else{
            display("You need to connect first");
        }
    }

    public void closeWebSocket(View view) {
        if(mWebSocketClient.getReadyState() == WebSocket.READYSTATE.OPEN) {
            display("Closing connection");
            mWebSocketClient.close();
        }else{
            display("You need to connect first");
        }
    }

    public void display(String s){
        final String message = s;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                output.setText(output.getText() + "\n" + message);
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public void changeIcon(final boolean isConnected){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isConnected){
                    imageView.setBackgroundResource(R.drawable.plus);
                }else{
                    imageView.setBackgroundResource(R.drawable.x);
                }
            }
        });
    }
}
