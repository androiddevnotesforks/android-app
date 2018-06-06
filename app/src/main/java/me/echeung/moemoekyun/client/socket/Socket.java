package me.echeung.moemoekyun.client.socket;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.squareup.moshi.Moshi;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.echeung.moemoekyun.client.RadioClient;
import me.echeung.moemoekyun.client.auth.AuthUtil;
import me.echeung.moemoekyun.client.socket.response.BaseResponse;
import me.echeung.moemoekyun.client.socket.response.ConnectResponse;
import me.echeung.moemoekyun.client.socket.response.UpdateResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

@RequiredArgsConstructor
public class Socket extends WebSocketListener {

    private static final String TAG = Socket.class.getSimpleName();

    private static final Moshi MOSHI = new Moshi.Builder().build();

    private static final String TRACK_UPDATE = "TRACK_UPDATE";
    private static final String TRACK_UPDATE_REQUEST = "TRACK_UPDATE_REQUEST";
    private static final String QUEUE_UPDATE = "QUEUE_UPDATE";

    private static final int RETRY_TIME_MIN = 250;
    private static final int RETRY_TIME_MAX = 4000;
    private int retryTime = RETRY_TIME_MIN;
    private boolean attemptingReconnect = false;

    private final OkHttpClient client;
    private final AuthUtil authUtil;

    private WebSocket socket;
    @Setter
    private Listener listener;

    private Handler heartbeatHandler = new Handler();
    private Runnable heartbeatTask;

    public void connect() {
        Log.d(TAG, "Connecting to socket...");

        if (socket != null) {
            disconnect();
        }

        final Request request = new Request.Builder().url(RadioClient.getLibrary().getSocketUrl()).build();
        socket = client.newWebSocket(request, this);
    }

    public void disconnect() {
        clearHeartbeat();

        if (socket != null) {
            socket.cancel();
            socket = null;
        }

        Log.d(TAG, "Disconnected from socket");
    }

    public void reconnect() {
        if (attemptingReconnect) return;

        Log.d(TAG, String.format("Reconnecting to socket in %d ms", retryTime));

        disconnect();

        attemptingReconnect = true;

        // Exponential backoff
        SystemClock.sleep(retryTime);
        if (retryTime < RETRY_TIME_MAX) {
            retryTime *= 2;
        }

        connect();
    }

    public void update() {
        Log.d(TAG, "Requesting update from socket");

        if (socket == null) {
            connect();
            return;
        }

        socket.send("{ \"op\": 2 }");
    }

    @Override
    public void onOpen(WebSocket socket, Response response) {
        Log.d(TAG, "Socket connection opened");

        retryTime = RETRY_TIME_MIN;
        attemptingReconnect = false;

        // Handshake with socket
        final String authToken = authUtil.isAuthenticated() ? authUtil.getAuthTokenWithPrefix() : "";
        socket.send(String.format("{ \"op\": 0, \"d\": { \"auth\": \"%s\" } }", authToken));
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "Received message from socket: " + text);

        parseWebSocketResponse(text);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "Socket failure: " + t.getMessage(), t);
        reconnect();
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.d(TAG, "Socket connection closed: " + reason);
        reconnect();
    }

    private void heartbeat(int milliseconds) {
        clearHeartbeat();

        heartbeatTask = new Runnable() {
            @Override
            public void run() {
                if (socket == null) return;

                Log.d(TAG, "Sending heartbeat to socket");
                socket.send("{ \"op\": 9 }");

                // Repeat
                heartbeatHandler.postDelayed(this, milliseconds);
            }
        };

        heartbeatHandler.postDelayed(heartbeatTask, milliseconds);
        Log.d(TAG, String.format("Created heartbeat task for %d ms", milliseconds));
    }

    private void clearHeartbeat() {
        if (heartbeatTask != null) {
            Log.d(TAG, "Removing heartbeat task");
            heartbeatHandler.removeCallbacksAndMessages(null);
            heartbeatTask = null;
        }
    }

    private void parseWebSocketResponse(String jsonString) {
        if (listener == null) {
            Log.d(TAG, "Listener is null");
            return;
        }

        if (jsonString == null) {
            listener.onSocketFailure();
            return;
        }

        try {
            final BaseResponse baseResponse = MOSHI.adapter(BaseResponse.class).fromJson(jsonString);
            switch (baseResponse.getOp()) {
                // Heartbeat init
                case 0:
                    final ConnectResponse connectResponse = MOSHI.adapter(ConnectResponse.class).fromJson(jsonString);
                    heartbeat(connectResponse.getD().getHeartbeat());
                    break;

                // Track update
                case 1:
                    final UpdateResponse updateResponse = MOSHI.adapter(UpdateResponse.class).fromJson(jsonString);
                    if (!isValidUpdate(updateResponse)) {
                        return;
                    }
                    listener.onSocketReceive(updateResponse.getD());
                    break;

                // Heartbeat ACK
                case 10:
                    break;

                default:
                    Log.d(TAG, "Received invalid socket data: " + jsonString);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse socket data: " + jsonString, e);
        }
    }

    private boolean isValidUpdate(UpdateResponse updateResponse) {
        return updateResponse.getT().equals(TRACK_UPDATE)
                || updateResponse.getT().equals(TRACK_UPDATE_REQUEST)
                || updateResponse.getT().equals(QUEUE_UPDATE);
    }

    public interface Listener {
        void onSocketReceive(UpdateResponse.Details info);
        void onSocketFailure();
    }

}
