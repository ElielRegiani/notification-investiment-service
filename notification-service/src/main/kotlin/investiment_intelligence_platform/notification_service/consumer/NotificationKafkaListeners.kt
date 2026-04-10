package investiment_intelligence_platform.notification_service.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import investiment_intelligence_platform.notification_service.model.MlPredictionPayload
import investiment_intelligence_platform.notification_service.model.MarketDataPayload
import investiment_intelligence_platform.notification_service.service.AlertProcessingService
import investiment_intelligence_platform.notification_service.service.EventNormalizer
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class NotificationKafkaListeners(
	private val objectMapper: ObjectMapper,
	private val eventNormalizer: EventNormalizer,
	private val alertProcessingService: AlertProcessingService,
	private val meterRegistry: MeterRegistry,
) {
	private val log = LoggerFactory.getLogger(javaClass)

	@KafkaListener(
		topics = ["ml-prediction-generated"],
		groupId = "\${spring.kafka.consumer.group-id:notification-service}",
	)
	fun onMlPrediction(payload: String, ack: Acknowledgment) {
		try {
			val p = objectMapper.readValue(payload, MlPredictionPayload::class.java)
			val event = eventNormalizer.fromMlPrediction(p)
			alertProcessingService.process(event)
		} catch (e: Exception) {
			log.error("Failed to process ml-prediction-generated event", e)
			meterRegistry.counter("notifications.kafka.processing.failures", "topic", "ml-prediction-generated").increment()
		} finally {
			ack.acknowledge()
		}
	}

	@KafkaListener(
		topics = ["market-data-updated"],
		groupId = "\${spring.kafka.consumer.group-id:notification-service}",
	)
	fun onMarketData(payload: String, ack: Acknowledgment) {
		try {
			val p = objectMapper.readValue(payload, MarketDataPayload::class.java)
			val event = eventNormalizer.fromMarketData(p)
			if (event != null) {
				alertProcessingService.process(event)
			}
		} catch (e: Exception) {
			log.error("Failed to process market-data-updated event", e)
			meterRegistry.counter("notifications.kafka.processing.failures", "topic", "market-data-updated").increment()
		} finally {
			ack.acknowledge()
		}
	}
}
