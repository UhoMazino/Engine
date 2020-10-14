
fun main() {
  System.setProperty("log4j.configurationFile", ".\\resources\\log4j2.xml") // путь к конфигу log4j2
  val engine = Engine()
  engine.run()
}