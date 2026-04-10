package investiment_intelligence_platform.notification_service.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

enum class NormalizedEventType {
	PRICE_DROP,
	PRICE_UP,
	SIGNAL,
}

data class NormalizedEvent(
	val symbol: String,
	val eventType: NormalizedEventType,
	val value: Double?,
	val prediction: String?,
	val confidence: Double?,
	val timestamp: Instant,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MlPredictionPayload(
	val symbol: String,
	val prediction: String,
	val confidence: Double,
	val timestamp: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MarketDataPayload(
	val symbol: String,
	val price: Double? = null,
	@JsonProperty("changePercent") val changePercent: Double,
	val timestamp: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserAlertDto(
	val userId: String,
	val phoneNumber: String,
	val conditionType: String,
	val threshold: Double,
)

data class SendMessageRequest(
	val to: String,
	val message: String,
)
