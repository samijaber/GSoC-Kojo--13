object Wrapper {
  def main(args: Array[String]) {
  	println(factorial(10))
  }
  def factorial(n: Int): Int = if (n == 0) 1 else n * factorial(n-1)
  factorial(5)
}