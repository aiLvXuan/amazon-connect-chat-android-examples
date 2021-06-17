package me.lvxuan.amazonconnectdemo.chat

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider

/**
 * @author LvXuan
 * Created by LvXuan on 2021/6/16 17:29.
 */
class AWSStaticCredentialsProvider(val accessKey: String, val secretKey: String) :
    AWSCredentialsProvider {
    override fun getCredentials(): AWSCredentials {
        return object : AWSCredentials {
            override fun getAWSAccessKeyId() = accessKey

            override fun getAWSSecretKey() = secretKey
        }
    }

    override fun refresh() {
    }
}