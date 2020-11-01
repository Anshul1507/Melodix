package tech.anshul1507.melodix.Models

class CurrentSongHelper {
    var songArtist: String? = null
    var songTitle: String? = null
    var songPath: String? = null
    var songId: Long = 0
    var sp: Int = 0
    var isPlaying: Boolean = false
    var isLoop: Boolean = false
    var isShuffle: Boolean = false
    var trackPosition: Int = 0
}