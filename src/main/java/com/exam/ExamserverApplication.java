package com.exam;

import org.springframework.web.filter.CorsFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

		@SpringBootApplication
		public class ExamserverApplication {

			private static final Logger logger = LoggerFactory.getLogger(ExamserverApplication.class);

			public static void main(String[] args) {
				SpringApplication.run(ExamserverApplication.class, args);
				logger.info("ExamserverApplication started successfully!");
			}

			@Bean
			public CorsFilter corsFilter() {
				CorsConfiguration config = new CorsConfiguration();
				config.addAllowedOrigin("http://localhost:3000");// React App URL
				config.addAllowedOrigin("https://17st.github.io/");
				config.addAllowedHeader("*"); // Allow all headers
				config.addAllowedMethod("*"); // Allow all HTTP methods (GET, POST, etc.)
				config.setAllowCredentials(true); // If you use cookies or authentication

				UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
				source.registerCorsConfiguration("/**", config);
				logger.debug("CORS filter configured successfully!");
				return new CorsFilter(source);
			}
		}
