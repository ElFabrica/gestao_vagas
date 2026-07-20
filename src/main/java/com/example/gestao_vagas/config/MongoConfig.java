package com.example.gestao_vagas.config;

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mongodb.MongoClientSettings.builder;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Value("${spring.data.mongodb.host:localhost}")
    private String host;

    @Value("${spring.data.mongodb.port:27017}")
    private Integer port;

    @Value("${spring.data.mongodb.database:gestao_vagas_logs}")
    private String database;

    @Value("${spring.data.mongodb.username:mongo}")
    private String username;

    @Value("${spring.data.mongodb.password:mongo}")
    private String password;

    @Value("${spring.data.mongodb.authentication-database:admin}")
    private String authenticationDatabase;

    @Bean
    public MongoClient mongoClient() {
        var credential = MongoCredential.createCredential(
                username,
                authenticationDatabase,
                password.toCharArray());

        var settings = builder()
                .credential(credential)
                .applyToClusterSettings(cluster -> cluster
                        .hosts(List.of(new ServerAddress(host, port)))
                        .serverSelectionTimeout(1000, TimeUnit.MILLISECONDS))
                .applyToSocketSettings(socket -> socket
                        .connectTimeout(1000, TimeUnit.MILLISECONDS))
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, database);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}
