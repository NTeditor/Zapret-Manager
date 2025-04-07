package ru.nteditor.zapretkotlin

import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException


class Shell(private val command: List<String>) {


    private fun List<String>.runCommand(): String {
        try {
            val proc = ProcessBuilder(this)
                .directory(File("/"))
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            return proc.inputStream.bufferedReader().readText()
        } catch (e: IOException) {
            e.printStackTrace()
            return "Error"
        }
    }

    private fun getCommand(): List<String> {
        if (command == listOf("")) {
            throw IllegalArgumentException("Command list is empty")
        }
        return command
    }

    fun start(): String {
        return getCommand().runCommand()
    }

}