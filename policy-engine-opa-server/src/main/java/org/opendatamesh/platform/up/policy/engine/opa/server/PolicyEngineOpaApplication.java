package org.opendatamesh.platform.up.policy.engine.opa.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"org.opendatamesh.platform.up.policy.engine.opa.server"})
public class PolicyEngineOpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolicyEngineOpaApplication.class, args);
    }

}