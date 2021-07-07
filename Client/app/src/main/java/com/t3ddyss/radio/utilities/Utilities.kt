package com.t3ddyss.radio.utilities

import android.os.Build

private fun isEmulator() = Build.FINGERPRINT.contains("generic")

fun getBaseUrlForCurrentDevice() = if (isEmulator()) BASE_URL_EMULATOR else BASE_URL_DEVICE