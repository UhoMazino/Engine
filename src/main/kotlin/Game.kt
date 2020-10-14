
fun main() {
  System.setProperty("log4j.configurationFile", "E:\\JavaProject\\Engine\\resources\\log4j2.xml") // путь к конфигу log4j2
  val engine = Engine()
  engine.run()
}