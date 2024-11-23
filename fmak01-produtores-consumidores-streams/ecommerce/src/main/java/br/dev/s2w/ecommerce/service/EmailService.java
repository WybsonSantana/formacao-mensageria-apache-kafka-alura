package br.dev.s2w.ecommerce.service;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class EmailService {
    public static void main(String[] args) {
        try (var consumer = new KafkaConsumer<String, String>(properties())) {
            var emailTopic = "ECOMMERCE_SEND_EMAIL";
            boolean listening = true;

            consumer.subscribe(Collections.singletonList(emailTopic));

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
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, EmailService.class.getSimpleName());

        return properties;
    }

    private static boolean processRecord(ConsumerRecord<String, String> record, boolean listening) {
        System.out.println("---------------------");
        System.out.println("Sending e-mail...");
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

        System.out.println("E-mail sent successfully!\n");
        return listening;
    }
}