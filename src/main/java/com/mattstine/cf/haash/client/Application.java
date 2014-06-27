package com.mattstine.cf.haash.client;

import com.mattstine.cf.haash.client.connector.cloudfoundry.HaashServiceInfo;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
public class Application {

    @Bean
    Cloud cloud() {
        return new CloudFactory().getCloud();
    }

    @Bean
    HaashServiceInfo haashServiceInfo() {
        List<ServiceInfo> serviceInfos = cloud().getServiceInfos();
        for (ServiceInfo serviceInfo : serviceInfos) {
            if (serviceInfo instanceof HaashServiceInfo) {
                return (HaashServiceInfo) serviceInfo;
            }
        }
        throw new RuntimeException("Unable to find bound HaaSh instance!");
    }

    @Bean
    RestTemplate restTemplate() {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        BasicCredentialsProvider credentialsProvider =  new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(haashServiceInfo().getUsername(), haashServiceInfo().getPassword()));
        httpClient.setCredentialsProvider(credentialsProvider);
        ClientHttpRequestFactory rf = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(rf);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RequestMapping("/HaaSh/info")
    public HaashServiceInfo info() {
        return haashServiceInfo();
    }

    @RequestMapping(value = "/HaaSh/{key}", method = RequestMethod.PUT)
    public ResponseEntity<String> put(@PathVariable("key") String key,
                                      @RequestBody String value) {
        restTemplate().put(haashServiceInfo().getUri()+"/{key}", value, key);
        return new ResponseEntity<>("{}", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/HaaSh/{key}", method = RequestMethod.GET)
    public ResponseEntity<String> put(@PathVariable("key") String key) {
        String response = restTemplate().getForObject(haashServiceInfo().getUri() + "/{key}", String.class, key);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
