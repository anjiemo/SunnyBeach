package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

class ApiResponse<T>(
    @SerializedName("code")
    private val code: Int,
    @SerializedName("success")
    private val success: Boolean,
    @SerializedName("message")
    private val message: String,
    @SerializedName("data")
    private val `data`: T
) : IApiResponse<T> {

    override fun getCode(): Int = code

    override fun isSuccess(): Boolean = success

    override fun getMessage(): String = message

    override fun getData(): T = data
}