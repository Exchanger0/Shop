package com.shop.client;

import com.shop.client.model.Product;
import com.shop.client.model.User;
import com.shop.common.RequestResponse;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.shop.common.RequestResponse.Title.*;

public class Controller {
    private final Starter starter;
    private final Socket socket;
    private final ObjectOutputStream writer;
    private final ObjectInputStream reader;
    private final Thread listener;
    private User currentUser;

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

    public void loadCreatedProducts() {
        List<Product> products = currentUser.getCreatedProducts();
        if (products == null) {
            try {
                RequestResponse request = new RequestResponse(GET_CREATED_PRODUCTS);
                writer.writeObject(request);
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            Platform.runLater(() -> starter.getShopMenu().getCreatedGoodsPane().addProducts(products));
        }
    }

    private void loadCreatedProducts(RequestResponse response) {
        List<RequestResponse> createdProducts = response.getField(ArrayList.class, "created_products");
        System.out.println(createdProducts);
        List<Product> createdPr = new ArrayList<>();
        for (RequestResponse info : createdProducts) {
            Product product = new Product(info.getField(String.class, "name"),
                    info.getField(String.class, "description"),
                    info.getField(BigDecimal.class, "price"),
                    info.getField(ArrayList.class, "images"));
            System.out.println(product.getPictures());
            createdPr.add(product);
        }
        currentUser.setCreatedProducts(createdPr);
        Platform.runLater(() -> starter.getShopMenu().getCreatedGoodsPane().addProducts(createdPr));
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
                        case SUCCESSFUL_LOG_IN -> {
                            currentUser = new User(
                                    response.getField(String.class, "username"),
                                    response.getField(String.class, "password"),
                                    response.getField(Integer.class, "balance"));
                            Platform.runLater(() -> starter.logIn(response));
                        }
                        case LOG_IN_ERROR -> Platform.runLater(() -> starter.logIn(response));
                        case CREATE_PRODUCT -> {
                            Product pr = new Product(
                                    response.getField(String.class, "name"),
                                    response.getField(String.class, "description"),
                                    response.getField(BigDecimal.class, "price"),
                                    response.getField(ArrayList.class, "images")
                            );
                            currentUser.getCreatedProducts().add(pr);
                            Platform.runLater(() -> starter.getShopMenu().getCreatedGoodsPane().addProduct(pr));
                        }
                        case GET_CREATED_PRODUCTS -> loadCreatedProducts(response);
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
