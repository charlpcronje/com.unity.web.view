package com.gamezboost.view

data class Colors {
    private var heading: String = "#00000"
    private var background: String = "#FFFFFF"
    private var description: String = "#000000"

    // Heading Test Color
    fun heading(value: String = ""): String {
        this.heading = value
        return this.heading
    }
    fun heading(): String {
        return heading;
    }

    // Background Color
    fun background(value: String): String {
        this.background = value
        return this.background
    }
    fun background():String {
        return this.background
    }

    // Description Test Color
    fun description(value: String): String {
        this.description = value
        return this.description
    }
    fun description(): String {
        return this.description
    }
}