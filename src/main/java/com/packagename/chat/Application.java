package com.packagename.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

	}

	// a unicast where all the messages will be pushed.
	@Bean
	UnicastProcessor<ChatMessage> publisher() {
		return UnicastProcessor.create();
	}

	// If someone joined the chat later, he can see the last 30 messages.
	// autoConnect() defers the setup until somebody actually listens on this.
	@Bean
	Flux<ChatMessage> messages(UnicastProcessor<ChatMessage> publisher) {
		return publisher.replay(30).autoConnect();
	}
}
