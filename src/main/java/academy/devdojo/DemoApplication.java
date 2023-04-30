package academy.devdojo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@SpringBootApplication
@ComponentScan(basePackages = {"test.outside","academy.devdojo"})
public class DemoApplication {

	public static void main(String[] args) {
		var applicationContext = SpringApplication.run(DemoApplication.class, args);
		Arrays.stream(applicationContext.getBeanDefinitionNames())
				.forEach(System.out::println);
	}

}
