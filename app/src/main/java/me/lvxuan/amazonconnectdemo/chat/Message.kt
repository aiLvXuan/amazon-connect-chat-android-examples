package me.lvxuan.amazonconnectdemo.chat

import java.util.*

/**
 * @author LvXuan
 * Created by LvXuan on 2021/6/15 10:38.
 */
data class Message(
    var participant: String?,
    var text: String?,
    val id: UUID = UUID.randomUUID()
)