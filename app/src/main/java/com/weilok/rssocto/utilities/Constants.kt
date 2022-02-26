/*
    Copyright (C) 2021-2022 Wong Wei Lok <wongweilok@disroot.org>

    This file is part of RSSOcto

    RSSOcto is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    RSSOcto is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this RSSOcto.  If not, see <https://www.gnu.org/licenses/>.
*/

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