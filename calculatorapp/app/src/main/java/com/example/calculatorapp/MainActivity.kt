package com.example.calculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculatorapp.ui.theme.CalculatorappTheme
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorappTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Calculator()
                }
            }
        }
    }
}

@Composable
fun Calculator() {
    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = input, fontSize = 34.sp, modifier = Modifier.padding(bottom = 4.dp))
            Text(text = output, fontSize = 26.sp, color = MaterialTheme.colorScheme.primary)
        }

        val keys = listOf(
            listOf("7", "8", "9", "÷"),
            listOf("4", "5", "6", "×"),
            listOf("1", "2", "3", "-"),
            listOf("0", ".", "=", "+")
        )

        keys.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEach { key ->
                    Button(
                        onClick = {
                            when (key) {
                                "=" -> {
                                    val expr = input.replace('×', '*').replace('÷', '/')
                                    output = try {
                                        "= ${compute(expr)}"
                                    } catch (e: Exception) {
                                        "Err"
                                    }
                                }
                                else -> input += key
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(text = key, fontSize = 20.sp)
                    }
                }
            }
        }

        Button(
            onClick = {
                input = ""
                output = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Clear")
        }
    }
}

fun compute(expr: String): String {
    val rhino = Context.enter()
    rhino.optimizationLevel = -1
    return try {
        val scope: Scriptable = rhino.initStandardObjects()
        val result = rhino.evaluateString(scope, expr, "calc", 1, null)
        result.toString()
    } finally {
        Context.exit()
    }
}