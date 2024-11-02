package io.github.aleksrychkov.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class AbstractActivity : AppCompatActivity(), AutoTrace {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
