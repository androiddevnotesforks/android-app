package me.echeung.moemoekyun.client.api.v5

import me.echeung.moemoekyun.FavoritesQuery
import me.echeung.moemoekyun.SearchQuery
import me.echeung.moemoekyun.UserQuery
import me.echeung.moemoekyun.client.model.Song
import me.echeung.moemoekyun.client.model.SongDescriptor
import me.echeung.moemoekyun.client.model.User
import me.echeung.moemoekyun.fragment.SongFields

fun UserQuery.User.transform(): User {
    return User(
            this.displayName!!,
            this.avatarImage,
            this.bannerImage,
            this.additionalSongRequests
    )
}

fun FavoritesQuery.Song.transform(): Song {
    return this.fragments.songFields.transform()
}

fun SearchQuery.Search.transform(): Song {
    return this.fragments.songFields!!.transform()
}

private fun SongFields.transform(): Song {
    return Song(
            this.id,
            this.title,
            this.titleRomaji,
            this.titleSearchRomaji,
            this.artists.mapNotNull { it?.transform() },
            this.sources.mapNotNull { it?.transform() },
            this.albums.mapNotNull { it?.transform() },
            this.duration
    )
}

private fun SongFields.Artist.transform(): SongDescriptor {
    return SongDescriptor(
            this.id,
            this.name,
            this.nameRomaji,
            this.image
    )
}

private fun SongFields.Source.transform(): SongDescriptor {
    return SongDescriptor(
            this.id,
            this.name,
            this.nameRomaji,
            this.image
    )
}

private fun SongFields.Album.transform(): SongDescriptor {
    return SongDescriptor(
            this.id,
            this.name,
            this.nameRomaji,
            this.image
    )
}
