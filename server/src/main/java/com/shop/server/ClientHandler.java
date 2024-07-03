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
                    case REMOVE_CREATED_PRODUCT -> removeCreatedProduct(request);
                    case TOP_UP_BALANCE -> topUpBalance(request);
                    case GET_PRODUCTS -> getProducts(request);
                    case ADD_TO_CART -> addToCart(request);
                    case GET_CART -> getCart();
                    case REMOVE_CART_PRODUCT -> removeCartProduct(request);
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

    private com.shop.common.model.User toCommonUser(User user) {
        return new com.shop.common.model.User(user.getUsername(), user.getBalance());
    }

    private com.shop.common.model.Product toCommonProduct(Product product) {
        return new com.shop.common.model.Product(product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getAmount(),
                product.getPictures()
                        .stream()
                        .map(Picture::getImage)
                        .collect(Collectors.toCollection(ArrayList::new)));
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
        RequestResponse response = new RequestResponse();
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
                response.setTitle(SUCCESSFUL_LOG_IN);
                response.setField("user", toCommonUser(user));
            }
            session.getTransaction().commit();
        }catch (Exception ex) {
            session.getTransaction().rollback();
            response.setTitle(LOG_IN_ERROR);
        }finally {
            session.close();
        }
        writeResponse(response);
    }

    private void createProduct(RequestResponse request) {
        System.out.println("\nStart create product");
        Session session = server.getSessionFactory().openSession();
        RequestResponse response = new RequestResponse(CREATE_PRODUCT);
        try {
            session.beginTransaction();
            currentUser = session.get(User.class, currentUser.getId());

            List<Picture> pictures = new ArrayList<>();
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
            response.setField("product", toCommonProduct(product));
            writeResponse(response);
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

            List<com.shop.common.model.Product> createdProducts = new ArrayList<>();

            currentUser.getCreatedProducts().forEach(product -> createdProducts.add(toCommonProduct(product)));

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

    private void removeCreatedProduct(RequestResponse request) {
        System.out.println("\nStart remove created product");
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

    private void topUpBalance(RequestResponse request) {
        System.out.println("\nStart top up balance");
        Session session = server.getSessionFactory().openSession();
        try {
            session.beginTransaction();
            currentUser = session.get(User.class, currentUser.getId());

            currentUser.setBalance(currentUser.getBalance() + request.getField(Integer.class, "amount"));

            session.getTransaction().commit();
            writeResponse(request);
        } catch (Exception ex) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    private void getProducts(RequestResponse request) {
        System.out.println("\nStart get products");
        Session session = server.getSessionFactory().openSession();
        try {
            session.beginTransaction();

            List<Product> products = session.createQuery(
                    "SELECT p FROM Product p JOIN FETCH p.pictures", Product.class)
                    .setFirstResult(request.getField(Integer.class, "offset"))
                    .setMaxResults(request.getField(Integer.class, "limit"))
                    .getResultList();

            List<com.shop.common.model.Product> commonProducts = new ArrayList<>();
            for (Product pr : products) {
                commonProducts.add(toCommonProduct(pr));
            }

            RequestResponse response = new RequestResponse(GET_PRODUCTS);
            response.setField("offset", products.size());
            response.setField("products", commonProducts);
            writeResponse(response);

            session.getTransaction().commit();
        } catch (Exception ex) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    public void addToCart(RequestResponse request) {
        System.out.println("\nStart add to cart");
        Session session = server.getSessionFactory().openSession();
        try {
            session.beginTransaction();
            currentUser = session.get(User.class, currentUser.getId());

            Product product = session.createQuery("SELECT p FROM Product p WHERE p.id = :id", Product.class)
                            .setParameter("id", request.getField(Integer.class, "id"))
                            .getSingleResult();

            currentUser.getCart().add(product);
            session.getTransaction().commit();

            RequestResponse response = new RequestResponse(ADD_TO_CART);
            response.setField("product", toCommonProduct(product));
            writeResponse(response);
        } catch (Exception ex) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    private void getCart() {
        System.out.println("\nStart get cart");
        Session session = server.getSessionFactory().openSession();
        try {
            session.beginTransaction();
            currentUser = session.get(User.class, currentUser.getId());

            List<com.shop.common.model.Product> cart = new ArrayList<>();

            currentUser.getCart().forEach(product -> cart.add(toCommonProduct(product)));

            RequestResponse response = new RequestResponse(GET_CART);
            response.setField("cart", cart);
            writeResponse(response);

            session.getTransaction().commit();
        } catch (Exception ex) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    private void removeCartProduct(RequestResponse request) {
        System.out.println("\nStart remove cart product");
        Session session = server.getSessionFactory().openSession();
        try {
            session.beginTransaction();
            currentUser = session.get(User.class, currentUser.getId());

            Product product = session.createQuery("SELECT p FROM Product p WHERE p.id = :id", Product.class)
                    .setParameter("id", request.getField(Integer.class, "id"))
                    .getSingleResult();

            currentUser.getCart().remove(product);

            session.getTransaction().commit();
            writeResponse(request);
        } catch (Exception ex) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

}
