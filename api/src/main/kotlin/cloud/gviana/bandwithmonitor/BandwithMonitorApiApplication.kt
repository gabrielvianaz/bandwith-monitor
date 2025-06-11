package cloud.gviana.bandwithmonitor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class BandwithMonitorApiApplication

fun main(args: Array<String>) {
    runApplication<BandwithMonitorApiApplication>(*args)
}
