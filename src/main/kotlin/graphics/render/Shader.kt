package graphics.render

import java.io.File
import api.enums.G
import kotlinx.serialization.json.Json
import kotlinx.serialization.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL41C
import org.lwjgl.opengl.GL46C.*

@Serializable
private data class JsonShader(val type: String, val src: String)

class ShaderProgram(private val path: String) {
  private val shaderSources = HashMap<Int, String>()

  init {
    initShaders(readShaderSrc(this.path))
  }

  private fun readShaderSrc(path: String): Array<JsonShader> {
    return Json.decodeFromString(File(path).readText())
  }

  private fun initShaders(shaders: Array<JsonShader>): Map<Int,String> {
    shaders.forEach { e ->
      shaderSources[G.valueOf(e.type).value] = File("./resources/default/${e.src}").readText()
    }
    return shaderSources
  }



}