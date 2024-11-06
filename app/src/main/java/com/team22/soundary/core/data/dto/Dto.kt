package com.team22.soundary.core.data.dto

import android.net.Uri
import com.team22.soundary.core.domain.model.Category
import com.team22.soundary.core.domain.model.Share
import com.team22.soundary.core.domain.model.Song
import com.team22.soundary.core.domain.model.Token
import com.team22.soundary.core.domain.model.User
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

// 친구 목록 응답 DTO
@Serializable
data class FriendsResponse(
    @SerialName("friends") val friends: List<FriendInfoDto>?
)

// 받은 친구 요청 목록 응답 DTO
@Serializable
data class ReceivedRequestsResponse(
    @SerialName("received_requests") val receivedRequests: List<FriendInfoDto>?
)

// 보낸 친구 요청 목록 응답 DTO
@Serializable
data class SentRequestsResponse(
    @SerialName("sent_requests") val sentRequests: List<FriendInfoDto>?
)

@Serializable
data class UserInfoDto(
    @SerialName("display_id") val id : String?,
    @SerialName("nickname") val name : String?,
    @SerialName("description") val description : String?,
    @SerialName("profile_image_url") val profile : String?,
    @SerialName("roles") val roles : List<String>?
)

@Serializable
data class FriendInfoDto(
    @SerialName("id") val id : String?,
    @SerialName("display_id") val displayId : String?,
    @SerialName("nickname") val name : String?,
    @SerialName("profile_image_url") val profile : String?,
)

@Serializable
data class ReceivedShareListDto(
    @SerialName("total") val total: Int?,
    @SerialName("total_pages") val totalPage : Int?,
    @SerialName("shared_musics") val shareList: List<ReceivedShareDto>?
)

@Serializable
data class SentShareListDto(
    @SerialName("total") val total: Int?,
    @SerialName("total_pages") val totalPage : Int?,
    @SerialName("shared_musics") val shareList: List<SentShareDto>?
)

@Serializable
data class ReceivedShareDto(
    @SerialName("id") val id: String?,
    @SerialName("from_user") val fromUser: FromUserResponse?,
    @SerialName("track") val track: TrackDto?,
    @SerialName("comment") val comment: String?,
    @Contextual @SerialName("shared_at") val sharedAt: Date?,
    @SerialName("is_liked") val isLiked : Boolean? = false
)

@Serializable
data class SentShareDto(
    @SerialName("id") val id: String?,
    @SerialName("track") val track: TrackDto?,
    @SerialName("comment") val comment: String?,
    @Contextual @SerialName("shared_at") val sharedAt: Date?
)

@Serializable
data class FromUserResponse(
    @SerialName("id") val id: String?,
    @SerialName("display_name") val displayName: String?,
    @SerialName("name") val name: String?,
    @SerialName("profile_image_url") val profileImageUrl: String?
)

@Serializable
data class TrackListDto(
    @SerialName("tracks") val trackList : List<TrackDto>?
)

@Serializable
data class TrackDto(
    @SerialName("platform") val platform: String? = null,
    @SerialName("track_id") val platformTrackId: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("artists") val artist: List<String>? = null,
    @SerialName("duration") val duration: Int? = null,
    @SerialName("album_cover_url") val albumCoverUrl: String? = null,
    @SerialName("preview_mp3_url") val previewMp3Url: String? = null
)

@Serializable
data class TokenDto(
    @SerialName("role") val role: String?,
    @SerialName("accessToken") val accessToken: String?,
    @SerialName("refreshToken") val refreshToken: String?,
    @SerialName("expiresIn") val expiresIn : Int?
)

@Serializable
data class ErrorResponse(
    @SerialName("code") val code: String?,
    @SerialName("message") val message: String?
)

@Serializable
data class LoginRequestDto(
    @SerialName("platform") val platform: String = "KAKAO",
    @SerialName("token") val token: String
)

@Serializable
data class FriendRequestDto(
    @SerialName("target_display_id") val targetId : String
)

@Serializable
data class UserInfoResponse(
    @SerialName("display_id") val displayId : String,
    @SerialName("nickname") val nickname: String,
    @SerialName("description") val description: String? = null,
    @SerialName("profile_image_url") val profileImage : String? = null,
    @SerialName("roles") val roles : List<String>,
    @SerialName("labels") val category : List<Category>,
)

@Serializable
data class RefreshRequestDto(
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class UserInfoInitRequestDto(
    @SerialName("labels") val category : List<Category>,
    @SerialName("device_token") val deviceToken : String,
    @SerialName("display_id") val displayId : String,
    @SerialName("nickname") val nickname: String,
    @SerialName("description") val description: String? = null,
    @SerialName("profile_image_url") val profileImage : String? = null
)

data class ShareMusicRequest(
    @SerialName("track") val track: TrackIdentifierDto,
    @SerialName("comment") val comment: String,
    @SerialName("target_user_ids") val userList: List<String>
)

@Serializable
data class TrackIdentifierDto(
    @SerialName("platform") val platform: String = "SPOTIFY",
    @SerialName("platform_track_id") val platformTrackId: String
)

@Serializable
data class ShareMusicResponse(
    @SerialName("shared_music_id") val platformTrackId: String?
)

fun SentShareDto.toVO(): Share {
    return Share(
        this.id ?: "",
        this.track?.toVO() ?: Song(),
        this.comment ?: "",
        User(),//me
        false,
        this.sharedAt ?: Date()
    )
}

fun ReceivedShareDto.toVO(): Share {
    return Share(
        this.id ?: "",
        this.track?.toVO() ?: Song(),
        this.comment ?: "",
        this.fromUser?.toVO() ?: User(),
        this.isLiked ?: false,
        this.sharedAt ?: Date()
    )
}

fun TrackDto.toVO(): Song =
    Song(
        this.platformTrackId ?: "",
        this.title ?: "",
        this.artist ?: emptyList(),
        Uri.parse(this.previewMp3Url ?: ""),
        Uri.parse(this.albumCoverUrl ?: "")
    )

fun FromUserResponse.toVO(): User =
    User(
        name = this.displayName ?: "",
        image = Uri.parse(this.profileImageUrl ?: "")
    )


fun UserInfoDto.toVO(): User {
    return User(
        id = this.id ?: "",
        name = this.name ?: "",
        image = Uri.parse(this.profile),
        statusMessage = this.description ?: "",
        role = this.roles ?: emptyList()
    )
}

fun FriendInfoDto.toVO(): User {
    return User(
        id = this.id ?: "",
        displayId = this.displayId ?: "",
        name = this.name ?: "",
        image = Uri.parse(this.profile),
    )
}

fun UserInfoResponse.toVO(): User {
    return User(
        displayId = this.displayId ?: "",
        name = this.nickname ?: "",
        image = Uri.parse(this.profileImage),
    )
}

fun TokenDto.toVO() : Token =
    Token(
        this.accessToken ?: "",
        this.refreshToken ?: ""
    )

