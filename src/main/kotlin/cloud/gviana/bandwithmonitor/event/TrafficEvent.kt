package cloud.gviana.bandwithmonitor.event

data class TrafficEvent(val address: String, val interfaceName: String, val sessionId: String)
