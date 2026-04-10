package investiment_intelligence_platform.notification_service.service

import investiment_intelligence_platform.notification_service.config.AppProperties
import investiment_intelligence_platform.notification_service.model.NormalizedEvent
import investiment_intelligence_platform.notification_service.model.NormalizedEventType
import investiment_intelligence_platform.notification_service.model.UserAlertDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration
import java.time.Instant

class DeduplicatorTest {

	private val redis: StringRedisTemplate = mock()
	private val ops: ValueOperations<String, String> = mock()
	private lateinit var deduplicator: Deduplicator

	@BeforeEach
	fun setup() {
		whenever(redis.opsForValue()).thenReturn(ops)
		deduplicator = Deduplicator(
			redis,
			AppProperties(
				deduplication = AppProperties.DeduplicationProperties(ttlMinutes = 10),
				userService = AppProperties.ServiceEndpointProperties(),
				chatService = AppProperties.ServiceEndpointProperties(),
			),
		)
	}

	@Test
	fun `tryAcquireSendSlot returns true when redis sets key`() {
		whenever(ops.setIfAbsent(any(), any(), any<Duration>())).thenReturn(true)
		val alert = UserAlertDto("u1", "5511", "PRICE_DROP", 5.0)
		val event = NormalizedEvent("PETR4", NormalizedEventType.PRICE_DROP, -6.0, null, null, Instant.now())
		assertThat(deduplicator.tryAcquireSendSlot(alert, event)).isTrue()
	}

	@Test
	fun `tryAcquireSendSlot returns false when key exists`() {
		whenever(ops.setIfAbsent(any(), any(), any<Duration>())).thenReturn(false)
		val alert = UserAlertDto("u1", "5511", "PRICE_DROP", 5.0)
		val event = NormalizedEvent("PETR4", NormalizedEventType.PRICE_DROP, -6.0, null, null, Instant.now())
		assertThat(deduplicator.tryAcquireSendSlot(alert, event)).isFalse()
	}
}
