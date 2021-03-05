package hu.lsm.smdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.statemachine.config.EnableStateMachine;

@SpringBootApplication(exclude = {
		SecurityAutoConfiguration.class,
		ManagementWebSecurityAutoConfiguration.class
}, scanBasePackages = "hu.lsm.smdemo")
@EnableAsync
@EnableStateMachine
@EnableScheduling
public class SmDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmDemoApplication.class, args);
	}

}
