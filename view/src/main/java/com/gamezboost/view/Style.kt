package com.gamezboost.view

internal class Style {
    private var color: Colors = Colors()
    private var text: Text = Text()


    fun color(color: Colors): Colors {
        this.color = color
        return this.color
    }
    fun color(): Colors {
        return this.color;
    }

    fun text(text: Text): Text {
        this.text = text
        return this.text
    }
    fun text(): Text {
        return this.text;
    }
}

