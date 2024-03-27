package org.opendatamesh.platform.up.policy.engine.opa.server.opaclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpaClientConfiguration {

    @Value("${timeout.seconds}")
    private Long timeout;

    @Value("${opa.url.policies}")
    private String policiesUrl;

    @Value("${opa.url.data}")
    private String dataUrl;

    @Bean
    public OpaClient opaClient()
    {
        return new OpaClient(policiesUrl, dataUrl, timeout);
    }

}
