package com.shop.server;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
//todo: commit, сделать синхронизацию между аккаунтами
public class Server {

    private SessionFactory sessionFactory;

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
}
