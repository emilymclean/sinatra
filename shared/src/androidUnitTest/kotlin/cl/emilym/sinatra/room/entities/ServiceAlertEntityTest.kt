package cl.emilym.sinatra.room.entities

import kotlin.test.Test
import kotlin.test.assertEquals

class ServiceAlertEntityTest {

    @Test
    fun `service alert should correctly convert to model when region is blank`() {
        val entity = ServiceAlertEntity(
            "test",
            "title",
            "https://sinatra-transport.com",
            0L,
            ""
        )

        val alert = entity.toModel()

        assertEquals(0, alert.regions.size)
    }

}