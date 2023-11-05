package com.artostapyshyn;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class App {

    private final static String QUEUE_NAME = "QUEUE_NAME";
    private final static String EXCHANGE_NAME = "EXCHANGE_NAME";
    private final static String EXCHANGE_TYPE = "EXCHANGE_TYPE";
    private final static String ROUTING_KEY = "ROUTING_KEY";
    private final static String URL = "URL";

    public static void main(String[] args) throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(URL);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

            String[] messages = {
                    "Your order has been received",
                    "The product is back in stock"
            };

            for (String message : messages) {
                channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println("Sent: " + message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}