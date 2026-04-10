package investiment_intelligence_platform.notification_service.integration

import investiment_intelligence_platform.notification_service.model.SendMessageRequest
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class ChatServiceClient(
	@Qualifier("chatServiceRestClient") private val restClient: RestClient,
	private val httpRetry: HttpRetry,
) {

	fun sendMessage(to: String, message: String) {
		httpRetry.execute {
			restClient.post()
				.uri("/send-message")
				.contentType(MediaType.APPLICATION_JSON)
				.body(SendMessageRequest(to, message))
				.retrieve()
				.toBodilessEntity()
		}
	}
}
