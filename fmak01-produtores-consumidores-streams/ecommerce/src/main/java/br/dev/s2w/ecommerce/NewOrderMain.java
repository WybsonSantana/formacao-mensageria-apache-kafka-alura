package br.dev.s2w.ecommerce;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    public static void main(String[] args) {
        try (var producer = new KafkaProducer<String, String>(properties())) {
            var topic = "ECOMMERCE_NEW_ORDER";
            var key = "132123,67523,7894589745";
            var value = "New order received successfully!";

            var record = new ProducerRecord<>(topic, key, value);

            producer.send(record, (data, e) -> {
                if (e != null) {
                    System.err.printf("An exception occurred: %s%n", e.getMessage());
                    return;
                }

                System.out.printf("Success! Sending message...\n%s - Topic: %s - Partition: %s - Offset: %s%n",
                        formatTimestamp(data.timestamp()), data.topic(), data.partition(), data.offset());
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.printf("Thread was interrupted: %s%n", e.getMessage());
        } catch (ExecutionException e) {
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

    private static String formatTimestamp(long timestamp) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(timestamp));
    }
}