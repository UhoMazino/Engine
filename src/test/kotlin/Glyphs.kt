package test

import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.font.FontRenderContext
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


class Texture {
  var w = 0
  var h = 0
  fun getTexture(): IntArray {
    val sampleText = "<~ === FIRA КИРИЛИЦА <==> CODE === ~>"
    val font = Font("Fira Code", Font.PLAIN, 50)
    val frc = FontRenderContext(null, true, true)
    val bounds: Rectangle2D = font.getStringBounds(sampleText, frc)
    val w = bounds.width.toInt().also { this.w = w }
    val h = bounds.height.toInt().also { this.h = h }
    val image = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
    val g = image.createGraphics()
    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
    g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE)
    g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
    g.color = Color(0, true)
    g.fillRect(0, 0, w, h)
    g.color = Color.WHITE
    g.font = font
    g.drawString(sampleText, bounds.x.toFloat(), (-bounds.y).toFloat())
    g.dispose()
    val pixels = IntArray(w * h)
    image.getRGB(0, 0, w, h, pixels, 0, w)
    return pixels
  }
}