package investiment_intelligence_platform.notification_service.service

import investiment_intelligence_platform.notification_service.model.NormalizedEvent
import investiment_intelligence_platform.notification_service.model.NormalizedEventType
import investiment_intelligence_platform.notification_service.model.UserAlertDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class AlertEvaluatorTest {

	private val evaluator = AlertEvaluator()

	@Test
	fun `PRICE_DROP triggers when move exceeds threshold`() {
		val alert = UserAlertDto("1", "5511", "PRICE_DROP", 5.0)
		val event = NormalizedEvent(
			"PETR4",
			NormalizedEventType.PRICE_DROP,
			-5.2,
			null,
			null,
			Instant.now(),
		)
		assertThat(evaluator.shouldTrigger(alert, event)).isTrue()
	}

	@Test
	fun `PRICE_UP triggers`() {
		val alert = UserAlertDto("1", "5511", "PRICE_UP", 3.0)
		val event = NormalizedEvent(
			"VALE3",
			NormalizedEventType.PRICE_UP,
			4.0,
			null,
			null,
			Instant.now(),
		)
		assertThat(evaluator.shouldTrigger(alert, event)).isTrue()
	}

	@Test
	fun `SIGNAL triggers for UP and confidence`() {
		val alert = UserAlertDto("1", "5511", "SIGNAL", 0.8)
		val event = NormalizedEvent(
			"PETR4",
			NormalizedEventType.SIGNAL,
			null,
			"UP",
			0.85,
			Instant.now(),
		)
		assertThat(evaluator.shouldTrigger(alert, event)).isTrue()
	}
}
