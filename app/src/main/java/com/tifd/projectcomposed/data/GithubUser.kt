package com.tifd.projectcomposed.data

import com.google.gson.annotations.SerializedName

data class GithubUser(
    @SerializedName("login") val username: String,
    @SerializedName("name") val name: String?,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("followers") val followers: Int,
    @SerializedName("following") val following: Int
)
