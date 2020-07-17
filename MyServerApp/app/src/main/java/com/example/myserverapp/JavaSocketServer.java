package com.example.myserverapp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class JavaSocketServer {
    public static void main(String[] args) {
        try {
            int portNumber = 7080;

            System.out.println("starting java socket server...");
            ServerSocket aServerSocket = new ServerSocket(portNumber);
            System.out.println("Listening at port " + portNumber + " ...");

            while(true) {
                Socket socket = aServerSocket.accept();
                InetAddress clientHost = socket.getLocalAddress();
                int clientPort = socket.getPort();
                System.out.println("A client conneted. host: " + clientHost + ", port: " + clientPort);
                ObjectInputStream inputstream = new ObjectInputStream(socket.getInputStream());
                Object object = inputstream.readObject();
                System.out.println("input: " + object);

                ObjectOutputStream outputstream = new ObjectOutputStream(socket.getOutputStream());
                outputstream.writeObject(object + " from Server.");
                outputstream.flush();
                socket.close();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}

