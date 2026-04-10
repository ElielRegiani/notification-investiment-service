package investiment_intelligence_platform.notification_service.service

import investiment_intelligence_platform.notification_service.integration.UserServiceClient
import investiment_intelligence_platform.notification_service.model.NormalizedEvent
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AlertProcessingService(
	private val userServiceClient: UserServiceClient,
	private val alertEvaluator: AlertEvaluator,
	private val deduplicator: Deduplicator,
	private val notificationDispatcher: NotificationDispatcher,
	private val meterRegistry: MeterRegistry,
) {
	private val log = LoggerFactory.getLogger(javaClass)

	fun process(event: NormalizedEvent) {
		val alerts = try {
			userServiceClient.fetchAlertsForSymbol(event.symbol)
		} catch (e: Exception) {
			log.error("Failed to fetch alerts for symbol={}", event.symbol, e)
			meterRegistry.counter("notifications.user_service.failures").increment()
			throw e
		}

		meterRegistry.counter("notifications.events.processed", "type", event.eventType.name).increment()

		for (alert in alerts) {
			val triggered = alertEvaluator.shouldTrigger(alert, event)
			log.info(
				"event={} type={} user={} alert={} triggered={}",
				event.symbol,
				event.eventType,
				alert.userId,
				alert.conditionType,
				triggered,
			)
			if (!triggered) continue

			if (!deduplicator.tryAcquireSendSlot(alert, event)) {
				meterRegistry.counter("notifications.alerts.deduplicated", "symbol", event.symbol).increment()
				log.debug("Dedup skip user={} symbol={} alert={}", alert.userId, event.symbol, alert.conditionType)
				continue
			}

			try {
				notificationDispatcher.dispatch(alert, event)
				meterRegistry.counter("notifications.alerts.sent", "symbol", event.symbol).increment()
			} catch (e: Exception) {
				log.error("Dispatch failed user={} symbol={}", alert.userId, event.symbol, e)
				meterRegistry.counter("notifications.dispatch.failures").increment()
				throw e
			}
		}
	}
}
