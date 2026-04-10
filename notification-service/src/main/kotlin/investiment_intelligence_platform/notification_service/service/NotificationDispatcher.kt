package investiment_intelligence_platform.notification_service.service

import investiment_intelligence_platform.notification_service.integration.ChatServiceClient
import investiment_intelligence_platform.notification_service.model.NormalizedEvent
import investiment_intelligence_platform.notification_service.model.UserAlertDto
import org.springframework.stereotype.Service
import kotlin.math.abs

@Service
class NotificationDispatcher(
	private val chatServiceClient: ChatServiceClient,
) {

	fun dispatch(alert: UserAlertDto, event: NormalizedEvent) {
		val message = buildMessage(alert, event)
		chatServiceClient.sendMessage(alert.phoneNumber, message)
	}

	private fun buildMessage(alert: UserAlertDto, event: NormalizedEvent): String {
		val symbol = event.symbol
		return when (alert.conditionType.uppercase()) {
			"PRICE_DROP" -> {
				val pct = event.value ?: 0.0
				"⚠️ $symbol caiu ${"%.1f".format(abs(pct))}%. Seu alerta foi atingido."
			}
			"PRICE_UP" -> {
				val pct = event.value ?: 0.0
				"⚠️ $symbol subiu ${"%.1f".format(pct)}%. Seu alerta foi atingido."
			}
			"SIGNAL" -> {
				val conf = event.confidence ?: 0.0
				"⚠️ Sinal forte em $symbol (UP, confiança ${"%.0f".format(conf * 100)}%). Seu alerta foi atingido."
			}
			else -> "⚠️ Alerta atingido para $symbol."
		}
	}
}
