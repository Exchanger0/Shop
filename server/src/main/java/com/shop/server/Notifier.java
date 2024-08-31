package com.shop.server;

import com.shop.common.RequestResponse;

import java.util.HashSet;
import java.util.Set;

public class Notifier {
    private final Set<ClientHandler> clients = new HashSet<>();

    public void addClient(ClientHandler client) {
        synchronized (clients) {
            clients.add(client);
        }
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    public void notifyClients(ClientHandler from, RequestResponse response) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (!client.equals(from)) {
                    client.writeResponse(response);
                }
            }
        }
    }
}
