package org.opendatamesh.platform.up.policy.engine.opa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
/*@ComponentScan(basePackages = "org.opendatamesh.platform.up.policy")
@EntityScan(basePackages = "org.opendatamesh.platform.up.policy")*/
public class PolicyEngineOpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolicyEngineOpaApplication.class, args);
	}

}
