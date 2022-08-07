package com.helium4.tipcalculator.util




    fun calculateTotalTip(total: Double, tipPercentage: Int): Double {


        return if (total>1 &&
            total.toString().isNotEmpty())
            (total*tipPercentage)/100 else 0.0

    }

fun calculatePerPerson(
    totalBill:Double,
    splitBy: Int,
    tipPercentage: Int):Double{
    val bill = calculateTotalTip(total = totalBill  , tipPercentage = tipPercentage) + totalBill
    return ( bill / splitBy)
}