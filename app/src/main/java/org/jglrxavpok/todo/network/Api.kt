package org.jglrxavpok.todo.network

import android.content.Context
import androidx.preference.PreferenceManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.jglrxavpok.todo.SHARED_PREF_TOKEN_KEY
import retrofit2.Retrofit

class Api(private val context: Context) {
    companion object {
        // constantes qui serviront à faire les requêtes
        private const val BASE_URL = "https://android-tasks-api.herokuapp.com/api/"
        lateinit var Instance: Api
    }

    private val TOKEN get()= PreferenceManager.getDefaultSharedPreferences(context).getString(SHARED_PREF_TOKEN_KEY, "")//"eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjozNTMsImV4cCI6MTY0MDEwMzYwNX0.fRvOw0MIFpIoHXxGFrAN9vLDm2RHC6YRplonAwoxl6Y"

    // on construit une instance de parseur de JSON:
    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    // instance de convertisseur qui parse le JSON renvoyé par le serveur:
    private val converterFactory =
        jsonSerializer.asConverterFactory("application/json".toMediaType())

    // client HTTP
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                // intercepteur qui ajoute le `header` d'authentification avec votre token:
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $TOKEN")
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }

    // permettra d'implémenter les services que nous allons créer:
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(converterFactory)
        .build()

    fun hasToken() = TOKEN?.isNotBlank() ?: false
}