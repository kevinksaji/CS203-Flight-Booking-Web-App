package com.G2T5203.wingit;

import com.G2T5203.wingit.adminUtils.DatabaseInitializer;
import com.G2T5203.wingit.user.WingitUser;
import com.G2T5203.wingit.user.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;

@SpringBootApplication
public class WingitApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(WingitApplication.class, args);

		UserRepository userRepo = context.getBean(UserRepository.class);
		BCryptPasswordEncoder encoder = context.getBean(BCryptPasswordEncoder.class);
		if (userRepo.findAll().isEmpty()) {
			// Create the default admin user.
			userRepo.save(new WingitUser(
					"admin",
					encoder.encode("P@SSw0rd"),
					"ROLE_ADMIN",
					"Admin",
					"Admin",
					LocalDate.parse("2000-01-01"),
					"admin@wingit.world",
					"+65 8888 9999",
					"Master"));
		}

		try {
			org.springframework.core.io.Resource resource = new ClassPathResource("application.properties");
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			String activeProfile = props.getProperty("spring.profiles.active");
			boolean isProduction = activeProfile.equals("prod");
			DatabaseInitializer.initNonAdminUsersData(context);
			DatabaseInitializer.initPlanesAndRoutesData(context, isProduction);
		} catch (IOException e) {
			System.out.println("ERROR: " + e.getLocalizedMessage());
		}
	}

	// create a CORS mapping
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedMethods("*")
						.allowedOrigins("*");
			}
		};
	}

}
