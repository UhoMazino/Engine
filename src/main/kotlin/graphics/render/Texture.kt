package graphics.render

import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints.*
import java.awt.font.FontRenderContext
import java.awt.font.TextAttribute
import java.awt.font.TextAttribute.*
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage


class Texture(c: Char) {

  init {
    genTexture(c)
  }

  var w = 0
  var h = 0
  var tex: IntArray? = null


  private fun genTexture(c: Char) {
    val sampleText = c.toString()
    var font = Font("Fira Code", Font.PLAIN, 500)
    val attr = HashMap<TextAttribute, Any>()
    attr[LIGATURES] = LIGATURES_ON
    font = font.deriveFont(attr)
    val frc = FontRenderContext(null, true, true)
    val bounds: Rectangle2D = font.getStringBounds(sampleText, frc)
    w = bounds.width.toInt()
    h = bounds.height.toInt()
    val image = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
    val g = image.createGraphics()
    g.setRenderingHint(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY)
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON)
    g.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON)
    g.setRenderingHint(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY)
    g.setRenderingHint(KEY_DITHERING, VALUE_DITHER_ENABLE)
    g.setRenderingHint(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON)
    g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR)
    g.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY)
    g.setRenderingHint(KEY_STROKE_CONTROL, VALUE_STROKE_PURE)
    g.color = Color(0, true)
    g.fillRect(0, 0, w, h)
    g.color = Color.WHITE
    g.font = font
    g.drawString(sampleText, bounds.x.toFloat(), (-bounds.y).toFloat())
    g.dispose()
    tex = IntArray(w * h)
    image.getRGB(0, 0, w, h, tex, 0, w)
  }
}