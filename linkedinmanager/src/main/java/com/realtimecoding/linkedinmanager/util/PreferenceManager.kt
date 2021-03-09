package com.realtimecoding.linkedinmanager.util

import android.content.Context


class PreferenceManager {

    private var context: Context? = null

    companion object {
        private var preferenceManager: PreferenceManager? = null
        fun getInstance(context: Context?): PreferenceManager {
            if (preferenceManager == null) {
                preferenceManager = PreferenceManager()
            }
            preferenceManager!!.context = context
            return preferenceManager!!
        }
    }

    fun putString(preferenceName: String?, key: String?, value: String?): Boolean {
        val sharedPreferences = context!!.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        return editor.commit()
    }

    fun putInt(preferenceName: String?, key: String?, value: Int): Boolean {
        val sharedPreferences = context!!.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        return editor.commit()
    }

    fun putFloat(preferenceName: String?, key: String?, value: Float): Boolean {
        val sharedPreferences = context!!.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat(key, value)
        return editor.commit()
    }

    fun getString(preferenceName: String?, key: String?, defaultValue: String?): String? {
        val sharedPreferences = context!!.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, defaultValue)
    }

    fun getInt(preferenceName: String?, key: String?, defaultValue: Int): Int {
        val sharedPreferences = context!!.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun getFloat(preferenceName: String?, key: String?, defaultValue: Float): Float {
        val sharedPreferences = context!!.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        return sharedPreferences.getFloat(key, defaultValue)
    }

    fun removePreferenceKey(preferenceName: String?, key: String?): Boolean {
        val sharedPreferences = context!!.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        return sharedPreferences.edit().remove(key).commit()
    }

}