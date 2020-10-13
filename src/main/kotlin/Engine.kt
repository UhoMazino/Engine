import graphics.render.ShaderProgram
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL46C.*
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.FloatBuffer

class Engine {
  companion object {

    val WINDOW_SIZE = Pair(800, 600)

  }

  private var errorCallback: GLFWErrorCallback? = null
  private var keyCallback: GLFWKeyCallback? = null
  private var shaderProgram: ShaderProgram? = null
  private var window: Long? = null
  private var vbo: Int = 0
  private var vao: Int = 0
  private var angleLocation : Int = 0

  private fun init() {
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

    /*glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
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

    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    GL.createCapabilities()

    //GL11.glViewport(0, 0, 800, 600)
    glDisable(GL_CULL_FACE)
    //glCullFace(GL_BACK);
    //glCullFace(GL_BACK);
    //GL11.glEnable(GL11.GL_ALPHA_TEST)
    //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    //GL11.glEnable(GL11.GL_DEPTH_TEST)

    shaderProgram = ShaderProgram("./resources/default/index.json")
    vao = glGenVertexArrays()
    glBindVertexArray(vao)
    vbo = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, vbo)
    glBufferData(GL_ARRAY_BUFFER, floatArrayOf(-0.9f, -0.9f, 0.4f, 0f, 0.9f, -0.4f,  0.9f, -0.9f, 0.4f), GL_STATIC_DRAW)
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
    glEnableVertexAttribArray(0)

    shaderProgram?.use()
    angleLocation = glGetUniformLocation( shaderProgram!!.getId(), "angle");

  }

  var angle = 0f
  private fun frame() {
    // Clear the framebuffer
    glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
    shaderProgram?.use()
    angle += 0.01f
    glUniform1f(angleLocation, angle)
    glBindVertexArray(vao)
    glDrawArrays(GL_TRIANGLES, 0, 3)
  }

  private fun loop() {

    // Set the clear color
    glClearColor(0.1f, 0.0f, 0.1f, 0.0f)

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
    } finally {

      glfwTerminate()
      errorCallback?.free()

    }
  }

}