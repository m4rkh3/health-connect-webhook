package com.hcwebhook.app

import androidx.health.connect.client.records.MealType
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class JsonRecordMetadataTest {

    @Test
    fun metadata_includesIdentityFieldsWhenPresent() {
        val meta = RecordMetadata(
            dataOrigin = "com.example",
            recordingMethod = "manual_entry",
            id = "rec-1",
            clientRecordId = "client-1",
            clientRecordVersion = 3L,
            lastModifiedTime = Instant.parse("2026-05-09T12:00:00Z"),
            startZoneOffsetSeconds = 10800,
            endZoneOffsetSeconds = 10800,
        )

        val json = buildJsonObject { putRecordMetadata(meta) }["metadata"]!!.jsonObject

        assertEquals("com.example", json["data_origin"]!!.jsonPrimitive.content)
        assertEquals("manual_entry", json["recording_method"]!!.jsonPrimitive.content)
        assertEquals("rec-1", json["id"]!!.jsonPrimitive.content)
        assertEquals("client-1", json["client_record_id"]!!.jsonPrimitive.content)
        assertEquals(3L, json["client_record_version"]!!.jsonPrimitive.content.toLong())
        assertEquals("2026-05-09T12:00:00Z", json["last_modified_time"]!!.jsonPrimitive.content)
        assertEquals(10800, json["start_zone_offset_seconds"]!!.jsonPrimitive.content.toInt())
        assertEquals(10800, json["end_zone_offset_seconds"]!!.jsonPrimitive.content.toInt())
        assertFalse(json.containsKey("zone_offset_seconds"))
        assertFalse(json.containsKey("device"))
    }

    @Test
    fun metadata_omitsIdentityFieldsWhenAbsent() {
        val meta = RecordMetadata(dataOrigin = "com.example", recordingMethod = "unknown")

        val json = buildJsonObject { putRecordMetadata(meta) }["metadata"]!!.jsonObject

        assertEquals(setOf("data_origin", "recording_method"), json.keys)
    }

    @Test
    fun mealTypeName_mapsHealthConnectConstants() {
        assertEquals("breakfast", mealTypeName(MealType.MEAL_TYPE_BREAKFAST))
        assertEquals("lunch", mealTypeName(MealType.MEAL_TYPE_LUNCH))
        assertEquals("dinner", mealTypeName(MealType.MEAL_TYPE_DINNER))
        assertEquals("snack", mealTypeName(MealType.MEAL_TYPE_SNACK))
        assertNull(mealTypeName(MealType.MEAL_TYPE_UNKNOWN))
        assertNull(mealTypeName(99))
    }
}
