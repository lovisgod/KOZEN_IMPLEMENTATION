package com.isw.iswkozen.di

import android.util.Base64
import com.interswitchng.smartpos.simplecalladapter.SimpleCallAdapterFactory
import com.isw.iswkozen.core.data.utilsData.Constants
import com.isw.iswkozen.core.network.AuthInterface
import com.isw.iswkozen.core.network.kimonoInterface
import com.isw.iswkozen.views.repo.IswDataRepo
import com.isw.iswkozen.views.viewmodels.IswKozenViewModel
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

const val RETROFIT_KIMONO = "kimono_retrofit"
const val AUTH_INTERCEPTOR = "auth_interceptor"


val networkModule = module {

    factory {


        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
    }

    // retrofit interceptor for authentication
    single(AUTH_INTERCEPTOR) {

        return@single Interceptor { chain ->
            Prefs.getString(Constants.TOKEN)
            val request = chain.request().newBuilder()
                    .addHeader("Content-type", "application/json")
                    .addHeader("Authorization", "Bearer $it")
                    .build()

            return@Interceptor chain.proceed(request)

        }
    }

    // create Auth service with retrofit
    single {


        val inttrr = Interceptor { chain ->
            Prefs.getString(Constants.TOKEN)
            val request = chain.request().newBuilder()
                .addHeader("Content-type", "application/json")
                .addHeader("Authorization", "Bearer $it")
                .build()

            return@Interceptor chain.proceed(request)

        }
        // set base url based on env
        val iswBaseUrl = Constants.ISW_KIMONO_BASE_URL

        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        val authInterceptor: Interceptor = inttrr

        val strategy = AnnotationStrategy()
        val serializer = Persister(strategy)
        val builder = Retrofit.Builder()
            .baseUrl(iswBaseUrl)
            .addConverterFactory(SimpleXmlConverterFactory.createNonStrict(serializer))
            .addCallAdapterFactory(SimpleCallAdapterFactory.create())

        // getResult the okhttp client for the retrofit
        val clientBuilder: OkHttpClient.Builder = get()


        clientBuilder.addInterceptor(logger)
        // add auth interceptor for max services
//        clientBuilder.addInterceptor(authInterceptor)

        // add client to retrofit builder
        val client = clientBuilder.build()
        builder.client(client)


        val retrofit: Retrofit = builder.build()
        return@single retrofit.create(kimonoInterface::class.java)
    }


    // create Auth service with retrofit
    single {

        // set base url based on env
        val iswBaseUrl = Constants.ISW_KIMONO_BASE_URL

        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        val builder = Retrofit.Builder()
            .baseUrl(iswBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(SimpleCallAdapterFactory.create())

        // getResult the okhttp client for the retrofit
        val clientBuilder: OkHttpClient.Builder = get()


        clientBuilder.addInterceptor(logger)

        // add client to retrofit builder
        val client = clientBuilder.build()
        builder.client(client)


        val retrofit: Retrofit = builder.build()
        return@single retrofit.create(AuthInterface::class.java)
    }

}
