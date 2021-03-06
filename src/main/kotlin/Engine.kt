import org.apache.logging.log4j.kotlin.logger
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWCharCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.GL_ALPHA_TEST
import org.lwjgl.opengl.GL46C.*
import org.lwjgl.system.MemoryUtil.NULL

import graphics.render.Texture
import graphics.render.ShaderProgram
import org.lwjgl.glfw.GLFWWindowSizeCallback

class Engine {
  companion object {
    val WINDOW_SIZE = Pair(800, 800)
  }

  private val log = logger(this.javaClass.name)
  private var errorCallback: GLFWErrorCallback? = null
  private var charCallback: GLFWCharCallback? = null
  private var keyCallback: GLFWKeyCallback? = null
  private var windowSizeCallback: GLFWWindowSizeCallback? = null
  private var shaderProgram: ShaderProgram? = null
  private var window: Long? = null
  private var textureId: Int = 0
  private var vbo: Int = 0
  private var vao: Int = 0
  private var vbotex: Int = 0
  private var angleLocation: Int = 0
  private var texture = Texture('▲')

  private fun init() {
    log.info("start init")
    // Setup an error callback. The default implementation
    // will print the error message in System.err.
    errorCallback = glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err))

    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if (!glfwInit()) {
      throw IllegalStateException("Unable to initialize GLFW")
    }
    // Configure our window
    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
    glfwWindowHint(GLFW_SAMPLES, 16)

    /**glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)*/


    // Create the window
    window = glfwCreateWindow(WINDOW_SIZE.first, WINDOW_SIZE.second, "Hello World!", NULL, NULL)
    if (window == NULL) {
      throw RuntimeException("Failed to create the GLFW window")
    }

    // Setup a key callback. It will be called every time a key is pressed, repeated or released.

    keyCallback = glfwSetKeyCallback(window!!, object : GLFWKeyCallback() {
      override fun invoke(
        window: Long,
        key: Int,
        scancode: Int,
        action: Int,
        mods: Int,
      ) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
          glfwSetWindowShouldClose(window, true)
        }
      }
    })

    charCallback = glfwSetCharCallback(window!!, object : GLFWCharCallback() {
      override fun invoke(
        window: Long,
        c: Int,
      ) {
        texture = Texture(c.toChar())
      }
    })

    windowSizeCallback = glfwSetWindowSizeCallback(window!!, object : GLFWWindowSizeCallback(){
      override fun invoke(
        window: Long,
        width: Int,
        height: Int
      ) {
        try {
          GL.getCapabilities()
          /*TODO: нет нормальной проверки на создание gl контекста и загрузки адресов функций,
            не удачный get бросает исключение, в тавком случае нельзе делать gl вызовы. А это возмлжно
            так как келбек вызывается до создания контекста gl*/
          glViewport(0, 0, width, height)
        } catch (e: Exception){
          log.warn("Ресайз окна вызван до создания контекста opengl\n${e.message!!}")
        }
      }
    })

    // Get the resolution of the primary monitor
    val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

    // Center our window
    glfwSetWindowPos(
      window!!,
      (vidmode!!.width() - WINDOW_SIZE.first) / 2,
      (vidmode.height() - WINDOW_SIZE.second) / 2
    )

    // Make the OpenGL context current
    glfwMakeContextCurrent(window!!)
    // Enable v-sync
    glfwSwapInterval(1)

    // Make the window visible
    glfwShowWindow(window!!)

    /** This line is critical for LWJGL's interoperation with GLFW's
    OpenGL context, or any context that is managed externally.
    LWJGL detects the context that is current in the current thread,
    creates the GLCapabilities instance and makes the OpenGL
    bindings available for use. */
    GL.createCapabilities()

    glViewport(0, 0, WINDOW_SIZE.first, WINDOW_SIZE.second)
    glDisable(GL_CULL_FACE)
//    glEnable(GL_CULL_FACE)
//    glCullFace(GL_BACK);
//    glCullFace(GL_BACK);
    glEnable(GL_ALPHA_TEST)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    glEnable(GL_DEPTH_TEST)

    shaderProgram = ShaderProgram("./resources/default/index.json")
    vao = glGenVertexArrays()
    glBindVertexArray(vao)
    vbo = glGenBuffers()
    vbotex = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, vbo)
    glBufferData(GL_ARRAY_BUFFER,
      floatArrayOf( // 307 _ 615 _ 188805 w h size fo char
        -.307f, -.615f, 0f,
        .307f, -.615f, 0f,
        -.307f, .615f, 0f,
        -.307f, .615f, 0f,
        .307f, -.615f, 0f,
        .307f, .615f, 0f
      ),
      GL_STATIC_DRAW)
    glVertexAttribPointer(glGetAttribLocation(shaderProgram!!.getId(), "position"), 3, GL_FLOAT, false, 0, 0)
    glBindBuffer(GL_ARRAY_BUFFER, vbotex)
    glBufferData(GL_ARRAY_BUFFER, floatArrayOf(0f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 1f, 1f, 1f, 0f), GL_STATIC_DRAW)
    glVertexAttribPointer(glGetAttribLocation(shaderProgram!!.getId(), "coor"), 2, GL_FLOAT, false, 0, 0)
    glEnableVertexAttribArray(glGetAttribLocation(shaderProgram!!.getId(), "position"))
    glEnableVertexAttribArray(glGetAttribLocation(shaderProgram!!.getId(), "coor"))
    textureId = glGenTextures().also {
      glBindTexture(GL_TEXTURE_2D, it)
    }
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texture.w, texture.h, 0, GL_RGBA, GL_UNSIGNED_BYTE, texture.tex)
    lastTexture = texture
    shaderProgram?.use()
    angleLocation = glGetUniformLocation(shaderProgram!!.getId(), "angle")
  }

  var angle = 1f
  private var lastTexture : Texture? = null
  private fun frame() {
    // Clear the framebuffer
    glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    glEnable(GL_BLEND)
    if (lastTexture != texture) {
      glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texture.w, texture.h, 0, GL_BGRA, GL_UNSIGNED_BYTE, texture.tex)
      lastTexture = texture
    }
    shaderProgram?.use()
    angle += .01f
    glBindTexture(GL_TEXTURE_2D, textureId)
    glUniform1f(angleLocation, angle)
    glBindVertexArray(vao)
    glDrawArrays(GL_TRIANGLES, 0, 6)
  }

  private fun loop() {
    // Set the clear color
    glClearColor(0f, 0f, 0f, 0f)

    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (!glfwWindowShouldClose(window!!)) {
      frame()
      // Swap the color buffers
      glfwSwapBuffers(window!!)
      // Poll for window events. The key callback above will only be
      // invoked during this call.
      glfwPollEvents()
    }

  }

  fun run() {
    try {
      init()
      loop()
      glfwDestroyWindow(window!!)
      keyCallback?.free()
      charCallback?.free()
    } finally {
      glfwTerminate()
      errorCallback?.free()

    }
  }

}