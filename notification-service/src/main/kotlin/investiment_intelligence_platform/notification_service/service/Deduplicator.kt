package investiment_intelligence_platform.notification_service.service

import investiment_intelligence_platform.notification_service.config.AppProperties
import investiment_intelligence_platform.notification_service.model.NormalizedEvent
import investiment_intelligence_platform.notification_service.model.UserAlertDto
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class Deduplicator(
	private val redis: StringRedisTemplate,
	private val appProperties: AppProperties,
) {

	fun tryAcquireSendSlot(alert: UserAlertDto, event: NormalizedEvent): Boolean {
		val key = buildKey(alert, event)
		val ttl = Duration.ofMinutes(appProperties.deduplication.ttlMinutes)
		val acquired = redis.opsForValue().setIfAbsent(key, "1", ttl) ?: false
		return acquired
	}

	private fun buildKey(alert: UserAlertDto, event: NormalizedEvent): String =
		"alert:dedupe:${alert.userId}:${event.symbol}:${alert.conditionType.uppercase()}"
}
