@Component
public class SocialMediaHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Implement health check logic
        return Health.up()
                    .withDetail("socialMedia", "OK")
                    .build();
    }
}