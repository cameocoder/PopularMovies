/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cameocoder.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

public class TestMovieContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final long TEST_MOVIE_ID = 12345;

    public void testBuildMovieId() {
        Uri movieUri = MovieContract.MovieEntry.buildMovieWithId(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned",
                movieUri);
        assertEquals("Error: Movie not properly appended to the end of the Uri",
                String.valueOf(TEST_MOVIE_ID), movieUri.getLastPathSegment());
        assertEquals("Error: Movie Uri doesn't match",
                movieUri.toString(),
                "content://com.cameocoder.popularmovies.app/movies/12345");
    }
}
