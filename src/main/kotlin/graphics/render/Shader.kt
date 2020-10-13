package graphics.render

import java.io.File
import api.enums.G
import kotlinx.serialization.json.Json
import kotlinx.serialization.*
import org.lwjgl.opengl.GL46C.*

@Serializable
private data class JsonShader(val type: String, val src: String)

class ShaderProgram(private val path: String){
  private var shaderSources = HashMap<Int, String>() //id src
  private val shaders = HashMap<Int, Int>() //id type
  private var programId: Int = 0

  init {
    initShaders(readShaderSrc(this.path))
    compileShaders()
    linkProgram()
  }

  fun Use() {
    glUseProgram(programId)
  }


  private fun readShaderSrc(path: String): Array<JsonShader> {
    return Json.decodeFromString(File(path).readText())
  }

  private fun initShaders(shaders: Array<JsonShader>) {
    shaders.forEach { e ->
      shaderSources[G.valueOf(e.type).value] = File("./resources/default/${e.src}").readText()
    }
  }

  private fun compileShaders() {
    val status = IntArray(1)
    shaderSources.forEach {
      shaders[glCreateShader(it.key)] = it.key
    }
    shaders.forEach {
      glShaderSource(it.key, shaderSources[it.value]!!)
      glCompileShader(it.key)
      glGetShaderiv(it.key, GL_COMPILE_STATUS, status)
      if (status[0] == GL_FALSE) {
        val str = glGetShaderInfoLog(it.key)
        glDeleteShader(it.key)
        throw RuntimeException(str)
      }
    }
  }

  private fun linkProgram() {
    programId = glCreateProgram()
    shaders.forEach {
      glAttachShader(programId, it.key)
    }
    glLinkProgram(programId)
    val status = IntArray(1)
    glGetProgramiv(programId, GL_LINK_STATUS, status)
    if (status[0] == GL_FALSE) {
      val str = glGetProgramInfoLog(programId)
      glDeleteProgram(programId)
      throw RuntimeException(str)
    }
    shaders.forEach {
      glDetachShader(programId, it.key)
    }
  }

  fun dropSrc() {
    shaderSources.clear()
  }
}
