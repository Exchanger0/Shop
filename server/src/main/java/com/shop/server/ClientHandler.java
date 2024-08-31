package com.shop.server;

import com.shop.common.RequestResponse;
import com.shop.common.model.ProductType;
import com.shop.server.model.Order;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.shop.common.RequestResponse.Title.*;

/*
1. При создании продукта уведомить пользователей о его создании
2. При добавлении товара в корзину
3. При изменении баланса
4. При оформлении заказа
 */

public class ClientHandler implements Runnable{
    private final Server server;
    private final Socket socket;
    private final ObjectInputStream reader;
    private final ObjectOutputStream writer;
    private User currentUser;
    private Notifier notifier;

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
                    case GET_ORDERS -> getOrders();
                    case MAKE_ORDER -> makeOrder(request);
                    case EXIT -> {
                        writer.writeObject(request);
                        writer.flush();
                        writer.close();
                        reader.close();
                        socket.close();
                        notifier.removeClient(this);
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

    public void writeResponse(RequestResponse response) {
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
                product.getPrice(), product.getAmount(), product.getType(),
                product.getPictures()
                        .stream()
                        .map(Picture::getImage)
                        .collect(Collectors.toCollection(ArrayList::new)));
    }

    private List<com.shop.common.model.Product> toCommonProducts(List<Product> products) {
        return products.stream().map(this::toCommonProduct).collect(Collectors.toCollection(ArrayList::new));
    }

    private com.shop.common.model.Order toCommonOrder(Order order) {
        return new com.shop.common.model.Order(order.getId(), order.getTotalPrice(), toCommonProducts(order.getProducts()));
    }

    private List<com.shop.common.model.Order> toCommonOrders(List<Order> order) {
        return order.stream().map(this::toCommonOrder).collect(Collectors.toCollection(ArrayList::new));
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
                notifier = server.registerClientHandler(user, this);
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
                    request.getField(Integer.class, "amount"), ProductType.FOR_SALE, pictures);
            currentUser.getCreatedProducts().add(product);

            session.persist(product);
            session.getTransaction().commit();
            response.setField("product", toCommonProduct(product));
            writeResponse(response);
            notifier.notifyClients(this, response);
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

            List<com.shop.common.model.Product> createdProducts = toCommonProducts(currentUser.getCreatedProducts());
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
            notifier.notifyClients(this, request);
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
            notifier.notifyClients(this, request);
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
                    "SELECT p FROM Product p JOIN FETCH p.pictures WHERE p.type = ProductType.FOR_SALE", Product.class)
                    .setFirstResult(request.getField(Integer.class, "offset"))
                    .setMaxResults(request.getField(Integer.class, "limit"))
                    .getResultList();

            List<com.shop.common.model.Product> commonProducts = toCommonProducts(products);
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

    private void addToCart(RequestResponse request) {
        System.out.println("\nStart add to cart");
        Session session = server.getSessionFactory().openSession();
        RequestResponse response = new RequestResponse(ADD_TO_CART);
        response.setField("success", false);
        try {
            session.beginTransaction();
            currentUser = session.get(User.class, currentUser.getId());

            Product product = session.createQuery("SELECT p FROM Product p WHERE p.id = :id", Product.class)
                            .setParameter("id", request.getField(Integer.class, "id"))
                            .getSingleResult();

            if (!currentUser.getCart().contains(product)) {
                currentUser.getCart().add(product);
                response.setField("success", true);
                response.setField("product", toCommonProduct(product));
            }
            session.getTransaction().commit();
            writeResponse(response);
            notifier.notifyClients(this, response);
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

            List<com.shop.common.model.Product> cart = toCommonProducts(currentUser.getCart());
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
            notifier.notifyClients(this, request);
        } catch (Exception ex) {
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
    }

    private void getOrders() {
        System.out.println("\nStart get orders");
        Session session = server.getSessionFactory().openSession();
        try {
            session.beginTransaction();
            currentUser = session.get(User.class, currentUser.getId());

            RequestResponse response = new RequestResponse(GET_ORDERS);
            response.setField("orders", toCommonOrders(currentUser.getOrders()));
            writeResponse(response);

            session.getTransaction().commit();
        } catch (Exception ex) {
            session.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

    private void makeOrder(RequestResponse request) {
        System.out.println("\nStart make order");
        Session session = server.getSessionFactory().openSession();
        try {
            session.beginTransaction();
            currentUser = session.get(User.class, currentUser.getId());

            //продукты которые пользователь хочет включить в заказ
            //key: product id, value: amount
            Map<Integer, Integer> products = request.getField(HashMap.class, "products");

            List<Product> saleProducts = session
                    .createQuery(
                    "SELECT pr FROM Product pr WHERE pr.id IN :list_of_id", Product.class)
                    .setParameter("list_of_id", products.keySet())
                    .getResultList();

            boolean success = true;
            List<String> errorMessages = new ArrayList<>();
            for (Product p : saleProducts) {
                //сколько продукта останется в случае его покупки
                int amount = p.getAmount() - products.get(p.getId());
                if (amount < 0) {
                    success = false;
                    errorMessages.add(String.format("The quantity requested for product: %s (%d) is greater than the actual quantity (%d)"
                            ,p.getName(), products.get(p.getId()), p.getAmount()));
                } else {
                    p.setAmount(amount);
                }
            }

            BigDecimal totalPrice = request.getField(BigDecimal.class, "total_price");
            if (currentUser.getBalance() < totalPrice.intValue()) {
                success = false;
                errorMessages.add("There's not enough money in the account");
            } else {
                currentUser.setBalance(currentUser.getBalance() - totalPrice.intValue());
            }

            RequestResponse response = new RequestResponse();
            if (success) {
                List<com.shop.common.model.Product> updatedProducts = new ArrayList<>();
                List<Product> productsForOrder = new ArrayList<>();
                for (Product p : saleProducts) {
                    //продукт для заказа с количеством указанным пользователем
                    Product product = new Product(p.getName(), p.getDescription(), p.getPrice(), products.get(p.getId()),
                            ProductType.FOR_ORDER, new ArrayList<>(p.getPictures()));
                    updatedProducts.add(toCommonProduct(p));
                    productsForOrder.add(product);
                    session.persist(product);
                }

                Order order = new Order(currentUser, totalPrice, productsForOrder);
                session.persist(order);

                response.setTitle(MAKE_ORDER);
                response.setField("order", toCommonOrder(order));
                response.setField("updated_products", updatedProducts);
                response.setField("balance", currentUser.getBalance());
                session.getTransaction().commit();

            } else {
                response.setTitle(MAKE_ORDER_ERROR);
                response.setField("errors", errorMessages);
                session.getTransaction().rollback();
            }

            writeResponse(response);
            if (response.getTitle() == MAKE_ORDER) {
                notifier.notifyClients(this, response);
            }
        } catch (Exception ex) {
            session.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            session.close();
        }
    }

}
