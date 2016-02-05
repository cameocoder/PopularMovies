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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Error: Database does not contain all expected tables",
                tableNameHashSet.isEmpty());

        // do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_ADULT);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_GENRE_IDS);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POPULARITY);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VIDEO);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_COUNT);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required columns",
                movieColumnHashSet.isEmpty());
        db.close();
    }

    public void testMovieTable() {
        insertMovie();
    }

    public long insertMovie() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createMovieValues();

        long rowId;
        rowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(rowId != -1);

        Cursor cursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

//        // Move the cursor to a valid database row and check to see if we got any records back
//        // from the query
        assertTrue( "Error: No Records returned from query", cursor.moveToFirst() );

//        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
//        // (you can use the validateCurrentRecord function in TestUtilities to validate the
//        // query if you like)
        validateCurrentRecord("Error: Query Validation Failed", cursor, testValues);
//
//        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );
//
//        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return rowId;
    }

    static ContentValues createMovieValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieEntry.COLUMN_ID, 12345);
        testValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Littlest Hobo");
        testValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Lost German Sheppard");
        testValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "Littlest Hobo is a TV show ...");
        testValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "1984-10-17");
        testValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/poster.jpg");
        testValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, "/backdrop.jpg");
        testValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, "en");
        testValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, false);
        testValues.put(MovieContract.MovieEntry.COLUMN_GENRE_IDS, "[18,35]");
        testValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 1.2);
        testValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 2.1);
        testValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, 321);
        testValues.put(MovieContract.MovieEntry.COLUMN_ADULT, true);

        return testValues;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue;
            String actualValue = valueCursor.getString(idx);
            if (entry.getValue() instanceof Boolean) {
                expectedValue = (boolean) entry.getValue() ? "1" : "0";
            } else {
                expectedValue = entry.getValue().toString();
            }
            assertEquals("Value '" + actualValue +
                        "' did not match the expected value '" +
                        expectedValue + "'. " + error, expectedValue, actualValue);

        }
    }

}
