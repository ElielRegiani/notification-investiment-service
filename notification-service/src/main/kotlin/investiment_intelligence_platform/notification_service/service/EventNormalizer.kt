package investiment_intelligence_platform.notification_service.service

import investiment_intelligence_platform.notification_service.model.MlPredictionPayload
import investiment_intelligence_platform.notification_service.model.MarketDataPayload
import investiment_intelligence_platform.notification_service.model.NormalizedEvent
import investiment_intelligence_platform.notification_service.model.NormalizedEventType
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.format.DateTimeParseException

@Service
class EventNormalizer {

	fun fromMlPrediction(payload: MlPredictionPayload): NormalizedEvent =
		NormalizedEvent(
			symbol = payload.symbol.uppercase(),
			eventType = NormalizedEventType.SIGNAL,
			value = null,
			prediction = payload.prediction.uppercase(),
			confidence = payload.confidence,
			timestamp = parseTimestamp(payload.timestamp),
		)

	fun fromMarketData(payload: MarketDataPayload): NormalizedEvent? {
		val change = payload.changePercent
		if (change == 0.0) return null
		val type = when {
			change < 0 -> NormalizedEventType.PRICE_DROP
			else -> NormalizedEventType.PRICE_UP
		}
		val ts = payload.timestamp?.let { parseTimestamp(it) } ?: Instant.now()
		return NormalizedEvent(
			symbol = payload.symbol.uppercase(),
			eventType = type,
			value = change,
			prediction = null,
			confidence = null,
			timestamp = ts,
		)
	}

	private fun parseTimestamp(raw: String): Instant =
		try {
			Instant.parse(raw)
		} catch (_: DateTimeParseException) {
			Instant.now()
		}
}
