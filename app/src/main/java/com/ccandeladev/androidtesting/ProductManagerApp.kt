package com.ccandeladev.androidtesting

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Add in Manifest android:name=".ProductManagerApp"
@HiltAndroidApp
class ProductManagerApp: Application()