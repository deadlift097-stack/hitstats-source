package hitstats;

import org.modhost.Menu;
import org.modhost.Mod;
import org.modhost.ModContext;

/**
 * HitStats: an attack counter and attacks-per-second logger for Mod Host.
 * Not a hit-registration mod: the Mod Host API cannot see entities, damage or sounds.
 */
public class HitStatsMod implements Mod {
    private static final int MAX_WINDOW = 60;

    private ModContext ctx;
    private final long[] stamps = new long[4096];
    private int head = 0, size = 0;
    private long total = 0;
    private int tickCount = 0;

    @Override
    public void onLoad(ModContext context) throws Exception {
        this.ctx = context;

        Menu menu = new Menu("HitStats")
            .description("Counts your attacks and logs attacks per second")
            .toggle("enabled", "Enable counter", true)
            .toggle("logEachAttack", "Log every attack", false)
            .slider("windowSec", "Window (seconds)", 5, 1, MAX_WINDOW)
            .slider("reportSec", "Report every (seconds)", 5, 1, 60);
        context.registerMenu(menu);

        context.onAttack(now -> {
            if (!context.settings().getBoolean("enabled")) return;
            long t = System.nanoTime();
            stamps[head] = t;
            head = (head + 1) % stamps.length;
            if (size < stamps.length) size++;
            total++;
            if (context.settings().getBoolean("logEachAttack")) {
                context.log("attack #" + total);
            }
        });

        context.onTick(now -> {
            if (!context.settings().getBoolean("enabled")) return;
            tickCount++;
            int everyTicks = Math.max(1, context.settings().getInt("reportSec")) * 20;
            if (tickCount % everyTicks != 0) return;

            int windowSec = Math.max(1, context.settings().getInt("windowSec"));
            long cutoff = System.nanoTime() - windowSec * 1_000_000_000L;
            int inWindow = 0;
            for (int i = 0; i < size; i++) {
                int idx = (head - 1 - i + stamps.length) % stamps.length;
                if (stamps[idx] >= cutoff) inWindow++; else break;
            }
            double aps = inWindow / (double) windowSec;
            context.log(String.format("HitStats: %d attacks total, %.2f/s over last %ds",
                total, aps, windowSec));
        });

        context.log("HitStats loaded");
    }

    @Override
    public void onUnload() {
        if (ctx != null) ctx.log("HitStats unloaded (" + total + " attacks this session)");
    }
}
