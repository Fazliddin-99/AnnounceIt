package com.example.announceit.di


import android.app.Application
import android.app.DownloadManager
import androidx.room.Room
import com.example.announceit.data.FirebaseStorageManager
import com.example.announceit.data.datastore.AnnounceDataStore
import com.example.announceit.data.datastore.dataStore
import com.example.announceit.data.db.AnnounceItDatabase
import com.example.announceit.data.repository.AnnounceItRepository
import com.example.announceit.data.repository.AnnounceItRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): AnnounceItDatabase {
        return Room.databaseBuilder(
            app, AnnounceItDatabase::class.java, "announce_it"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideAnnounceItRepository(
        db: AnnounceItDatabase,
        dataStore: AnnounceDataStore,
        dowloadManager: DownloadManager,
        firebaseStorageManager: FirebaseStorageManager
    ): AnnounceItRepository {
        return AnnounceItRepositoryImpl(db, dataStore, dowloadManager, firebaseStorageManager)
    }

    @Provides
    @Singleton
    fun provideLandtechDataStore(app: Application): AnnounceDataStore {
        return AnnounceDataStore(app.dataStore)
    }

    @Provides
    @Singleton
    fun provideDownloadManager(app: Application): DownloadManager {
        return app.getSystemService(DownloadManager::class.java)
    }

    @Provides
    @Singleton
    fun provideFirebaseStorageManager(app: Application): FirebaseStorageManager {
        return FirebaseStorageManager(app)
    }
}