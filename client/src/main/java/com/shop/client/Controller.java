package com.shop.client;

import com.shop.common.RequestResponse;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.List;

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

    public void registration(String username, String password) {
        regLog(REGISTRATION, username, password);
    }

    public void logIn(String username, String password) {
        regLog(LOG_IN, username, password);
    }

    private void regLog(RequestResponse.Title title, String username, String password) {
        try {
            RequestResponse request = new RequestResponse(title);
            request.setField("username", username);
            request.setField("password", password);

            writer.writeObject(request);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createProduct(String name, String description, BigDecimal price, List<byte[]> images) {
        try {
            RequestResponse request = new RequestResponse(CREATE_PRODUCT);
            request.setField("name", name);
            request.setField("description", description);
            request.setField("price", price);
            request.setField("images", images);

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
