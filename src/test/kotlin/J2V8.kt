import com.eclipsesource.v8.NodeJS

fun fact(a: Double): Double {
  return if (a > 1.0) a * fact(a - 1) else a * 1.0
}

fun main() {
  val runtime = NodeJS.createNodeJS().runtime
  val str = """
    console.log(global)
//    const cp = require('child_process')
//    cp.exec('ping 8.8.8.8', function(err,out,stderr) {
//      if(err || stderr) throw Error(err||stderr)
//      console.log(out)
//    })
    """.trimIndent()
  val result = runtime.executeScript(str.trimIndent())
//  runtime.release()
}