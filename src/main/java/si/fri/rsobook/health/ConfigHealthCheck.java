package si.fri.rsobook.health;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import si.fri.rsobook.config.FriendsConfigProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Health
@ApplicationScoped
public class ConfigHealthCheck implements HealthCheck {

    @Inject
    private FriendsConfigProperties friendsConfigProperties;

    @Override
    public HealthCheckResponse call() {

        if(friendsConfigProperties.getUserApiHostDiscovery() == null) {
            return HealthCheckResponse.named(ConfigHealthCheck.class.getSimpleName()).down().build();
        }

        return HealthCheckResponse.named(ConfigHealthCheck.class.getSimpleName()).up().build();
    }

}
