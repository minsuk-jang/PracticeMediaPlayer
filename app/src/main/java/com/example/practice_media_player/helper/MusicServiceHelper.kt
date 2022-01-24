package com.example.practice_media_player.helper

import android.content.ComponentName
import android.content.Context
import android.media.browse.MediaBrowser
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.example.practice_media_player.MusicLibrary
import com.example.practice_media_player.service.MusicService
import com.orhanobut.logger.Logger

/**
 * 순서
 * 1. MediaBrowser 생성
 * 2. 연결되면 MediaController
 * 3. MediaBrowser subscribe
 */
abstract class MusicServiceHelper(context: Context) {
    private var mediaBrowser: MediaBrowserCompat? = null
    private var mediaController: MediaControllerCompat? = null

    abstract fun onChildLoaded(
        children : MutableList<MediaBrowserCompat.MediaItem>
    )

    fun connect(){
        mediaBrowser?.connect()
    }

    fun disconnect(){
        mediaBrowser?.disconnect()
    }

    private val mediaControllerCallback = object : MediaControllerCompat.Callback(){
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            Logger.i("meta change")
        }
    }

    private val mediaBrowserSubscriptionCallback =
        object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                onChildLoaded(children)
            }
        }

    private val mediaBrowserCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            mediaBrowser?.let {
                mediaController = MediaControllerCompat(context, it.sessionToken).apply {
                    registerCallback(mediaControllerCallback)
                }

                it.subscribe(it.root, mediaBrowserSubscriptionCallback)
            }
        }
    }

    init {
        mediaBrowser = MediaBrowserCompat(
            context,
            ComponentName(context, MusicService::class.java),
            mediaBrowserCallback,
            null
        )
    }
}