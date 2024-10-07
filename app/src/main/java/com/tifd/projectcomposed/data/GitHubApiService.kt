package com.tifd.projectcomposed.data

import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApiService {

    // Fungsi untuk mengambil data profil pengguna GitHub berdasarkan username
    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): GithubUser
}
