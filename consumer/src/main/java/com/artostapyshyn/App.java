package com.artostapyshyn;

import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;

public class App {
    private final static String URL = "CONNECTION_URL";
    private final static String QUEUE_NAME = "QUEUE_NAME";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(URL);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received: " + message);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };

        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {});

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (channel.isOpen()) {
                    channel.close();
                }
                if (connection.isOpen()) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
}
