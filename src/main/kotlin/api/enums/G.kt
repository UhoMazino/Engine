package api.enums

import org.lwjgl.opengl.GL46C.*

enum class G(val value: Int) {
  VERTEX(GL_VERTEX_SHADER),
  FRAGMENT(GL_FRAGMENT_SHADER)
}
