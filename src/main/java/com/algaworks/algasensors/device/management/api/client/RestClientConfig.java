package com.algaworks.algasensors.device.management.api.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    @Bean
    public SensorMonitoringClient sensorMonitoringClient(RestClientFactory factory){
        RestClient restClient = factory.temperatureMonitoringRestClient();
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        return proxyFactory.createClient(SensorMonitoringClient.class);
    }

    @Bean
    public ViaCepClient viaCepClient(RestClientFactory factory){
        RestClient restClient = factory.viaCepRestClient();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();
        return proxyFactory.createClient(ViaCepClient.class);
    }
}
