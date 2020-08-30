package me.RedTheITGuy.SpeedrunnerVsHunters;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class manageScoreboard {
	public Objective createBoard() {
		// Loads the config
		FileConfiguration config = Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters").getConfig();
		// Gets info from the config
		String title = ChatColor.translateAlternateColorCodes('&', config.getString("title"));

		// Runs if the title is longer than 128 characters
		if (title.length() > 128) {
			// Tells the console the issue
			Bukkit.getLogger().warning("Title in config too long, please use a smaller title");
			// Changes the title to a compatible title
			title = ChatColor.RED + "Change Config";
		}
		
		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
		
		// Unregisters the board if it already exists
		if (scoreboard.getObjective("svhGameInfo") != null) scoreboard.getObjective("svhGameInfo").unregister();
		
		// Creates the objective used for displaying game information
		Objective svhGameInfo = scoreboard.registerNewObjective("svhGameInfo", "dummy", title);
		// Sets the display slot for the objective
		svhGameInfo.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		// Runs the method to set the board to it's starting state
		resetBoard();
		
		// Returns the created board
		return svhGameInfo;
	}
	
	public void resetBoard() {
		// Reloads the config
		Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters").reloadConfig();
		
		// Creates the key for the boss bar
		NamespacedKey barKey = new NamespacedKey(Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters"), "svhBar");
		// Removes the bar
		Bukkit.getServer().removeBossBar(barKey);
		
		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
		// Gets the game info objective
		Objective svhGameInfo = scoreboard.getObjective("svhGameInfo");
		// Runs if the board doesn't exist
		if (svhGameInfo == null) {
			// Runs the method to create the board
			createBoard();
			// Exits the method
			return;
		}
		
		// Gets the speedrunner team
		Team speedrunnerTeam = scoreboard.getTeam("speedrunner");
		// Removes the speedrunner team if it exists
		if (speedrunnerTeam != null) speedrunnerTeam.unregister();
		
		// Gets the hunters team
		Team huntersTeam = scoreboard.getTeam("hunters");
		// Removes the hunters team if it exists
		if (huntersTeam != null) huntersTeam.unregister();
		
		// Runs for all the entries tracked by the scoreboard
		for (String entry : scoreboard.getEntries()) {
			// Moves to the next entry if this entry is not tracked in the objective
			if (!svhGameInfo.getScore(entry).isScoreSet()) continue;
			// Removes the entry from the objective
			scoreboard.resetScores(entry);
		}
		
		// Gets the gamemode team 
		Team gamemodeTeam = scoreboard.getTeam("gamemode");
		// Creates the team if it doesn't exist
		if (gamemodeTeam == null) gamemodeTeam = scoreboard.registerNewTeam("gamemode");
		// Adds the gamemode entry
		gamemodeTeam.addEntry(ChatColor.AQUA + "Gamemode: ");
		// Sets the info in the gamemode entry
		gamemodeTeam.setSuffix("Speedrunner vs Hunters");
		// Sets the score for the entry so it displays
		svhGameInfo.getScore(ChatColor.AQUA + "Gamemode: ").setScore(2);
		
		// Adds an empty entry for better spacing
		svhGameInfo.getScore(ChatColor.DARK_BLUE.toString()).setScore(1);

		// Sets the score for the entry
		svhGameInfo.getScore(ChatColor.AQUA + "Players: ").setScore(0);
		// Gets the class to set the player count
		PlayerManager playerManager = new PlayerManager();
		// Sets the player count
		playerManager.setPlayerCount(0);
	}
}
