package rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jdbc.DatabaseAgent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class MQAgent {

    public static Connection connectRabbitMQ() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        factory.setHost("localhost");
        factory.setPort(5672);
        Connection connection;
        try {
            connection = factory.newConnection();
            return connection;
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            System.out.println("[ERROR] Failed connecting to rabbitMQ");
            return null;
        }
    }
    public static void sendData(Connection connection, String queueName, Map<String, String> map) {
        try {
            Channel channel = connection.createChannel();
            {
                channel.basicQos(0, 1, false);
                channel.queueDeclare(queueName, false, false, false, null);
                channel.basicPublish("", queueName, null, map.toString().getBytes(StandardCharsets.UTF_8));
                System.out.println(" [" + queueName + "] Sent '" + map + "'");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void receiveData(Connection connection, String queueName) {
        try {
            Channel channel = connection.createChannel();
            channel.basicQos(0, 1, false);
            channel.queueDeclare(queueName, false, false, false, null);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [INFO] q:[" + queueName+ "] Received '" + message + "'");
                DatabaseAgent da = new DatabaseAgent();
                message = message.replaceAll(" ", "");
                message = message.substring(1, message.length()-1);
                Map<String, String> map = Arrays.stream(message.split(","))
                        .map(s -> s.split("="))
                        .collect(Collectors.toMap(s -> s[0], s -> s[1]));
                switch (queueName) {
                    case ("ow") -> da.insertOpenWeatherData(map);
                    case ("owpoll") -> da.insertOpenWeatherPollutionDate(map);

                }
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendData(Connection c, String owpoll, Object components, String id) {
        try {
            Channel channel = c.createChannel();
            {
                channel.basicQos(0, 1, false);
                channel.queueDeclare(owpoll, false, false, false, null);
                channel.basicPublish("", owpoll, null, components.toString().getBytes(StandardCharsets.UTF_8));
                System.out.println(" [INFO] q:[" + owpoll + "] Sent '" + components + "'");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
