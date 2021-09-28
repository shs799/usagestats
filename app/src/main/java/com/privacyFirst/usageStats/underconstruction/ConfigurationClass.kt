package com.privacyFirst.usageStats.underconstruction

import android.app.usage.ConfigurationStats
import android.app.usage.UsageStatsManager
import android.content.res.Configuration
import android.os.Build
import com.privacyFirst.usageStats.staticLib.date.DateTrans

object ConfigurationClass {
    fun a(usm: UsageStatsManager, i: Int, s: Long, e: Long) {
        val qc = usm.queryConfigurations(i, s, e)
        for (i in qc) {
            configurationStatsToString(i)
        }
    }

    fun configurationStatsToString(i: ConfigurationStats): String {
        val sb = StringBuilder()
        sb.append("firstTimeStamp: " + DateTrans.stamp(i.firstTimeStamp) + "\n")
        sb.append("lastTimeStamp: " + DateTrans.stamp(i.lastTimeStamp) + "\n")
        sb.append("lastTimeActive: " + DateTrans.stamp(i.lastTimeActive) + "\n")
        sb.append("totalTimeActive: " + DateTrans.time(i.totalTimeActive) + "\n")
        sb.append("activationCount: " + i.activationCount + "\n")

        sb.append(configurationToString(i.configuration))
        return sb.toString()
    }

    fun configurationToString(conf: Configuration): String {

        val sb = StringBuilder()

        sb.append("fontScale: " + conf.fontScale + "\n")

        sb.append("mcc: ")
        when(val mcc = conf.mcc) {
            0 -> sb.append("(Undefined)")
            else -> sb.append("$mcc\n")
        }
        sb.append("\n")

        sb.append("mnc: ")
        /*val mncsa = config.a<Configuration>("mnc")
        val ret=mncsa.get(conf.mnc)
        if(ret!=null)
            sb.append(ret)
        else*/ when (val mnc = conf.mnc) {
            Configuration.MNC_ZERO -> sb.append("ZERO")
            0 -> sb.append("(Undefined)")
            else -> sb.append("$mnc")
        }
        sb.append("\n")

        sb.append("touchscreen: ")

        when (val touchscreen = conf.touchscreen) {
            Configuration.TOUCHSCREEN_UNDEFINED -> sb.append("UNDEFINED")
            Configuration.TOUCHSCREEN_NOTOUCH -> sb.append("NOTOUCH")
            Configuration.TOUCHSCREEN_STYLUS -> sb.append("STYLUS")
            Configuration.TOUCHSCREEN_FINGER -> sb.append("FINGER")
            else -> sb.append("$touchscreen (Unknown value)")
        }
        sb.append("\n")

        sb.append("keyboard: ")
        when (val keyboard = conf.keyboard) {
            Configuration.KEYBOARD_UNDEFINED -> sb.append("UNDEFINED")
            Configuration.KEYBOARD_NOKEYS -> sb.append("NOKEYS")
            Configuration.KEYBOARD_QWERTY -> sb.append("QWERTY")
            Configuration.KEYBOARD_12KEY -> sb.append("12KEY")
            else -> sb.append("$keyboard (Unknown value)")
        }
        sb.append("\n")

        sb.append("keyboardHidden: ")
        when (val keyboardHidden = conf.keyboardHidden) {
            Configuration.HARDKEYBOARDHIDDEN_UNDEFINED -> sb.append("UNDEFINED")
            Configuration.HARDKEYBOARDHIDDEN_NO -> sb.append("NO")
            Configuration.HARDKEYBOARDHIDDEN_YES -> sb.append("YES")
            else -> sb.append("$keyboardHidden (Unknown value)")
        }
        sb.append("\n")


        sb.append("navigation: ")
        when (val navigation = conf.navigation) {
            Configuration.NAVIGATION_UNDEFINED -> sb.append("UNDEFINED")
            Configuration.NAVIGATION_NONAV -> sb.append("NONAV")
            Configuration.NAVIGATION_DPAD -> sb.append("DPAD")
            Configuration.NAVIGATION_TRACKBALL -> sb.append("TRACKBALL")
            Configuration.NAVIGATION_WHEEL -> sb.append("WHEEL")
            else -> sb.append("$navigation (Unknown value)")
        }
        sb.append("\n")

        sb.append("navigationHidden: ")
        when (val navigationHidden = conf.navigationHidden) {
            Configuration.NAVIGATIONHIDDEN_UNDEFINED -> sb.append("UNDEFINED")
            Configuration.NAVIGATIONHIDDEN_NO -> sb.append("NO")
            Configuration.NAVIGATIONHIDDEN_YES -> sb.append("YES")
            else -> sb.append("$navigationHidden (Unknown value)")
        }
        sb.append("\n")

        sb.append("orientation: ")
        when (val orientation = conf.orientation) {
            Configuration.ORIENTATION_UNDEFINED -> sb.append("UNDEFINED")
            Configuration.ORIENTATION_PORTRAIT -> sb.append("PORTRAIT")
            Configuration.ORIENTATION_LANDSCAPE -> sb.append("LANDSCAPE")
            else -> sb.append("$orientation (Unknown value)")
        }
        sb.append("\n")


        sb.append("screenLayoutSize: ")
        when (val screenLayoutSize = conf.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
            Configuration.SCREENLAYOUT_SIZE_UNDEFINED -> sb.append("UNDEFINED")
            Configuration.SCREENLAYOUT_SIZE_SMALL -> sb.append("SMALL")
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> sb.append("NORMAL")
            Configuration.SCREENLAYOUT_SIZE_LARGE -> sb.append("LARGE")
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> sb.append("XLARGE")
            else -> sb.append("$screenLayoutSize (Unknown value)")
        }
        sb.append("\n")

        sb.append("screenLayoutLong: ")
        when (val screenLayoutLong = conf.screenLayout and Configuration.SCREENLAYOUT_LONG_MASK) {
            Configuration.SCREENLAYOUT_LONG_UNDEFINED -> sb.append("UNDEFINED")
            Configuration.SCREENLAYOUT_LONG_NO -> sb.append("NO")
            Configuration.SCREENLAYOUT_LONG_YES -> sb.append("YES")
            else -> sb.append("$screenLayoutLong (Unknown value)")
        }
        sb.append("\n")

        sb.append("screenLayoutDir: ")
        when (val screenLayoutDir =
            conf.screenLayout and Configuration.SCREENLAYOUT_LAYOUTDIR_MASK) {
            Configuration.SCREENLAYOUT_LAYOUTDIR_UNDEFINED -> sb.append("UNDEFINED")
            Configuration.SCREENLAYOUT_LAYOUTDIR_LTR -> sb.append("LTR")
            Configuration.SCREENLAYOUT_LAYOUTDIR_RTL -> sb.append("RTL")
            else -> sb.append("$screenLayoutDir (Unknown value)")
        }
        sb.append("\n")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sb.append("screenLayoutRound: ")
            when (val screenLayoutRound =
                conf.screenLayout and Configuration.SCREENLAYOUT_ROUND_MASK) {
                Configuration.SCREENLAYOUT_ROUND_UNDEFINED -> sb.append("UNDEFINED")
                Configuration.SCREENLAYOUT_ROUND_NO -> sb.append("NO")
                Configuration.SCREENLAYOUT_ROUND_YES -> sb.append("YES")
                else -> sb.append("$screenLayoutRound (Unknown value)")
            }
            sb.append("\n")
        }

        sb.append("uiModeType: ")
        when (val uiModeType = conf.uiMode and Configuration.UI_MODE_TYPE_MASK) {
            Configuration.UI_MODE_TYPE_UNDEFINED -> sb.append("UNDEFINED")
            Configuration.UI_MODE_TYPE_NORMAL -> sb.append("NORMAL")
            Configuration.UI_MODE_TYPE_DESK -> sb.append("DESK")
            Configuration.UI_MODE_TYPE_CAR -> sb.append("CAR")
            Configuration.UI_MODE_TYPE_TELEVISION -> sb.append("TELEVISION")
            Configuration.UI_MODE_TYPE_APPLIANCE -> sb.append("APPLIANCE")
            Configuration.UI_MODE_TYPE_WATCH -> sb.append("WATCH")
            Configuration.UI_MODE_TYPE_VR_HEADSET -> sb.append("VR_HEADSET")
            else -> sb.append("$uiModeType (Unknown value)")
        }
        sb.append("\n")

        sb.append("uiModeNight: ")
        when (val uiModeNight = conf.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_UNDEFINED -> sb.append("UNDEFINED")
            Configuration.UI_MODE_NIGHT_NO -> sb.append("NO")
            Configuration.SCREENLAYOUT_ROUND_YES -> sb.append("YES")
            else -> sb.append("$uiModeNight (Unknown value)")
        }
        sb.append("\n")

        sb.append("screenWidthDp: ")
        when (val screenWidthDp = conf.screenWidthDp) {
            Configuration.SCREEN_WIDTH_DP_UNDEFINED -> sb.append("UNDEFINED")
            else -> sb.append(screenWidthDp)
        }
        sb.append("\n")

        sb.append("screenHeightDp: ")
        when (val screenHeightDp = conf.screenHeightDp) {
            Configuration.SCREEN_HEIGHT_DP_UNDEFINED -> sb.append("UNDEFINED")
            else -> sb.append(screenHeightDp)
        }
        sb.append("\n")

        sb.append("smallestScreenWidthDp: ")
        when (val smallestScreenWidthDp = conf.smallestScreenWidthDp) {
            Configuration.SMALLEST_SCREEN_WIDTH_DP_UNDEFINED -> sb.append("UNDEFINED")
            else -> sb.append(smallestScreenWidthDp)
        }
        sb.append("\n")

        sb.append("densityDpi: ")
        when (val densityDpi = conf.smallestScreenWidthDp) {
            Configuration.DENSITY_DPI_UNDEFINED -> sb.append("UNDEFINED")
            /*Configuration.DENSITY_DPI_ANY*/0xfffe -> sb.append("ANY")
            /*Configuration.DENSITY_DPI_NONE*/0xffff -> sb.append("NONE")
            else -> sb.append(densityDpi)
        }
        sb.append("\n")

        conf.densityDpi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            sb.append("locales: " + conf.locales.toLanguageTags())
        } else {
            sb.append("locale: " + conf.locale.toLanguageTag())
        }
        sb.append("\n")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            sb.append("colorModeWideColorGamut: ")
            when (val colorMode =
                conf.colorMode and Configuration.COLOR_MODE_WIDE_COLOR_GAMUT_MASK) {
                Configuration.COLOR_MODE_WIDE_COLOR_GAMUT_UNDEFINED -> sb.append("UNDEFINED")
                Configuration.COLOR_MODE_WIDE_COLOR_GAMUT_NO -> sb.append("NO")
                Configuration.COLOR_MODE_WIDE_COLOR_GAMUT_YES -> sb.append("YES")
                else -> sb.append(colorMode)
            }
            sb.append("\n")

            sb.append("colorModeHDR: ")
            when (val colorMode = conf.colorMode and Configuration.COLOR_MODE_HDR_MASK) {
                Configuration.COLOR_MODE_HDR_UNDEFINED -> sb.append("UNDEFINED")
                Configuration.COLOR_MODE_HDR_NO -> sb.append("NO")
                Configuration.COLOR_MODE_HDR_YES -> sb.append("YES")
                else -> sb.append(colorMode)
            }
            sb.append("\n")

        }
        return sb.toString()
    }

}