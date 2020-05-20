package me.echeung.moemoekyun.util

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import androidx.preference.PreferenceManager
import java.util.Comparator
import me.echeung.moemoekyun.R
import me.echeung.moemoekyun.adapter.SongsListAdapter
import me.echeung.moemoekyun.client.model.Song

class SongSortUtil(
    private val context: Context
) {

    fun setListSortType(listId: String, sortType: String) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPrefs.edit()
            .putString(PREF_LIST_PREFIX_TYPE + listId, sortType)
            .apply()
    }

    fun setListSortDescending(listId: String, descending: Boolean) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPrefs.edit()
            .putBoolean(PREF_LIST_PREFIX_DESC + listId, descending)
            .apply()
    }

    fun getComparator(listId: String): Comparator<Song> {
        val sortType = getSortTypeByListId(listId)
        val sortDescending = getSortDescendingByListId(listId)

        return when (sortType) {
            SORT_ARTIST ->
                if (sortDescending) {
                    compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.artistsString ?: "" }
                } else {
                    compareBy(String.CASE_INSENSITIVE_ORDER) { it.artistsString ?: "" }
                }

            // Default is SORT_TITLE
            else ->
                if (sortDescending) {
                    compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.titleString ?: "" }
                } else {
                    compareBy(String.CASE_INSENSITIVE_ORDER) { it.titleString ?: "" }
                }
        }
    }

    fun initSortMenu(listId: String, menu: Menu) {
        val sortTypeId = when (getSortTypeByListId(listId)) {
            SORT_ARTIST -> R.id.action_sort_type_artist
            // Default is SORT_TITLE
            else -> R.id.action_sort_type_title
        }
        menu.findItem(sortTypeId).isChecked = true

        val sortDescending = getSortDescendingByListId(listId)
        menu.findItem(R.id.action_sort_desc).isChecked = sortDescending
    }

    fun handleSortMenuItem(item: MenuItem, adapter: SongsListAdapter): Boolean {
        when (item.itemId) {
            R.id.action_sort_desc -> {
                item.isChecked = !item.isChecked
                adapter.sortDescending(item.isChecked)
                return true
            }

            R.id.action_sort_type_title -> {
                item.isChecked = true
                adapter.sortType(SORT_TITLE)
                return true
            }

            R.id.action_sort_type_artist -> {
                item.isChecked = true
                adapter.sortType(SORT_ARTIST)
                return true
            }
        }

        return false
    }

    private fun getSortTypeByListId(listKey: String): String {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPrefs.getString(PREF_LIST_PREFIX_TYPE + listKey, SORT_TITLE)!!
    }

    private fun getSortDescendingByListId(listKey: String): Boolean {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPrefs.getBoolean(PREF_LIST_PREFIX_DESC + listKey, false)
    }

    companion object {
        private const val PREF_LIST_PREFIX_TYPE = "song_sort_list_type_"
        private const val PREF_LIST_PREFIX_DESC = "song_sort_list_desc_"

        private const val SORT_TITLE = "song_sort_title"
        private const val SORT_ARTIST = "song_sort_artist"
    }
}
