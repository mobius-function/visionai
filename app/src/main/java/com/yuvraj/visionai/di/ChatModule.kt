package com.yuvraj.visionai.di

import android.content.Context
import androidx.room.Room
import com.yuvraj.visionai.db.ChatHistoryDatabase
import com.yuvraj.visionai.db.dao.ChatMessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideChatDatabase(context: Context): ChatHistoryDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ChatHistoryDatabase::class.java,
            "chat_db"
        ).build()
    }

    @Provides
    fun provideChatMessageDao(chatDatabase: ChatHistoryDatabase): ChatMessageDao {
        return chatDatabase.chatMessageDao()
    }
}