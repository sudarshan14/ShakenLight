package sudarshan.bhatt.shakenlight

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class FlightModeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        Toast.makeText(context, "action changed", Toast.LENGTH_LONG).show()

    }
}
