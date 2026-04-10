package investiment_intelligence_platform.notification_service.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "notification")
data class AppProperties(
	val deduplication: DeduplicationProperties = DeduplicationProperties(),
	val userService: ServiceEndpointProperties,
	val chatService: ServiceEndpointProperties,
	val http: HttpRetryProperties = HttpRetryProperties(),
) {
	data class DeduplicationProperties(
		val ttlMinutes: Long = 10,
	)

	data class ServiceEndpointProperties(
		val baseUrl: String = "http://localhost:8081",
		val timeoutSeconds: Long = 10,
	)

	data class HttpRetryProperties(
		val maxRetries: Int = 3,
		val initialBackoffMs: Long = 200,
		val maxBackoffMs: Long = 2000,
	)
}
