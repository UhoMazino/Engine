package graphics.render

import api.enums.G
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.lwjgl.opengl.GL46C.*
import java.io.File

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

  fun getId(): Int {
    return programId
  }

  fun use() {
    glUseProgram(programId)
  }

  fun unUse() {
    glUseProgram(0)
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
    for (it in shaderSources) {
      shaders[glCreateShader(it.key)] = it.key
    }
    for (it in shaders) {
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
    for (it in shaders) {
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
    for (it in shaders) {
      glDetachShader(programId, it.key)
    }
  }

  fun dropSrc() {
    shaderSources.clear()
  }
}