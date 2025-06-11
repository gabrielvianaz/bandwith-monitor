package cloud.gviana.bandwithmonitor.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
class ThreadPoolTaskSchedulerConfig {

    @Bean
    fun threadPoolTaskScheduler(): ThreadPoolTaskScheduler {
        val threadPoolTaskScheduler = ThreadPoolTaskScheduler()

        threadPoolTaskScheduler.poolSize = 10
        threadPoolTaskScheduler.setThreadNamePrefix("bandwidth-monitor-")

        return threadPoolTaskScheduler
    }

}
