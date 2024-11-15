package com.team22.soundary

import com.team22.soundary.feature.main.data.remote.ShareService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.test.runTest

class ShareServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var shareService: ShareService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        shareService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ShareService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `requestReceiveShare returns expected data`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"shareList": []}""")
        mockWebServer.enqueue(mockResponse)

        // suspend 함수를 코루틴 블록에서 호출
        val response = shareService.requestReceiveShare()

        // Assert를 통해 응답이 성공했는지 확인
        assert(response.isSuccessful)
        assert(response.body()?.shareList?.isEmpty() == true)
    }

}
