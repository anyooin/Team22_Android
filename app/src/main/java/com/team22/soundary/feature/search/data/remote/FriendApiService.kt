package com.team22.soundary.feature.search.data.remote

import com.team22.soundary.core.data.dto.FriendRequestDto
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
    suspend fun getFriends(): Response<FriendsResponse>

    // 친구 요청 보내기/수락하기
    @POST("/api/v1/friends")
    suspend fun addFriend(
        @Body targetDisplayId: FriendRequestDto
    ): Response<Unit>

    // 내가 보낸 친구 요청 목록 가져오기
    @GET("/api/v1/friends/requests/sent")
    suspend fun getSentRequests(): Response<SentRequestsResponse>

    // 받은 친구 요청 목록 가져오기
    @GET("/api/v1/friends/requests/received")
    suspend fun getReceivedRequests(): Response<ReceivedRequestsResponse>

    // 친구 삭제하기
    @DELETE("/api/v1/friends/{target-user-id}")
    suspend fun removeFriend(
        @Path("target-user-id") targetUserId: String
    ): Response<Unit>

    // 받은 친구 요청 거절하기
    @DELETE("/api/v1/friends/requests/received/{target-user-id}")
    suspend fun rejectReceivedRequest(
        @Path("target-user-id") targetUserId: String
    ): Response<Unit>

    // 사용자 검색
    @GET("/api/v1/users")
    suspend fun searchUser(
        @Query("display-id") displayId: String
    ): Response<UserInfoDto>
}
