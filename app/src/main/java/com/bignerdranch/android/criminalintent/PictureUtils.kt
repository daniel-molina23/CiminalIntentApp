package com.bignerdranch.android.criminalintent

import android.app.Activity
import android.content.Context.WINDOW_SERVICE
import android.graphics.*
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.Display
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.annotation.RequiresApi
import kotlin.math.roundToInt

private const val TAG = "PictureUtils"

@RequiresApi(Build.VERSION_CODES.R)
fun getScaledBitmap(path: String, activity: Activity) : Bitmap {
    // consult with page 601 of the book
    //https://developer.android.com/reference/android/content/Context#getDisplay()    ////// Deprecated
    //https://developer.android.com/reference/android/app/Activity      ///// Activity
    //look at android documentation: https://developer.android.com/reference/android/view/WindowMetrics#getBounds()    //////Only active one

    Log.i(TAG, "Using the Insets and Window Manager here for dimensions")

    val metrics = activity.windowManager.currentWindowMetrics
    val windowInsets = metrics.windowInsets
    val insets: Insets = windowInsets.getInsetsIgnoringVisibility(
                                WindowInsets.Type.displayCutout())

    val insetWidth = insets.right + insets.left
    val insetHeight = insets.bottom + insets.top

    val c = activity.window.context.display.
    // Legacy size that display#getSize reports
    //val bounds : Rect = metrics.bounds
//    val legacySize = Size(bounds.width() - insetWidth,
//                            bounds.height() - insetHeight)

    return getScaledBitmap(path, insetWidth, insetHeight)
}

fun getScaledBitmap(path : String, destWidth : Int, destHeight: Int):
        Bitmap {
    //Read in the dimension of the image on the disk
    var options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)

    val srcWidth = options.outWidth.toFloat()
    val srcHeight = options.outHeight.toFloat()

    //Figure out how much to scale down by
    var inSampleSize : Int = 1
    if (srcHeight > destHeight || srcWidth > destWidth) {
        val heightScale = srcHeight / destHeight
        val widthScale = srcWidth / destWidth

        val sampleScale = if(heightScale > widthScale){
            heightScale
        } else{
            widthScale
        }
        inSampleSize = sampleScale.roundToInt()
    }

    options = BitmapFactory.Options()
    options.inSampleSize = inSampleSize

    //Read in and create final bitmap
    return BitmapFactory.decodeFile(path, options)
}