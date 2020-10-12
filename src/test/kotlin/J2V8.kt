import com.eclipsesource.v8.V8

fun fact(a: Double): Double {
    return if (a > 1.0) a * fact(a - 1) else a * 1.0
}

fun main() {
    val runtime = V8.createV8Runtime()
    val str = """
            const f = a => a != 1 ? a * f(a-1) : a * 1
            const st = Date.now()
                for(var i = 100000; i; i--) {
                    f(100)
                }
            (Date.now() - st) + ' ms'
    """.trimIndent()
    val result = runtime.executeScript(str.trimIndent())
    println(result)
    runtime.release()
    val st = System.currentTimeMillis()
    for (x in 0..100000) {
        fact(100.0)
    }
    println((System.currentTimeMillis() - st).toString() + " ms")
}