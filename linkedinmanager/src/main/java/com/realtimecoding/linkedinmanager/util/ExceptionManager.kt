package com.realtimecoding.linkedinmanager.util

object ExceptionManager {
    fun exceptionLog(exception: Exception) {
        exception.printStackTrace()
    }

    fun errorLog(error: Error) {
        error.printStackTrace()
    }

    fun throwableLog(throwable: Throwable) {
        throwable.printStackTrace()
    }
}