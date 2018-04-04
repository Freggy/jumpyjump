package de.bergwerklabs.jumpyjump.core;

import de.bergwerklabs.framework.bedrock.api.LabsGame;
import de.bergwerklabs.framework.bedrock.api.PlayerRegistry;
import de.bergwerklabs.framework.bedrock.api.event.session.SessionDonePreparationEvent;
import de.bergwerklabs.framework.bedrock.api.session.MinigameSession;
import de.bergwerklabs.jumpyjump.api.JumpyJumpPlayer;
import de.bergwerklabs.jumpyjump.core.listener.PlayerInteractListener;
import de.bergwerklabs.jumpyjump.core.listener.PlayerMoveListener;
import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * Created by Yannic Rieger on 02.04.2018.
 * <p>
 * Main class for the JumpyJump game.
 *
 * @author Yannic Rieger
 */
public class JumpyJumpSession extends MinigameSession<JumpyJumpPlayer> {

    public static JumpyJumpSession getInstance() {
        return instance;
    }

    /**
     * Gets the {@link MapManager}.
     */
    public MapManager getMapManager() {
        return mapManager;
    }

    public LabsGame getGame() {
        return new JumpyJump();
    }


    private static JumpyJumpSession instance;
    private PlayerRegistry<JumpyJumpPlayer> registry;
    private MapManager mapManager;

    public void prepare() {
        instance = this;
        this.mapManager = new MapManager(this);
        this.mapManager.loadMap();
        Bukkit.getPluginManager().callEvent(new SessionDonePreparationEvent(this));
    }

    /**
     * Gets a {@link JumpyJumpPlayer} based on their {@link UUID}
     *
     * @param uuid {@link UUID} of the player.
     */
    public JumpyJumpPlayer getPlayer(UUID uuid) {
        return this.registry.getPlayer(uuid);
    }

    public PlayerRegistry<JumpyJumpPlayer> getRegistry() {
        return registry;
    }

    public void setRegistry(PlayerRegistry<JumpyJumpPlayer> registry) {
        this.registry = registry;
    }
}
