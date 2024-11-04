package com.team22.soundary.feature.search.data.api

import com.team22.soundary.core.data.dto.FriendsResponse
import com.team22.soundary.core.data.dto.ReceivedRequestsResponse
import com.team22.soundary.core.data.dto.SentRequestsResponse
import com.team22.soundary.core.data.dto.UserInfoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendApiService {
    // 친구 목록 가져오기
    @GET("/api/v1/friends")
    suspend fun getFriends(
        @Query("userId") userId: String,
        @Query("label") label: List<String>? = null
    ): Response<FriendsResponse>

    // 친구 요청 보내기/수락하기
    @POST("/api/v1/friends")
    suspend fun addFriend(
        @Query("userId") userId: String,
        @Body targetDisplayId: Map<String, String>
    ): Response<Void>

    // 내가 보낸 친구 요청 목록 가져오기
    @GET("/api/v1/friends/requests/sent")
    suspend fun getSentRequests(
        @Query("userId") userId: String
    ): Response<SentRequestsResponse>

    // 받은 친구 요청 목록 가져오기
    @GET("/api/v1/friends/requests/received")
    suspend fun getReceivedRequests(
        @Query("userId") userId: String
    ): Response<ReceivedRequestsResponse>

    // 친구 삭제하기
    @DELETE("/api/v1/friends/{target-user-id}")
    suspend fun removeFriend(
        @Path("target-user-id") targetUserId: String
    ): Response<Void>

    // 받은 친구 요청 거절하기
    @DELETE("/api/v1/friends/requests/received/{target-user-id}")
    suspend fun rejectReceivedRequest(
        @Path("target-user-id") targetUserId: String
    ): Response<Void>

    // 친구 프로필 가져오기
    @GET("/api/v1/friends/{friendId}")
    suspend fun getFriendProfile(
        @Path("friendId") friendId: String
    ): Response<UserInfoDto>
}
