package me.RedTheITGuy.SpeedrunnerVsHunters;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class PlayerCompass {
	public void Set(Player player) {
		Bukkit.getLogger().info("Ran compass set");
		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
		// Loads the objective
		Objective infoBoard = scoreboard.getObjective("svhGameInfo");
		
		// Returns if the info board doensn't exist or a game isn't running
		if (infoBoard == null || !infoBoard.getScore(ChatColor.AQUA + "Runner: ").isScoreSet()) return;
		
		// Creates the location variable
		Location location = new Location(Bukkit.getWorld("svh-overworld"), 0, 0, 0);
		
		// Runs if there is a portal location
		if (infoBoard.getScore(ChatColor.DARK_AQUA + "Portal Location:").isScoreSet()) {
			// Sets the x location
			if (scoreboard.getTeam("portalXcoord") != null) location.setX(Double.parseDouble(scoreboard.getTeam("portalXcoord").getSuffix()));
			// Sets the y location
			if (scoreboard.getTeam("portalYcoord") != null) location.setY(Double.parseDouble(scoreboard.getTeam("portalYcoord").getSuffix()));
			// Sets the z location
			if (scoreboard.getTeam("portalZcoord") != null) location.setZ(Double.parseDouble(scoreboard.getTeam("portalZcoord").getSuffix()));
			Bukkit.getLogger().info("Portal");
		}
		else if (infoBoard.getScore(ChatColor.DARK_AQUA + "Player Location:").isScoreSet()) {
			// Sets the world if needed
			if (infoBoard.getScore(ChatColor.AQUA + "World: ").isScoreSet()) location.setWorld(Bukkit.getWorld("svh-nether"));
			// Sets the x location
			if (scoreboard.getTeam("xCoord") != null) location.setX(Double.parseDouble(scoreboard.getTeam("xCoord").getSuffix()));
			// Sets the y location
			if (scoreboard.getTeam("yCoord") != null) location.setY(Double.parseDouble(scoreboard.getTeam("yCoord").getSuffix()));
			// Sets the z location
			if (scoreboard.getTeam("zCoord") != null) location.setZ(Double.parseDouble(scoreboard.getTeam("zCoord").getSuffix()));
			Bukkit.getLogger().info("Player");
		}
		else return;
		
		// Logs the compass location
		Bukkit.getLogger().info("Compass location: " + location.getWorld().getEnvironment() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
		
		// Sets the players compass location
		player.setCompassTarget(location);
		Bukkit.getLogger().info("Set compass location for " + player.getName());
	}
}
