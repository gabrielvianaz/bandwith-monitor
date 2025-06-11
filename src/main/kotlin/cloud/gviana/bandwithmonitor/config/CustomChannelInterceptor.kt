package cloud.gviana.bandwithmonitor.config

import cloud.gviana.bandwithmonitor.event.TrafficScheduler
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component
import java.security.Principal

@Component
class CustomChannelInterceptor(private val trafficScheduler: TrafficScheduler) : ChannelInterceptor {

    override fun preSend(
        message: Message<*>,
        channel: MessageChannel
    ): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        if (accessor != null) {
            if (StompCommand.CONNECT == accessor.command) {
                val sessionId = accessor.getHeader("simpSessionId") as String

                accessor.user = Principal { sessionId }
            }

            if (StompCommand.SUBSCRIBE == accessor.command) {
                val address = getNativeHeader(accessor, "address")
                val interfaceName = getNativeHeader(accessor, "interface")

                trafficScheduler.schedule(address, interfaceName, accessor.user)
            }

            if (StompCommand.DISCONNECT == accessor.command) {
                trafficScheduler.unschedule(accessor.user)
            }
        }

        return message
    }

    private fun getNativeHeader(accessor: StompHeaderAccessor, headerName: String): String {
        return accessor.getFirstNativeHeader(headerName)
            ?: throw RuntimeException("$headerName not found in native headers")
    }
}
