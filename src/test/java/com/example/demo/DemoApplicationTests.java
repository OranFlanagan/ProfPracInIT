package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootTest
class DemoApplicationTests {

	@TestConfiguration
	static class TestMailConfiguration {
		@Bean
		JavaMailSender javaMailSender() {
			return new JavaMailSenderImpl();
		}
	}

	@Test
	void contextLoads() {
	}

}
