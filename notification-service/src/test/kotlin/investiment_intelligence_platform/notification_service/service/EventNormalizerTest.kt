package investiment_intelligence_platform.notification_service.service

import investiment_intelligence_platform.notification_service.model.MlPredictionPayload
import investiment_intelligence_platform.notification_service.model.MarketDataPayload
import investiment_intelligence_platform.notification_service.model.NormalizedEventType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EventNormalizerTest {

	private val normalizer = EventNormalizer()

	@Test
	fun `fromMlPrediction maps to SIGNAL`() {
		val e = normalizer.fromMlPrediction(
			MlPredictionPayload("petr4", "UP", 0.85, "2026-04-01T18:30:00Z"),
		)
		assertThat(e.symbol).isEqualTo("PETR4")
		assertThat(e.eventType).isEqualTo(NormalizedEventType.SIGNAL)
		assertThat(e.prediction).isEqualTo("UP")
		assertThat(e.confidence).isEqualTo(0.85)
	}

	@Test
	fun `fromMarketData maps drop`() {
		val e = normalizer.fromMarketData(
			MarketDataPayload("PETR4", 30.10, -5.2, "2026-04-01T18:30:00Z"),
		)!!
		assertThat(e.eventType).isEqualTo(NormalizedEventType.PRICE_DROP)
		assertThat(e.value).isEqualTo(-5.2)
	}

	@Test
	fun `fromMarketData returns null for zero change`() {
		assertThat(
			normalizer.fromMarketData(MarketDataPayload("PETR4", 30.0, 0.0, null)),
		).isNull()
	}
}
