package investiment_intelligence_platform.notification_service.service

import investiment_intelligence_platform.notification_service.model.NormalizedEvent
import investiment_intelligence_platform.notification_service.model.NormalizedEventType
import investiment_intelligence_platform.notification_service.model.UserAlertDto
import org.springframework.stereotype.Service

@Service
class AlertEvaluator {

	fun shouldTrigger(alert: UserAlertDto, event: NormalizedEvent): Boolean {
		val condition = alert.conditionType.uppercase()
		return when (condition) {
			"PRICE_DROP" -> matchesPriceDrop(alert, event)
			"PRICE_UP" -> matchesPriceUp(alert, event)
			"SIGNAL" -> matchesSignal(alert, event)
			else -> false
		}
	}

	private fun matchesPriceDrop(alert: UserAlertDto, event: NormalizedEvent): Boolean {
		if (event.eventType != NormalizedEventType.PRICE_DROP) return false
		val v = event.value ?: return false
		return v <= -alert.threshold
	}

	private fun matchesPriceUp(alert: UserAlertDto, event: NormalizedEvent): Boolean {
		if (event.eventType != NormalizedEventType.PRICE_UP) return false
		val v = event.value ?: return false
		return v >= alert.threshold
	}

	private fun matchesSignal(alert: UserAlertDto, event: NormalizedEvent): Boolean {
		if (event.eventType != NormalizedEventType.SIGNAL) return false
		val pred = event.prediction ?: return false
		val conf = event.confidence ?: return false
		return pred == "UP" && conf >= alert.threshold
	}
}
