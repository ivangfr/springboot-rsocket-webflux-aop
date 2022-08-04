package com.ivanfranchin.movieserver.config;

import com.ivanfranchin.movieserver.aspect.EventStreamerAspect;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class ClientsInfoContributor implements InfoContributor {

    private final EventStreamerAspect eventStreamerAspect;

    @Override
    public void contribute(Info.Builder builder) {
        Set<String> clientIds = eventStreamerAspect.getClientIds();
        Map<String, Object> details = new HashMap<>();
        details.put("total", clientIds.size());
        details.put("ids", clientIds);
        builder.withDetail("clients", details);
    }
}
