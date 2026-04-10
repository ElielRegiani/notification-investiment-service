package investiment_intelligence_platform.notification_service

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest
@EmbeddedKafka(
	partitions = 1,
	topics = ["ml-prediction-generated", "market-data-updated"],
)
class NotificationServiceApplicationTests {

	@MockitoBean
	lateinit var stringRedisTemplate: StringRedisTemplate

	@Test
	fun contextLoads() {
	}
}
