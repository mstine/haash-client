package com.mattstine.cf.haash.client.connector.cloudfoundry;

import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;

import java.util.Map;

public class HaashServiceInfoCreator extends CloudFoundryServiceInfoCreator<HaashServiceInfo> {
    public HaashServiceInfoCreator() {
        super(new Tags("HaaSh"));
    }

    @Override
    public HaashServiceInfo createServiceInfo(Map<String, Object> serviceData) {
        Map<String, Object> credentials = (Map<String, Object>) serviceData.get("credentials");

        String id = (String) serviceData.get("name");
        String uri = (String) credentials.get("uri");
        String username = (String) credentials.get("username");
        String password = (String) credentials.get("password");

        return new HaashServiceInfo(id, uri, username, password);
    }

    @Override
    public boolean accept(Map<String, Object> serviceData) {
        Map<String, Object> credentials = (Map<String, Object>) serviceData.get("credentials");
        String uri = (String) credentials.get("uri");
        String username = (String) credentials.get("username");
        String password = (String) credentials.get("password");
        return username != null &&
                password != null &&
                uri != null;
    }
}
