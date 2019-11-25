package com.gamezboost.view

public class Text {
    private var heading: String = "Heading"
    private var description: String = "Description"
    private var close: String = "X"

    // Heading Text
    fun heading(heading: String): String {
        this.heading = heading
        return this.heading
    }
    fun heading(): String {
        return this.heading
    }

    // Description Text
    fun description(description: String): String {
        this.description = description
        return this.description
    }
    fun description(): String {
        return this.description;
    }

    // Close Button Text
    fun close(close: String): String {
        this.close = close
        return this.close
    }
    fun close(): String {
        return this.close;
    }
}
