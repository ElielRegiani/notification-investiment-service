package investiment_intelligence_platform.notification_service.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.time.Duration

@Configuration
@EnableConfigurationProperties(AppProperties::class)
class AppConfig {

	@Bean
	fun userServiceRestClient(appProperties: AppProperties): RestClient {
		val factory = SimpleClientHttpRequestFactory()
		val t = Duration.ofSeconds(appProperties.userService.timeoutSeconds)
		factory.setConnectTimeout(t)
		factory.setReadTimeout(t)
		return RestClient.builder()
			.baseUrl(appProperties.userService.baseUrl.trimEnd('/'))
			.requestFactory(factory)
			.build()
	}

	@Bean
	fun chatServiceRestClient(appProperties: AppProperties): RestClient {
		val factory = SimpleClientHttpRequestFactory()
		val t = Duration.ofSeconds(appProperties.chatService.timeoutSeconds)
		factory.setConnectTimeout(t)
		factory.setReadTimeout(t)
		return RestClient.builder()
			.baseUrl(appProperties.chatService.baseUrl.trimEnd('/'))
			.requestFactory(factory)
			.build()
	}
}
