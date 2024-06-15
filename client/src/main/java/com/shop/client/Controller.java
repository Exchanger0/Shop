package com.shop.client;

import com.shop.common.RequestResponse;
import com.shop.common.UserType;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static com.shop.common.RequestResponse.Title.*;

public class Controller {
    private final Starter starter;
    private final Socket socket;
    private final ObjectOutputStream writer;
    private final ObjectInputStream reader;
    private final Thread listener;

    public Controller(Starter starter) throws IOException {
        this.starter = starter;
        this.socket = new Socket("localhost", 9087);
        this.writer = new ObjectOutputStream(socket.getOutputStream());
        this.reader = new ObjectInputStream(socket.getInputStream());
        this.listener = new Thread(new ServerListener());
        listener.start();
    }

    public void exit() {
        try {
            writer.writeObject(new RequestResponse(EXIT));
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void registration(UserType userType, String username, String password) {
        regLog(REGISTRATION, userType, username, password);
    }

    public void logIn(UserType userType, String username, String password) {
        regLog(LOG_IN, userType, username, password);
    }

    private void regLog(RequestResponse.Title title, UserType userType, String username, String password) {
        try {
            RequestResponse request = new RequestResponse(title);
            request.setField("user_type", userType);
            request.setField("username", username);
            request.setField("password", password);

            writer.writeObject(request);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class ServerListener implements Runnable {

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    RequestResponse response = (RequestResponse) reader.readObject();
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    switch (response.getTitle()) {
                        case SUCCESSFUL_REGISTRATION, REGISTRATION_ERROR -> Platform.runLater(() -> starter.registration(response));
                        case SUCCESSFUL_LOG_IN, LOG_IN_ERROR -> Platform.runLater(() -> starter.logIn(response));
                        case EXIT -> {
                            writer.close();
                            reader.close();
                            socket.close();
                            listener.interrupt();
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
