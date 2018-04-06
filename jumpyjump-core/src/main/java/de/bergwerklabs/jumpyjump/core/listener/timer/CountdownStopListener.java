package de.bergwerklabs.jumpyjump.core.listener.timer;

import de.bergwerklabs.framework.commons.spigot.general.timer.LabsTimer;
import de.bergwerklabs.framework.commons.spigot.general.timer.event.LabsTimerStopEvent;
import de.bergwerklabs.jumpyjump.api.JumpyJumpPlayer;
import de.bergwerklabs.jumpyjump.core.Common;
import de.bergwerklabs.jumpyjump.core.DisplayFailsTask;
import de.bergwerklabs.jumpyjump.core.JumpyJumpSession;
import de.bergwerklabs.jumpyjump.core.listener.JumpyJumpListener;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by Yannic Rieger on 05.04.2018.
 * <p>
 *
 * @author Yannic Rieger
 */
public class CountdownStopListener extends JumpyJumpListener implements Consumer<LabsTimerStopEvent> {

    private DisplayFailsTask displayFailsTask;
    private LabsTimer timer;
    private Collection<JumpyJumpPlayer> players;
    private Scoreboard scoreboard;

    public CountdownStopListener(JumpyJumpSession session, int duration, Scoreboard scoreboard) {
        super(session);
        this.players = this.session.getRegistry().getPlayers().values();
        this.displayFailsTask = new DisplayFailsTask(JumpyJumpSession.getInstance());
        this.scoreboard = scoreboard;
        this.timer = new LabsTimer(duration, timeLeft -> {
            String timeString = String.format("§b%02d:%02d", timeLeft / 60, timeLeft % 60);
            players.stream().filter(p -> p.getPlayer() != null).forEach(player -> {
                final Player spigotPlayer = player.getPlayer();
                spigotPlayer.getScoreboard().getObjective("distance").setDisplayName("§6>> §eJumpyJump §6❘ " + timeString);
                if (((float)timeLeft / 60F) % 10 == 0) {
                    this.game.getMessenger().message("Noch " + timeString + " §7Minuten.", spigotPlayer);
                    spigotPlayer.playSound(spigotPlayer.getEyeLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                }
                else if (timeLeft <= 5) {
                    this.game.getMessenger().message("§b" + String.valueOf(timeLeft), spigotPlayer);
                    spigotPlayer.playSound(spigotPlayer.getEyeLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                }
            });
        });
        this.timer.addStopListener(new RoundTimerStopListener(this.session));
    }

    @Override
    public void accept(LabsTimerStopEvent labsTimerStopEvent) {
        // Make players see each other again.
        players.forEach(player -> {
            final Player playerObject = player.getPlayer();
            players.forEach(p2 -> playerObject.showPlayer(p2.getPlayer()));
            player.unfreeze();
            playerObject.playSound(playerObject.getEyeLocation(), Sound.COW_HURT, 100, 1);
            Common.createAndSendTitle(playerObject, "§bLOS!", "");
            player.getPlayer().setScoreboard(this.scoreboard);
            this.timer.start();
        });
        Bukkit.getScheduler().runTaskTimerAsynchronously(JumpyJumpSession.getInstance(), this.displayFailsTask, 0L, 20L);
    }
}