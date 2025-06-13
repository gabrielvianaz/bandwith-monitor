package cloud.gviana.bandwithmonitor.service

import org.snmp4j.CommunityTarget
import org.snmp4j.PDU
import org.snmp4j.Snmp
import org.snmp4j.mp.SnmpConstants
import org.snmp4j.smi.OID
import org.snmp4j.smi.OctetString
import org.snmp4j.smi.UdpAddress
import org.snmp4j.smi.VariableBinding
import org.snmp4j.transport.DefaultUdpTransportMapping
import org.springframework.stereotype.Service

@Service
class SnmpService {

    fun sendCommand(address: String, oid: String): List<VariableBinding> {
        try {
            val transport = getTransport()
            val target = getTarget(address)
            val snmp = Snmp(transport)
            val pdu = PDU().apply {
                add(VariableBinding(OID(oid)))
            }
            val responseEvent = snmp.get(pdu, target)
            val response = responseEvent.response ?: throw RuntimeException("Invalid SNMP response")

            if (response.errorStatus != PDU.noError) {
                throw RuntimeException("Error in SNMP response: ${response.errorStatusText}")
            }

            return response.variableBindings
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Error sending SNMP command", e)
        }
    }

    fun sendCommandAndGetFirstVariable(address: String, oid: String): VariableBinding {
        return sendCommand(address, oid).first()
    }

    private fun getTarget(addr: String): CommunityTarget<UdpAddress> {
        return CommunityTarget<UdpAddress>().apply {
            community = OctetString("public")
            address = UdpAddress("$addr/161")
            retries = 2
            timeout = 1500
            version = SnmpConstants.version2c
        }
    }

    private fun getTransport(): DefaultUdpTransportMapping {
        return DefaultUdpTransportMapping().apply {
            listen()
        }
    }

}
