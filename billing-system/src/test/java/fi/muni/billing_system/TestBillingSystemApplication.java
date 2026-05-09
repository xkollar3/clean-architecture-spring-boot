package fi.muni.billing_system;

import org.springframework.boot.SpringApplication;

public class TestBillingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.from(BillingSystemApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
