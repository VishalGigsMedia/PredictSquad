package com.predict_squad.common_helper

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.predict_squad.common_helper.ConstantHelper.dateFormat
import com.predict_squad.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CustomRunnable(var handler: Handler, var holder: TextView, var endDate: String, val context: Context) : Runnable {
    //var millisUntilFinished: Long = 40000
    @SuppressLint("SimpleDateFormat")
    override fun run() {
        val formatter = SimpleDateFormat(dateFormat)
        formatter.isLenient = false
        val curDate = Date()
        /*  val curMillis = curDate.time
          val curTime = formatter.format(curDate)*/
        val startDate = formatter.format(curDate)

        /*val seconds = millisUntilFinished / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val time = ("Time Left " + hours % 24 + ":" + minutes % 60 + ":" + seconds % 60)
        //val time = days.toString() + " " + "days" + " :" + hours % 24 + ":" + minutes % 60 + ":" + seconds % 60
        holder.text = time
        millisUntilFinished -= 1000
        Log.d("DEV123", time)
        handler.postDelayed(this, 1000)*/


        //val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

        try {
            // parse method is used to parse
            // the text from a string to
            // produce the date
            val d1 = formatter.parse(startDate)
            val d2 = formatter.parse(endDate)

            // Calculate time difference
            // in milliseconds
            val differenceInTime = d2.time - d1.time

            // Calculate time difference in
            // seconds, minutes, hours, years,
            // and days
            var differenceInSeconds = ((differenceInTime / 1000) % 60)
            var differenceInMinutes = ((differenceInTime / (1000 * 60)) % 60)
            var differenceInHours = ((differenceInTime / (1000 * 60 * 60)) % 24)
            val differenceInYears = (differenceInTime / (1000L * 60 * 60 * 24 * 365))
            val differenceInDays = ((differenceInTime / (1000 * 60 * 60 * 24)) % 365)

            // Print the date difference in
            // years, in days, in hours, in
            // minutes, and in seconds
            //print("Difference " + "between two dates is: ")
            /*println(
                (differenceInYears.toString() + " years, "
                        + differenceInDays
                        + " days, "
                        + differenceInHours
                        + " hours, "
                        + differenceInMinutes
                        + " minutes, "
                        + differenceInSeconds
                        + " seconds")
            )*/

            if (differenceInHours.toInt() == 0 && differenceInMinutes.toInt() == 0 && differenceInSeconds.toInt() == 0) {
                handler.removeCallbacksAndMessages(true)
                //millisUntilFinished = 0
                holder.text = context.getString(R.string.match_ongoing)
                holder.setTextColor(ContextCompat.getColor(context, R.color.greenColor))

            } else {
                var hrs = ""
                var minutes = ""
                var second = ""
                hrs = if (differenceInHours < 10) {
                    "0$differenceInHours"
                } else {
                    differenceInHours.toString()
                }
                minutes = if (differenceInMinutes < 10) {
                    "0$differenceInMinutes"
                } else {
                    differenceInMinutes.toString()
                }
                second = if (differenceInSeconds < 10) {
                    "0$differenceInSeconds"
                } else {
                    differenceInSeconds.toString()
                }


                //val timeLeft = "$differenceInHours : $differenceInMinutes : $differenceInSeconds"
                val timeLeft = "$hrs : $minutes : $second"
                holder.text = timeLeft
                //millisUntilFinished -= 1000
                handler.postDelayed(this, 100)
            }

        } // Catch the Exception
        catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    /*init {
        this.millisUntilFinished = millisUntilFinished
    }*/
}