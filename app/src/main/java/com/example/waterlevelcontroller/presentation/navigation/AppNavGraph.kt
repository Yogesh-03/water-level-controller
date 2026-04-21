import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.waterlevelcontroller.presentation.ui.screens.dashboard.DashboardScreen
import com.example.waterlevelcontroller.presentation.ui.screens.schedule.ScheduleScreen

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    NavHost(navController, startDestination = "dashboard") {
        composable("dashboard") {
//            DashboardScreen()
            ScheduleScreen()
        }
    }
}