package com.shop.server;

import com.shop.server.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

//todo: сделать синхронизацию между аккаунтами
public class Server {

    private SessionFactory sessionFactory;
    private final HashMap<User, Notifier> userNotifier = new HashMap<>();

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    private void start(){
        try {
            System.out.println(this.getClass().getResource("/hibernate.cfg.xml"));
            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().configure(
                    this.getClass().getResource("/hibernate.cfg.xml")
            ).build();
            Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();
            sessionFactory = metadata.getSessionFactoryBuilder().build();

            ServerSocket serverSocket = new ServerSocket(9087);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(this, clientSocket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public Notifier registerClientHandler(User user, ClientHandler clientHandler) {
        synchronized (userNotifier) {
            Notifier n;
            if (userNotifier.containsKey(user)) {
                n = userNotifier.get(user);
            } else {
                n = new Notifier();
                userNotifier.put(user, n);
            }
            n.addClient(clientHandler);
            return n;
        }
    }
}
