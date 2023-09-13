package com.rajit.learnrxjava.model


/*
        "postLink": "https://redd.it/d8l4iq",
      "subreddit": "sylhet",
      "title": "Shahi Eidgah , Sylhet.",
      "url": "https://i.redd.it/428ob5a4x4k31.jpg",
      "nsfw": false,
      "spoiler": false,
      "author": "Zwadesht",
      "ups": 2,
      "preview": [
        "https://i.redd.it/428ob5a4x4k31.jpg"
      ]

 */
data class Meme(
    val postLink: String?,
    val subreddit: String?,
    val title: String?,
    val url: String?,
    val nsfw: Boolean?,
    val spoiler: Boolean?,
    val author: String?,
    val ups: Int?,
    val preview: Array<String>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Meme

        if (postLink != other.postLink) return false
        if (subreddit != other.subreddit) return false
        if (title != other.title) return false
        if (url != other.url) return false
        if (nsfw != other.nsfw) return false
        if (spoiler != other.spoiler) return false
        if (author != other.author) return false
        if (ups != other.ups) return false
        if (preview != null) {
            if (other.preview == null) return false
            if (!preview.contentEquals(other.preview)) return false
        } else if (other.preview != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = postLink?.hashCode() ?: 0
        result = 31 * result + (subreddit?.hashCode() ?: 0)
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (nsfw?.hashCode() ?: 0)
        result = 31 * result + (spoiler?.hashCode() ?: 0)
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + (ups ?: 0)
        result = 31 * result + (preview?.contentHashCode() ?: 0)
        return result
    }
}