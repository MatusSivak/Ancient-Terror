package sk.sivak.eldritchhorror.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;

/**
 * Runs in a separate process, kills the main game process and relaunches the game, so the
 * game always starts with clean static state and a fresh GL context.
 */
public class ProcessRestartActivity extends Activity {

    private static final String EXTRA_MAIN_PID = "main_pid";

    static void restart(Activity from) {
        Intent intent = new Intent(from, ProcessRestartActivity.class);
        intent.putExtra(EXTRA_MAIN_PID, Process.myPid());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
        from.finish();
        from.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mainPid = getIntent().getIntExtra(EXTRA_MAIN_PID, -1);
        if (mainPid > 0) {
            Process.killProcess(mainPid);
        }
        Intent launch = new Intent(this, GameActivity.class);
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(launch);
        finish();
        overridePendingTransition(0, 0);
        Runtime.getRuntime().exit(0);
    }
}
