package com.shop.server;

import com.shop.common.RequestResponse;
import com.shop.server.model.Picture;
import com.shop.server.model.Product;
import com.shop.server.model.User;
import org.hibernate.Session;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.shop.common.RequestResponse.Title.*;

public class ClientHandler implements Runnable{
    private final Server server;
    private final Socket socket;
    private final ObjectInputStream reader;
    private final ObjectOutputStream writer;
    private User currentUser;

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.writer = new ObjectOutputStream(socket.getOutputStream());
        this.reader = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
               RequestResponse request = (RequestResponse) reader.readObject();
                switch (request.getTitle()) {
                    case REGISTRATION -> registration(request);
                    case LOG_IN -> logIn(request);
                    case CREATE_PRODUCT -> createProduct(request);
                    case GET_CREATED_PRODUCTS -> getCreatedProducts();
                    case REMOVE_PRODUCT -> removeProduct(request);
                    case EXIT -> {
                        writer.writeObject(request);
                        writer.flush();
                        writer.close();
                        reader.close();
                        socket.close();
                        Thread.currentThread().interrupt();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            BigInteger bigInteger = new BigInteger(1, hash);
            return bigInteger.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeResponse(RequestResponse response) {
        try {
            writer.writeObject(response);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void registration(RequestResponse request) {
        System.out.println("\nStart registration");
        Session session = server.getSessionFactory().openSession();
        try {
            session.beginTransaction();
            User user = new User(request.getField(String.class, "username"),
                    getHash(request.getField(String.class, "password")), 0);
            session.persist(user);
            session.getTransaction().commit();

            request.setTitle(SUCCESSFUL_REGISTRATION);
        }catch (Exception ex) {
            session.getTransaction().rollback();
            request.setTitle(REGISTRATION_ERROR);
        }finally {
            session.close();
        }
        writeResponse(request);
    }

    private void logIn(RequestResponse request) {
        System.out.println("\nStart logIn");
        Session session = server.getSessionFactory().openSession();
        try {
            session.beginTransaction();

            User user = session.createQuery(
                    "SELECT u FROM User u " +
                    "WHERE u.username = :username AND u.password = :password", User.class)
                    .setParameter("username", request.getField(String.class, "username"))
                    .setParameter("password", getHash(request.getField(String.class, "password")))
                    .getSingleResult();

            if (user != null) {
                currentUser = user;
                request.setTitle(SUCCESSFUL_LOG_IN);
                request.setField("balance", user.getBalance());
            }
            session.getTransaction().commit();
        }catch (Exception ex) {
            session.getTransaction().rollback();
            request.setTitle(LOG_IN_ERROR);
        }finally {
            session.close();
        }
        writeResponse(request);
    }

    private void createProduct(RequestResponse request) {
        System.out.println("\nStart create product");
        Session session = server.getSessionFactory().openSession();

        try {
            session.beginTransaction();
            currentUser = session.get(User.class, currentUser.getId());

            List<Picture> pictures = new ArrayList<>();
            System.out.println(request.getField(ArrayList.class, "images"));
            for (byte[] img : (List<byte[]>) request.getField(ArrayList.class, "images")) {
                Picture picture = new Picture(img);
                pictures.add(picture);
                session.persist(picture);
            }

            Product product = new Product(request.getField(String.class, "name"),
                    request.getField(String.class, "description"), request.getField(BigDecimal.class, "price"),
                    request.getField(Integer.class, "amount"), pictures);
            currentUser.getCreatedProducts().add(product);

            session.persist(product);
            session.getTransaction().commit();
            request.setField("id", product.getId());
            writeResponse(request);
        } catch (Exception ex) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }

    }

    private void getCreatedProducts() {
        System.out.println("\nStart get created products");
        Session session = server.getSessionFactory().openSession();

        try {
            session.beginTransaction();
            currentUser = session.get(User.class, currentUser.getId());

            List<RequestResponse> createdProducts = new ArrayList<>();

            currentUser.getCreatedProducts().forEach(product -> {
                RequestResponse info = new RequestResponse();
                info.setField("id", product.getId());
                info.setField("name", product.getName());
                info.setField("description", product.getDescription());
                info.setField("price", product.getPrice());
                info.setField("amount", product.getAmount());
                info.setField("images", product.getPictures()
                        .stream()
                        .map(Picture::getImage)
                        .collect(Collectors.toCollection(ArrayList::new)));
                createdProducts.add(info);
            });

            RequestResponse response = new RequestResponse(GET_CREATED_PRODUCTS);
            response.setField("created_products", createdProducts);
            writeResponse(response);

            session.getTransaction().commit();
        } catch (Exception ex) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    private void removeProduct(RequestResponse request) {
        System.out.println("\nStart remove product");
        Session session = server.getSessionFactory().openSession();

        try {
            session.beginTransaction();
            currentUser = session.get(User.class, currentUser.getId());

            Product product = session.createQuery("SELECT p FROM Product p WHERE p.id = :id", Product.class)
                    .setParameter("id", request.getField(Integer.class, "id"))
                    .getSingleResult();
            if (request.getField(Integer.class, "amount") == product.getAmount()) {
                currentUser.getCreatedProducts().remove(product);
                session.remove(product);
            }else {
                product.setAmount(product.getAmount() - request.getField(Integer.class, "amount"));
            }
            session.getTransaction().commit();
            writeResponse(request);
        } catch (Exception ex) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

}
