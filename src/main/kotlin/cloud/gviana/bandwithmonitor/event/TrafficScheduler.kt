package cloud.gviana.bandwithmonitor.event

import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Component
import java.security.Principal
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledFuture

@Component
class TrafficScheduler(
    private val taskScheduler: ThreadPoolTaskScheduler,
    private val eventPublisher: ApplicationEventPublisher
) {

    private val scheduledTasks = ConcurrentHashMap<String, ScheduledFuture<*>>()

    fun schedule(address: String, interfaceName: String, user: Principal?) {
        user?.name?.let {
            if (!scheduledTasks.containsKey(it)) {
                val task = taskScheduler.scheduleAtFixedRate({
                    eventPublisher.publishEvent(TrafficEvent(address, interfaceName, it))
                }, Duration.ofSeconds(1))

                scheduledTasks[it] = task
            }
        }
    }

    fun unschedule(user: Principal?) {
        user?.name?.let {
            scheduledTasks.remove(it)?.cancel(false)
        }
    }

}
