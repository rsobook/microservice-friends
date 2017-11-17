package si.fri.rsobook.health;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import si.fri.rsobook.metric.FriendsMetrics;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Health
@ApplicationScoped
public class ConfigHealthCheck implements HealthCheck {

    @Inject
    private FriendsMetrics friendsMetrics;

    @Override
    public HealthCheckResponse call() {

        if(!friendsMetrics.isHealthy()) {
            return HealthCheckResponse.named(ConfigHealthCheck.class.getSimpleName()).down().build();
        }

        return HealthCheckResponse.named(ConfigHealthCheck.class.getSimpleName()).up().build();
    }

}
