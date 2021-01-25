
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mvialpha.models.AccountProperties
import com.example.mvialpha.models.AuthToken

@Database(entities = [AuthToken::class, AccountProperties::class], version = 1)
abstract class AppDatabase: RoomDatabase(){

    public abstract fun getAuthTokenDao(): AuthTokenDao
    public abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    companion object{
        const val DATABASE_NAME = "app_db"
    }
}