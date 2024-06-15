package com.shop.server;

import com.shop.common.RequestResponse;
import com.shop.common.UserType;
import com.shop.server.model.User;
import org.hibernate.Session;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        RequestResponse response;
        try {
            session.beginTransaction();
            User user = new User(request.getField(String.class, "username"),
                    getHash(request.getField(String.class, "password")),
                    request.getField(UserType.class, "user_type"), 0);
            session.persist(user);
            session.getTransaction().commit();

            response = new RequestResponse(SUCCESSFUL_REGISTRATION);
            response.setField("user_type", request.getField(UserType.class, "user_type"));
            response.setField("username", request.getField(String.class, "username"));
            response.setField("password", request.getField(String.class, "password"));
        }catch (Exception ex) {
            session.getTransaction().rollback();
            response = new RequestResponse(REGISTRATION_ERROR);
            ex.printStackTrace();
        }finally {
            session.close();
        }
        writeResponse(response);
    }

    private void logIn(RequestResponse request) {
        System.out.println("\nStart logIn");
        Session session = server.getSessionFactory().openSession();
        RequestResponse response = null;
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
                response = new RequestResponse(SUCCESSFUL_LOG_IN);
                response.setField("user_type", request.getField(UserType.class, "user_type"));
            }
            session.getTransaction().commit();
        }catch (Exception ex) {
            session.getTransaction().rollback();
            response = new RequestResponse(LOG_IN_ERROR);
            ex.printStackTrace();
        }finally {
            session.close();
        }
        writeResponse(response);
    }
}
