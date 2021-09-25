/*
    Copyright (C) 2021 Wong Wei Lok <wongweilok@disroot.org>

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

package com.weilok.rssocto.services

import android.util.Xml
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import okhttp3.*
import org.xmlpull.v1.XmlPullParser
import java.io.IOException
import java.io.StringReader

class Validator {
    fun validate(
        url: String,
        client: OkHttpClient,
        urlValid: MutableLiveData<String>,
        feedType: MutableLiveData<String>,
        isUrlValid: ObservableBoolean
    ) {
        val parser: XmlPullParser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)

        try {
            // Create request with user input URL
            val request = Request
                .Builder()
                .url(url)
                .build()

            // Create call and get its content
            client.newCall(request).enqueue(object: Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        feedType.postValue("")
                        urlValid.postValue("")
                        urlValid.postValue("Invalid URL.")
                        isUrlValid.set(false)
                        response.close()
                        throw IOException("Unexpected code $response")
                    }

                    val body = response.body()?.string()
                    parser.setInput(StringReader(body))
                    response.close()

                    // Parse the raw XML content and get feed type
                    val fType: String? = Parser().getFeedType(parser)
                    if (fType != null) {
                        feedType.postValue("")
                        feedType.postValue(fType)
                        urlValid.postValue("")
                        isUrlValid.set(true)
                    } else {
                        feedType.postValue("")
                        urlValid.postValue("")
                        urlValid.postValue("Invalid feed format.")
                        isUrlValid.set(false)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    feedType.postValue("")
                    urlValid.postValue("")
                    urlValid.postValue("Invalid URL.")
                    isUrlValid.set(false)
                }
            })
        } catch (e: IllegalArgumentException) {
            feedType.postValue("")
            urlValid.postValue("")
            urlValid.postValue("Invalid URL.")
            isUrlValid.set(false)
        }
    }
}