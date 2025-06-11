package cloud.gviana.bandwithmonitor.controller

import cloud.gviana.bandwithmonitor.service.SnmpService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SnmpController(private val snmpService: SnmpService) {

    @GetMapping("/interfaces")
    fun getInterfaces(@RequestParam("address") address: String): List<String> {
        val interfaces = snmpService.sendCommand(address, IF_DESCR_OID, getNext = true)

        return interfaces.map { it.toValueString() }
    }

    companion object {
        const val IF_DESCR_OID = ".1.3.6.1.2.1.2.2.1.2"
    }

}
