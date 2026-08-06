package com.example.sdkstudydemo.event.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.sdkstudydemo.event.model.SdkPendingEvent
import kotlinx.coroutines.flow.first
import org.json.JSONArray

//定义一个DataStore文件，名叫sdk_pending_event_store
private val Context.sdkPendingEventDataStore by preferencesDataStore(
    name = "sdk_pending_event_store"
)

class DataStoreSdkPendingEventStore(
    context: Context
) : SdkPendingEventStore {

    private val appContext = context.applicationContext

    private object Keys {
        val PENDING_EVENTS_JSON = stringPreferencesKey("pending_events_json")
    }

    override suspend fun loadPendingEvents(): List<SdkPendingEvent> {
        val preferences = appContext
            .sdkPendingEventDataStore
            .data
            .first()

        val jsonString = preferences[Keys.PENDING_EVENTS_JSON]
            ?: return emptyList()

        val jsonArray = JSONArray(jsonString)
        //把 JSONArray 里的每一个 JSONObject
        //还原成 SdkPendingEvent
        //最后组成 List<SdkPendingEvent>
        val result = mutableListOf<SdkPendingEvent>()

        for (index in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.optJSONObject(index)
                ?: continue

            val pendingEvent = SdkPendingEvent.fromJsonObject(jsonObject)

            result.add(pendingEvent)
        }

        return result
    }

    override suspend fun savePendingEvents(
        pendingEvents: List<SdkPendingEvent>
    ) {
        val jsonArray = JSONArray()

        for (pendingEvent in pendingEvents) {
            jsonArray.put(pendingEvent.toJsonObject())
        }

        appContext.sdkPendingEventDataStore.edit { preferences ->
            preferences[Keys.PENDING_EVENTS_JSON] = jsonArray.toString()
        }
    }

    override suspend fun clear() {
        appContext.sdkPendingEventDataStore.edit { preferences ->
            preferences.remove(Keys.PENDING_EVENTS_JSON)
        }
    }
}