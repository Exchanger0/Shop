package com.shop.client;

import com.shop.common.model.Order;
import com.shop.common.model.Product;
import com.shop.common.RequestResponse;
import com.shop.common.model.User;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static com.shop.common.RequestResponse.Title.*;

public class Controller {
    private final Starter starter;
    private final Socket socket;
    private final ObjectOutputStream writer;
    private final ObjectInputStream reader;
    private final Thread listener;
    private final CyclicBarrier wait = new CyclicBarrier(2);
    private User currentUser;
    private int offset = 0;
    private final int LIMIT = 40;
    private List<Product> products = new ArrayList<>();

    public Controller(Starter starter) throws IOException {
        this.starter = starter;
        this.socket = new Socket("localhost", 9087);
        this.writer = new ObjectOutputStream(socket.getOutputStream());
        this.reader = new ObjectInputStream(socket.getInputStream());
        this.listener = new Thread(new ServerListener());
        listener.start();
    }

    private void writeRequest(RequestResponse request) {
        try {
            writer.writeObject(request);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void exit() {
        writeRequest(new RequestResponse(EXIT));
    }

    public void registration(String username, String password) {
        regLog(REGISTRATION, username, password);
    }

    public void logIn(String username, String password) {
        regLog(LOG_IN, username, password);
    }

    private void regLog(RequestResponse.Title title, String username, String password) {
        RequestResponse request = new RequestResponse(title);
        request.setField("username", username);
        request.setField("password", password);
        writeRequest(request);
    }

    public void createProduct(String name, String description, BigDecimal price, int amount, List<byte[]> images) {
        RequestResponse request = new RequestResponse(CREATE_PRODUCT);
        request.setField("name", name);
        request.setField("description", description);
        request.setField("price", price);
        request.setField("amount", amount);
        request.setField("images", images);
        writeRequest(request);
    }

    public List<Product> getCreatedProducts() {
        if (currentUser.getCreatedProducts() == null) {
            writeRequest(new RequestResponse(GET_CREATED_PRODUCTS));
            try {
                wait.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }

        return currentUser.getCreatedProducts();
    }

    public void removeCreatedProduct(int productId, int amount) {
        RequestResponse request = new RequestResponse(REMOVE_CREATED_PRODUCT);
        request.setField("id", productId);
        request.setField("amount", amount);
        writeRequest(request);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void topUpBalance(int amount) {
        RequestResponse request = new RequestResponse(TOP_UP_BALANCE);
        request.setField("amount", amount);
        writeRequest(request);
    }

    public List<Product> getNextProducts() {
        RequestResponse request = new RequestResponse(GET_PRODUCTS);
        request.setField("limit", LIMIT);
        request.setField("offset", offset);
        writeRequest(request);
        try {
            wait.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    public List<Product> getPreviousProducts() {
        int preoffset = offset - LIMIT*2;
        if (preoffset >= 0) {
            offset = preoffset;
            return getNextProducts();
        } else {
            return products;
        }
    }

    public void addToCart(int productId) {
        RequestResponse request = new RequestResponse(ADD_TO_CART);
        request.setField("id", productId);
        writeRequest(request);
    }

    public List<Product> getCart() {
        if (currentUser.getCart() == null) {
            writeRequest(new RequestResponse(GET_CART));
            try {
                wait.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }
        return currentUser.getCart();
    }

    public void removeCartProduct(int productId) {
        RequestResponse request = new RequestResponse(REMOVE_CART_PRODUCT);
        request.setField("id", productId);
        writeRequest(request);
    }

    public List<Order> getOrders() {
        if (currentUser.getOrders() == null) {
            writeRequest(new RequestResponse(GET_ORDERS));
            try {
                wait.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }
        return currentUser.getOrders();
    }

    public void makeOrder(Map<Integer, Integer> products, BigDecimal totalPrice) {
        RequestResponse request = new RequestResponse(MAKE_ORDER);
        request.setField("products", products);
        request.setField("total_price", totalPrice);
        writeRequest(request);
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
                        case SUCCESSFUL_REGISTRATION, REGISTRATION_ERROR ->
                                Platform.runLater(() -> starter.registration(response));
                        case SUCCESSFUL_LOG_IN -> {
                            currentUser = response.getField(User.class, "user");
                            Platform.runLater(() -> starter.logIn(response));
                        }
                        case LOG_IN_ERROR -> Platform.runLater(() -> starter.logIn(response));
                        case CREATE_PRODUCT -> {
                            if (currentUser.getCreatedProducts() != null) {
                                currentUser.getCreatedProducts().add(response.getField(Product.class, "product"));
                            }
                        }
                        case GET_CREATED_PRODUCTS -> {
                            ArrayList<Product> createdProducts = response.getField(ArrayList.class, "created_products");
                            currentUser.setCreatedProducts(createdProducts);
                            wait.await();
                        }
                        case REMOVE_CREATED_PRODUCT -> {
                            int am = response.getField(Integer.class, "amount");
                            Product product = currentUser.getCreatedProducts()
                                    .stream()
                                    .filter(pr -> pr.getId() == response.getField(Integer.class, "id"))
                                    .findFirst().orElse(null);
                            if (product != null) {
                                if (product.getAmount() == am) {
                                    Platform.runLater(() -> starter.getShopMenu().getCreatedGoodsPane().removeProduct(product.getId()));
                                    currentUser.getCreatedProducts().remove(product);
                                } else {
                                    product.setAmount(product.getAmount() - am);
                                    Platform.runLater(() -> starter.getShopMenu().getCreatedGoodsPane().updateAmount(product.getId(), product.getAmount()));
                                }
                            }
                        }
                        case TOP_UP_BALANCE -> {
                            currentUser.setBalance(currentUser.getBalance() + response.getField(Integer.class, "amount"));
                            Platform.runLater(() ->
                                    starter.getShopMenu().getProfilePane().updateBalance(currentUser.getBalance()));
                        }
                        case GET_PRODUCTS -> {
                            List<Product> prs = response.getField(ArrayList.class, "products");
                            System.out.println(prs);
                            if (!prs.equals(products)) {
                                System.out.println(true);
                                offset += LIMIT;
                                products = prs;
                            }
                            wait.await();
                        }
                        case ADD_TO_CART -> {
                            boolean success = response.getField(Boolean.class, "success");
                            if (success && currentUser.getCart() != null) {
                                currentUser.getCart().add(response.getField(Product.class, "product"));
                            }
                        }
                        case GET_CART -> {
                            ArrayList<Product> cart = response.getField(ArrayList.class, "cart");
                            currentUser.setCart(cart);
                            wait.await();
                        }
                        case REMOVE_CART_PRODUCT -> {
                            Product product = currentUser.getCart()
                                    .stream()
                                    .filter(pr -> pr.getId() == response.getField(Integer.class, "id"))
                                    .findFirst().orElse(null);

                            if (product != null) {
                                Platform.runLater(() -> starter.getShopMenu().getCartPane().removeProduct(product.getId()));
                                currentUser.getCart().remove(product);
                            }
                        }
                        case GET_ORDERS -> {
                            ArrayList<Order> orders = response.getField(ArrayList.class, "orders");
                            currentUser.setOrders(orders);
                            wait.await();
                        }
                        case MAKE_ORDER -> {
                            Order order = response.getField(Order.class, "order");
                            currentUser.getOrders().add(order);
                            Platform.runLater(() -> starter.getShopMenu().getOrderPane().addOrder(order));
                            List<Product> updatedProducts = response.getField(ArrayList.class, "updated_products");
                            for (Product p : updatedProducts) {
                                int index = currentUser.getCart().indexOf(p);
                                if (index != -1) {
                                    currentUser.getCart().get(index).setAmount(p.getAmount());
                                }
                            }
                            currentUser.setBalance(response.getField(Integer.class, "balance"));
                            Platform.runLater(() -> starter.getShopMenu().getProfilePane().updateBalance(currentUser.getBalance()));
                        }
                        case MAKE_ORDER_ERROR -> Platform.runLater(() -> starter.showErrors(response.getField(ArrayList.class, "errors")));
                        case EXIT -> {
                            writer.close();
                            reader.close();
                            socket.close();
                            listener.interrupt();
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException | BrokenBarrierException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
