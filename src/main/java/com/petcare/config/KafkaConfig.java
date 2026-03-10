package com.petcare.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    // ── Topic definitions ───────────────────────────────────

    @Bean
    public NewTopic petCreatedTopic() {
        return TopicBuilder.name("pet.created")
                .partitions(3).replicas(3).build();
    }

    @Bean
    public NewTopic appointmentBookedTopic() {
        return TopicBuilder.name("appointment.booked")
                .partitions(3).replicas(3).build();
    }

    @Bean
    public NewTopic appointmentCancelledTopic() {
        return TopicBuilder.name("appointment.cancelled")
                .partitions(3).replicas(3).build();
    }

    @Bean
    public NewTopic petLostTopic() {
        return TopicBuilder.name("pet.lost")
                .partitions(3).replicas(3).build();
    }

    @Bean
    public NewTopic petFoundTopic() {
        return TopicBuilder.name("pet.found")
                .partitions(3).replicas(3).build();
    }
}
