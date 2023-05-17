package edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.root;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.dao.StockDAO;
import edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.dao.UserDAO;
import edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.entities.StockEntity;
import edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.entities.UserEntity;

@Database(entities = {UserEntity.class, StockEntity.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract StockDAO stockDAO();
    public abstract UserDAO userDAO();

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static volatile UserDatabase INSTANCE;

    public static UserDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    UserDatabase.class, "app_database").allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
