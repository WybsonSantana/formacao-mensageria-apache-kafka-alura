package br.dev.s2w.ecommerce.service;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Properties;
import java.util.regex.Pattern;

public class LoggingService {
    public static void main(String[] args) {
        try (var consumer = new KafkaConsumer<String, String>(properties())) {
            var loggingTopic = "ECOMMERCE.*";
            boolean listening = true;

            consumer.subscribe(Pattern.compile(loggingTopic));

            while (listening) {
                var records = consumer.poll(Duration.ofMillis(100));

                if (!records.isEmpty()) {
                    System.out.printf("%d record(s) found!%n", records.count());

                    for (var record : records) {
                        listening = processRecord(record, listening);
                    }
                }
            }
        }
    }

    private static Properties properties() {
        var properties = new Properties();

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, LoggingService.class.getSimpleName());

        return properties;
    }

    private static boolean processRecord(ConsumerRecord<String, String> record, boolean listening) {
        System.out.println("---------------------");
        System.out.printf("Logging content from topic: %s%n", record.topic());
        System.out.printf("Key: %s%n", record.key());
        System.out.printf("Value: %s%n", record.value());
        System.out.printf("Partition: %d%n", record.partition());
        System.out.printf("Offset: %d%n", record.offset());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.printf("Thread was interrupted: %s%n", e.getMessage());
            listening = false;
        }

        System.out.println("Content logged successfully!\n");
        return listening;
    }
}