package me.RedTheITGuy.SpeedrunnerVsHunters;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class PlayerManager {
	public void setPlayerCount(int countAlt) {
		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
		// Loads the objective
		Objective infoBoard = scoreboard.getObjective("svhGameInfo");
		
		// Exits if the player count isn't needed
		if (infoBoard == null || !infoBoard.getScore(ChatColor.AQUA + "Players: ").isScoreSet()) return;
		
		// Loads the config
		FileConfiguration config = Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters").getConfig();
		// Gets info from the config
		int minPlayers = config.getInt("minPlayers");
		
		// Creates the variable to store the online player count
		int onlinePlayers = countAlt;
		// Runs for all online players
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			// Adds to the online player count if the player is in survival
			if (player.getGameMode().equals(GameMode.SURVIVAL)) onlinePlayers++;
		}
		
		// Gets the gamemode team 
		Team playersTeam = scoreboard.getTeam("players");
		// Creates the team if it doesn't exist
		if (playersTeam == null) playersTeam = scoreboard.registerNewTeam("players");
		// Adds the players entry
		playersTeam.addEntry(ChatColor.AQUA + "Players: ");
		// Sets the info in the players entry
		playersTeam.setSuffix(onlinePlayers + "/" + minPlayers);
	}
}
