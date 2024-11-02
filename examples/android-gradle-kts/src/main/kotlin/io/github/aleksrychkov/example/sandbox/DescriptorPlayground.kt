package io.github.aleksrychkov.example.sandbox

@Suppress("unused", "UNUSED_PARAMETER", "UNUSED_VARIABLE")
class DescriptorPlayground : Playground {

    override fun test() {
        foo("1", 1)
        llong()
        aany(Any())
        iint(1)
        vvoid()
    }

    private fun foo(param1: String, param2: Int): String {
        return "foo result $param1 $param2"
    }

    private fun llong(): Long {
        return 1L
    }

    private fun aany(any: Any): Any {
        return Unit
    }

    private fun iint(p: Int): Int {
        return p
    }

    private fun vvoid() {
        val nothing = Unit
    }
}
