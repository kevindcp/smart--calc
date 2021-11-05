package calculator
import java.math.BigInteger

fun defineOperator( operatorString: String ): String {
    var isValid = operatorString.toIntOrNull()
    when{
        isValid != null -> return "-1"
    }
    when{
        operatorString.length % 2 == 0 -> return "+"
        operatorString == "=" -> return "-2"
        else -> return "-"
    }
}

fun defineLimits ( expression: List<String>, start: Int) : Int {
    var need = 0
    var end = start
    for ( i in start..expression.size ) {
        if (expression[i] == ")") {
            need -= 1
        }
        if (expression[i] == "("){
            need += 1
        }
        end = i
        if (need == 0) return end
    }
    return -1
}

fun evaluate( expressionList: List<String> ) : String {
    try {
        var i = 0
        var expression = expressionList.toMutableList()
        var size = expression.size
        var flag = 0
        while (i < size ) {
            if (expression[i] == "(") {
                flag = 1
                var subexpinicio = i
                var subexpfin = defineLimits(expression, subexpinicio)
                if ( subexpfin == -1 ){
                    throw IllegalArgumentException("Operator not valid")
                } else {
                    var subexp = expression.subList(subexpinicio+1, subexpfin).toMutableList()
                    var eval = evaluate(subexp)
                    var z = subexpinicio
                    while (z < subexpfin) {
                        expression.removeAt(subexpinicio)
                        z += 1
                        size -=1
                    }
                    expression[subexpinicio] = eval
                }
                i = 0
            } else if (expression[i] == ")" && flag == 0){
                throw IllegalArgumentException("Operator not valid")
            }else i++
        }
        i = 0
        size = expression.size
        while ( i < size - 2 ) {
            var op1 = expression[i].toBigInteger()
            var op2 = expression[i + 2].toBigInteger()
            var operator = expression[i + 1]

            when(operator) {
                "*" -> {
                    expression[i+2] = (op1 * op2).toString()
                    expression.removeAt(i)
                    expression.removeAt(i)
                    size -= 2
                    i = 0
                }
                "/" -> {
                    expression[i+2] = (op1 / op2).toString()
                    expression.removeAt(i)
                    expression.removeAt(i)
                    size -= 2
                    i = 0
                }
                else -> i += 2
            }
        }

        i = 0
        while ( i < size - 2 ) {
            var op1 = expression[i].toBigInteger()
            var op2 = expression[i + 2].toBigInteger()
            var operator = expression[i + 1]
            if( operator.length > 1 ) {
                operator = defineOperator(operator)
            }
            when(operator) {
                "+" -> expression[i+2] = (op1 + op2).toString()
                "-" -> expression[i+2] = (op1 - op2).toString()
                else -> throw IllegalArgumentException("Operator not valid")
            }
            i += 2
        }
        return expression.last()
    } catch (e : Exception) {
        return "Invalid expression"
    }
}

fun isLetters(string: String): Boolean {
    return string.all { it in 'A'..'Z' || it in 'a'..'z' }
}

fun isNumbers(string: String): Boolean {
    return string.all { it in '0'..'9' }
}

fun main() {
    var variables = mutableMapOf<String, BigInteger>()
    loop@ while(true) {
        var input = readLine()!!.replace("\\=".toRegex(), "eq=eq").replace("\\(".toRegex(), "(par").replace("\\)".toRegex(), "par)").replace("\\++".toRegex(), "plus+plus").replace("(?<!-)-(--)*(?!-|\\d)".toRegex(), "minus-minus").replace("\\--+".toRegex(), "plus+plus").replace("\\*".toRegex(), "mul*mul").replace("\\/(?!\\d|\\w)".toRegex(), "div/div").replace("\\s".toRegex(), "")
        var expression = input.split("plus|minus|eq|mul|div|par".toRegex()).toMutableList()
        var eval: String = ""
        when {
            expression[0] == "/help" -> {
                println("sdads")
                continue
            }
            expression[0] == "/exit" -> break
            expression[0].startsWith('/') -> {
                println("Unknown Command")
                continue
            }
            expression.size == 1 && expression[0] == "" -> continue
            expression.size == 1 && isLetters(expression[0]) -> {
                when {
                    variables.containsKey(expression[0]) -> {
                        println(variables[expression[0]])
                        continue
                    }
                    else -> {
                        println("Unknown variable")
                        continue
                    }
                }
            }
            expression.size == 3 && expression[1] == "=" -> {
                when {
                    isLetters(expression[0]) && !isLetters(expression[2]) -> {
                        try {
                            variables[expression[0]] = expression[2].toBigInteger()
                            continue
                        } catch (e: Exception){
                            println("Invalid expression")
                            continue
                        }
                    }
                    isLetters(expression[0]) && isLetters(expression[2]) -> {
                        when  {
                            variables.containsKey(expression[2]) ->{
                                variables[expression[0]] = variables[expression[2]]!!
                                continue
                            }
                            else -> {
                                println("Invalid assignment")
                                continue
                            }
                        }
                    }
                    else -> {
                        println("Invalid identifier")
                        continue
                    }
                }
            }
            expression.size == 3 -> {
                println(evaluate(expression))
                continue
            }
            expression.size > 3 -> {
                for (i in expression.indices) {
                    if ( i > 1 && expression[i] == "="){
                        println("Invalid assignment")
                        continue@loop
                    }
                }
                when {
                    expression[1] == "=" -> when {
                        isLetters(expression[0]) -> {
                            variables[expression[0]] = evaluate(expression.subList(2, expression.size - 1)).toBigInteger()
                            continue
                        }
                        else -> continue
                    }
                    else->{
                        for (i in expression.indices) {
                            if (isLetters(expression[i])) {
                                when {
                                    variables.containsKey(expression[i]) -> expression[i] = variables[expression[i]].toString()
                                    else -> {
                                        continue
                                    }
                                }
                            }
                        }
                        println(evaluate(expression))
                        continue
                    }
                }
            }
            else -> println(expression)

        }
        break
    }
    println("Bye")
}
