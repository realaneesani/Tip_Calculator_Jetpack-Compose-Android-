package com.helium4.tipcalculator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.helium4.tipcalculator.components.InputField
import com.helium4.tipcalculator.ui.theme.TipCalculatorTheme
import com.helium4.tipcalculator.util.calculatePerPerson
import com.helium4.tipcalculator.widgets.RoundIconButton
import com.helium4.tipcalculator.util.calculateTotalTip

class MainActivity : ComponentActivity() {

    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           MyApp {
               TipCalculator()
           }
        }
    }
}





//@Preview
@Composable
fun MyApp(content: @Composable () -> Unit) {
    TipCalculatorTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            content()
       //     TopHeader()
        }
    }
 }

@ExperimentalComposeUiApi
@Composable
fun TipCalculator() {
    Surface(modifier = Modifier
        .padding(12.dp)
        .fillMaxHeight()) {
        Column() {
            MainContent()
        }
    }
}


//@Preview
@Composable
fun TopHeader(totalPerPerson : Double =133.0){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(20.dp)
            //.clip(shape = CircleShape.copy(all = CornerSize(12.dp)))
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally
            ,verticalArrangement = Arrangement.Center) {
            Text(text = "Total Per Person",
            style = MaterialTheme.typography.h5)
            val total = "%.2f".format(totalPerPerson)
            Text(text = "$ $total",
                style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.ExtraBold)

        }
    }

}

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent(){
    val splitByState = remember {
        mutableStateOf(1)
    }
    val range = IntRange(start = 1, endInclusive = 100)

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val totalPersonState = remember {
        mutableStateOf(0.0)
    }
    BillForm(splitByState = splitByState,
        tipAmountState = tipAmountState,
        totalPersonState = totalPersonState

    ) {

    }

}

@ExperimentalComposeUiApi
@Composable

fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPersonState: MutableState<Double>,
    onValChange: (String) -> Unit = {},
) {

    val totalBill= remember {
        mutableStateOf("0")
    }


    val validSate = remember(totalBill.value) {

        totalBill.value.trim().isNotEmpty()
    }

    val sliderPosition = remember {
        mutableStateOf(0f)
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val tipPercentage  = (sliderPosition.value * 100).toInt()

    TopHeader( totalPerPerson = totalPersonState.value)

     Surface(modifier = modifier
         .padding(2.dp)
         .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp),),
        border = BorderStroke(width = 1.dp,color = Color.LightGray)
    ) {
        Column(modifier = modifier.padding(6.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start) {

            InputField(
                valueState = totalBill ,
                labelId = "Enter Amount",
                enabled = true ,
                isSingleLine = true,
                onAction = KeyboardActions{

                    if (!validSate) return@KeyboardActions

                    //Send the value back to lambda
                    onValChange(totalBill.value.trim())


                    keyboardController?.hide()
                }
                )

           if (validSate){

                Row(modifier = modifier.padding(3.dp),
                horizontalArrangement =  Arrangement.Start){
                    Text("Split",
                    modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    ))

                    Spacer(modifier = Modifier.width(120.dp))
                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                    horizontalArrangement = Arrangement.End) {


                        RoundIconButton(imageVector = Icons.Default.Remove,
                        onClick = {
                            splitByState.value =
                                if(splitByState.value>1) splitByState.value -1
                                else 1


                            totalPersonState.value =
                                calculatePerPerson(totalBill = totalBill.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage)

                        })
                        Text(text = "${splitByState.value}",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 9.dp, end = 9.dp))
                        RoundIconButton(imageVector = Icons.Default.Add,
                            onClick = {
                                    if(splitByState.value  <range.last){
                                        splitByState.value = splitByState.value + 1


                                        totalPersonState.value =
                                            calculatePerPerson(totalBill = totalBill.value.toDouble(),
                                                splitBy = splitByState.value,
                                                tipPercentage = tipPercentage)
                                    }
                            })
                    }



                }

            Row(modifier = modifier.padding(horizontal = 4.dp, vertical = 12.dp)) {

                Text(text = "Tip",
                modifier = modifier.align(Alignment.CenterVertically))
                Spacer(modifier = modifier.width(200.dp))

                    Text(text = "$ ${tipAmountState.value}",
                    modifier= modifier.align(alignment = Alignment.CenterVertically))
            }




            Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {



                Text(text = "$tipPercentage %   ")
                Spacer(modifier = modifier.height(14.dp))

                Slider(value = sliderPosition.value, onValueChange = { newVal ->

                    sliderPosition.value = newVal
                    tipAmountState.value =
                        calculateTotalTip(total=totalBill.value.toDouble(), tipPercentage = tipPercentage)
                    Log.d("Slider","SlideValue : $newVal")


                    totalPersonState.value =
                        calculatePerPerson(totalBill = totalBill.value.toDouble(),
                        splitBy = splitByState.value,
                        tipPercentage = tipPercentage)

                },
                modifier = modifier.padding(start = 16.dp, end= 16.dp),
                steps = 5,
                onValueChangeFinished = {

                })
            }

        }
        }

    }


}



@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp {
        TipCalculator()
    }
}
