package com.weilok.rssocto.utilities

import android.app.Activity

// Feed types
const val TYPE_RSS = "RSS"
const val TYPE_ATOM = "ATOM"

// Feed date format
const val ATOM_DATE_FMT = "yyyy-MM-dd'T'HH:mm:ssZ"
const val RSS_DATE_FMT = "EEE, dd MMM yyyy HH:mm:ss zzz"

// Others
const val ADD_FEED_RESULT_OK = Activity.RESULT_FIRST_USER
const val RESULT_CODE = Activity.RESULT_FIRST_USER + 7