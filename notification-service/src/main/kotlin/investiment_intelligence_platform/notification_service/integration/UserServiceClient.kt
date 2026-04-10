package investiment_intelligence_platform.notification_service.integration

import investiment_intelligence_platform.notification_service.model.UserAlertDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class UserServiceClient(
	@Qualifier("userServiceRestClient") private val restClient: RestClient,
	private val httpRetry: HttpRetry,
) {

	private val listType = object : ParameterizedTypeReference<List<UserAlertDto>>() {}

	fun fetchAlertsForSymbol(symbol: String): List<UserAlertDto> =
		httpRetry.execute {
			restClient.get()
				.uri("/users/alerts?symbol={symbol}", symbol)
				.retrieve()
				.body(listType)
		} ?: emptyList()
}
