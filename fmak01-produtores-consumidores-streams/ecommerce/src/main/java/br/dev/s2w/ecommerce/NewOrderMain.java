package br.dev.s2w.ecommerce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class NewOrderMain {
    public static void main(String[] args) {
        try (var producer = new KafkaProducer<String, String>(properties())) {
            var orderTopic = "ECOMMERCE_NEW_ORDER";
            var emailTopic = "ECOMMERCE_SEND_EMAIL";

            var orderKey = "132123,67523,7894589745";
            var orderValue = "New order received successfully!";

            var emailKey = "22134,78412,8903490562";
            var emailValue = "Thank you for your order! We are processing your request.";

            var orderRecord = new ProducerRecord<>(orderTopic, orderKey, orderValue);
            var emailRecord = new ProducerRecord<>(emailTopic, emailKey, emailValue);

            Callback callback = createCallback();

            producer.send(orderRecord, callback);
            producer.send(emailRecord, callback);
        } catch (Exception e) {
            System.err.printf("Execution exception: %s%n", e.getMessage());
        }
    }

    private static Properties properties() {
        var properties = new Properties();

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return properties;
    }

    private static Callback createCallback() {
        return (data, e) -> {
            if (e != null) {
                System.err.printf("An exception occurred: %s%n", e.getMessage());
            }

            System.out.printf("Success! Sending message...\n%s - Topic: %s - Partition: %s - Offset: %s%n",
                    formatTimestamp(data.timestamp()), data.topic(), data.partition(), data.offset());
        };
    }

    private static String formatTimestamp(long timestamp) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(timestamp));
    }
}