// DatabaseHelper.kt
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.habit_tracker_3.Habit
import com.example.habit_tracker_3.User
import java.io.ByteArrayOutputStream

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "HabitTracker.db"
        private const val DATABASE_VERSION = 1

        // Tables
        private const val TABLE_USERS = "users"
        private const val TABLE_HABITS = "habits"

        // Users table columns
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_GENDER = "gender"
        private const val COLUMN_PROFILE_PIC = "profile_pic"

        // Habits table columns
        private const val COLUMN_HABIT_ID = "habit_id"
        private const val COLUMN_HABIT_NAME = "habit_name"
        private const val COLUMN_STREAK = "streak"
        private const val COLUMN_HEALTH = "health"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS(
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE,
                $COLUMN_EMAIL TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_GENDER TEXT,
                $COLUMN_PROFILE_PIC BLOB
            )
        """.trimIndent()

        val createHabitsTable = """
            CREATE TABLE $TABLE_HABITS(
                $COLUMN_HABIT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_HABIT_NAME TEXT,
                $COLUMN_STREAK INTEGER DEFAULT 0,
                $COLUMN_HEALTH REAL DEFAULT 1.0,
                $COLUMN_USER_ID INTEGER,
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createHabitsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HABITS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // USER METHODS - For LoginFragment and SignUpFragment

    fun getUserByEmail(email: String): User? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))

        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                gender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                profilePic = getProfilePicFromCursor(cursor)
            )
            cursor.close()
            user
        } else {
            null
        }
    }

    fun addUser(user: User): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, user.username)
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_PASSWORD, user.password)
            put(COLUMN_GENDER, user.gender)
            // ADD PROFILE PICTURE TO DATABASE
            if (user.profilePic != null) {
                put(COLUMN_PROFILE_PIC, convertBitmapToByteArray(user.profilePic))
            }
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun saveCurrentUser(user: User, context: Context) {
        val sharedPref = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("user_id", user.id)
            putString("username", user.username)
            putString("email", user.email)
            putString("gender", user.gender)
            apply()
        }
    }

    fun getCurrentUser(context: Context): User? {
        val sharedPref = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)

        if (userId == -1) return null

        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                password = "", // Don't return password for security
                gender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                profilePic = getProfilePicFromCursor(cursor) // FIXED: Load profile picture
            )
            cursor.close()
            user
        } else {
            null
        }
    }

    // HABIT METHODS - For all fragments

    fun getHabits(userId: Int): MutableList<Habit> {
        val habitList = mutableListOf<Habit>()
        val db = readableDatabase
        val query =
            "SELECT * FROM $TABLE_HABITS WHERE $COLUMN_USER_ID = ? ORDER BY $COLUMN_CREATED_AT DESC"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        while (cursor.moveToNext()) {
            val habit = Habit(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_HABIT_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HABIT_NAME)),
                streak = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STREAK)),
                health = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_HEALTH))
            )
            habitList.add(habit)
        }
        cursor.close()
        return habitList
    }

    fun addHabit(habit: Habit, userId: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_HABIT_NAME, habit.name)
            put(COLUMN_STREAK, habit.streak)
            put(COLUMN_HEALTH, habit.health)
            put(COLUMN_USER_ID, userId)
        }
        return db.insert(TABLE_HABITS, null, values)
    }

    fun updateHabit(habit: Habit): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_HABIT_NAME, habit.name)
            put(COLUMN_STREAK, habit.streak)
            put(COLUMN_HEALTH, habit.health)
        }
        return db.update(TABLE_HABITS, values, "$COLUMN_HABIT_ID = ?", arrayOf(habit.id.toString()))
    }

    fun deleteHabit(habitId: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_HABITS, "$COLUMN_HABIT_ID = ?", arrayOf(habitId.toString()))
    }

    // LOGOUT METHOD - For SettingsFragment
    fun logout(context: Context) {
        val sharedPref = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }

    // Utility methods
    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun convertByteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    // NEW METHOD: Extract profile picture from cursor
    private fun getProfilePicFromCursor(cursor: Cursor): Bitmap? {
        return try {
            val profilePicIndex = cursor.getColumnIndex(COLUMN_PROFILE_PIC)
            if (profilePicIndex >= 0) {
                val profilePicBytes = cursor.getBlob(profilePicIndex)
                if (profilePicBytes != null && profilePicBytes.isNotEmpty()) {
                    convertByteArrayToBitmap(profilePicBytes)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Add this to your DatabaseHelper class
    // Add these methods to your DatabaseHelper class

    fun updateUserProfilePic(userId: Int, profilePic: Bitmap?): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            if (profilePic != null) {
                put(COLUMN_PROFILE_PIC, convertBitmapToByteArray(profilePic))
            } else {
                putNull(COLUMN_PROFILE_PIC)
            }
        }

        val result = db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        return result > 0
    }

    fun updateUserData(userId: Int, username: String, gender: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_GENDER, gender)
        }

        val result = db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        return result > 0
    }
}