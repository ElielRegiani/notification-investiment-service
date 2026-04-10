package investiment_intelligence_platform.notification_service.integration

import investiment_intelligence_platform.notification_service.config.AppProperties
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException

@Component
class HttpRetry(
	private val appProperties: AppProperties,
) {

	fun <T> execute(block: () -> T): T {
		var delay = appProperties.http.initialBackoffMs
		var last: Exception? = null
		repeat(appProperties.http.maxRetries) { attempt ->
			try {
				return block()
			} catch (e: RestClientException) {
				last = e
				if (attempt == appProperties.http.maxRetries - 1) throw e
				Thread.sleep(delay)
				delay = minOf(delay * 2, appProperties.http.maxBackoffMs)
			}
		}
		throw last ?: IllegalStateException("retry exhausted")
	}
}
