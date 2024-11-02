package io.github.aleksrychkov.example

import android.os.Bundle
import io.github.aleksrychkov.example.sandbox.DefaultPlayground
import io.github.aleksrychkov.example.sandbox.DescriptorPlayground
import io.github.aleksrychkov.example.sandbox.OkhttpPlayground
import io.github.aleksrychkov.example.sandbox.Playground


class MainActivity : AbstractActivity(), AutoTrace {

    private val sandbox: List<Playground> = listOf(
        DescriptorPlayground(),
        OkhttpPlayground(),
        DefaultPlayground(),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fl_container,
                MainFragment()
            )
            .commit()


        sandbox.forEach(Playground::test)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }
}
