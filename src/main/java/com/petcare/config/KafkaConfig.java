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
                .partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic appointmentBookedTopic() {
        return TopicBuilder.name("appointment.booked")
                .partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic adoptionRequestedTopic() {
        return TopicBuilder.name("adoption.requested")
                .partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic lostPetReportedTopic() {
        return TopicBuilder.name("lostpet.reported")
                .partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic emergencyTriggeredTopic() {
        return TopicBuilder.name("emergency.triggered")
                .partitions(3).replicas(1).build();
    }
}
