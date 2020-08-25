package me.RedTheITGuy.SpeedrunnerVsHunters;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class GameLogic {
	public void playGame(Player runner) {
		// Loads the config
		FileConfiguration config = Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters").getConfig();
		// Gets info from the config
		int headStart = config.getInt("game.headStart");
		int locationRevealTime = config.getInt("game.locationRevealTime");
		
		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
		
		// Gets the speedrunner team
		Team speedrunnerTeam = scoreboard.getTeam("speedrunner");
		// Creates the speedrunner team if it doesn't exist
		if (speedrunnerTeam == null) speedrunnerTeam = scoreboard.registerNewTeam("speedrunner");
		// Sets the runner's name to red to distinguish from hunters
		speedrunnerTeam.setColor(ChatColor.RED);
		// Puts the runner in the runner team
		speedrunnerTeam.addEntry(runner.getName());
		
		// Gets the hunters team
		Team huntersTeam = scoreboard.getTeam("hunters");
		// Creates the hunters team if it doesn't exist
		if (huntersTeam == null) huntersTeam = scoreboard.registerNewTeam("hunters");
		// Sets the hunters' name to green to distinguish from the runner
		huntersTeam.setColor(ChatColor.GREEN);
		// Disables PVP between hunters
		huntersTeam.setAllowFriendlyFire(false);
		// Allows hunters to see each other when invisible
		huntersTeam.setCanSeeFriendlyInvisibles(true);
		
		// Loads the objective
		Objective infoBoard = scoreboard.getObjective("svhGameInfo");
		// Runs if the objective doesn't exist
		if (infoBoard == null) {
			// Gets the class to create the scoreboard
			manageScoreboard manageScoreboard = new manageScoreboard();
			// Runs the method to create the scoreboard
			infoBoard = manageScoreboard.createBoard();
		}
		
		// Runs for all the entries tracked by the scoreboard
		for (String entry : scoreboard.getEntries()) {
			// Moves to the next entry if this entry is not tracked in the objective
			if (!infoBoard.getScore(entry).isScoreSet()) continue;
			// Removes the entry from the objective
			scoreboard.resetScores(entry);
		}
		
		// Gets the runner name team
		Team runnerName = scoreboard.getTeam("runnerName");
		// Creates the runner name team if it doesn't exist
		if (runnerName == null) runnerName = scoreboard.registerNewTeam("runnerName");
		// Adds the entry to the team
		runnerName.addEntry(ChatColor.AQUA + "Runner: ");
		// Adds the info for the entry
		runnerName.setSuffix(runner.getDisplayName());
		// Sets the score for the entry to display it in the objective
		infoBoard.getScore(ChatColor.AQUA + "Runner: ").setScore(14);
		
		// Adds an empty entry for better spacing
		infoBoard.getScore(ChatColor.DARK_BLUE.toString()).setScore(1);
		
		// Gets the runner name team
		Team runTimeTeam = scoreboard.getTeam("runTime");
		// Creates the runner name team if it doesn't exist
		if (runTimeTeam == null) runTimeTeam = scoreboard.registerNewTeam("runTime");
		// Adds the entry to the team
		runTimeTeam.addEntry(ChatColor.AQUA + "Time: ");
		// Adds the info for the entry
		runTimeTeam.setSuffix("00:00");
		// Sets the score for the entry to display it in the objective
		infoBoard.getScore(ChatColor.AQUA + "Time: ").setScore(0);
		
		// Creates the key for the boss bar
		NamespacedKey barKey = new NamespacedKey(Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters"), "svhBar");
		// Gets the boss bar
		KeyedBossBar bossBar = Bukkit.getServer().getBossBar(barKey);
		// Runs if the bar doesn't exist
		if (bossBar == null) {
			// Creates the bar
			bossBar = Bukkit.getServer().createBossBar(barKey, "", BarColor.BLUE, BarStyle.SOLID);
		}
		else {
			// Sets the bar style
			bossBar.setStyle(BarStyle.SOLID);
			// Sets the bar colour
			bossBar.setColor(BarColor.BLUE);
		}
		// Sets the bar to visible
		bossBar.setVisible(true);
		// Sets the bar to full for the start
		bossBar.setProgress(1.0);
		// Sets the title of the boss bar
		bossBar.setTitle(ChatColor.GOLD + "Hunters released in " + headStart + ":00");
		
		// Teleports the runner into the world
		runner.teleport(Bukkit.getWorld("svh-overworld").getSpawnLocation());

		// Runs for every player
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			// Displays the bar to the player
			bossBar.addPlayer(player);
			// Sends a title to the player to let them know the game has started
			player.sendTitle(ChatColor.GOLD + runner.getDisplayName() + " has started as the runner!", ChatColor.AQUA + "Let the games begin...", 10, 70, 20);
			// Plays a sound to draw attention to the start of the game
			player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, SoundCategory.VOICE, 10F, 1F);
			
			// Adds the player to the hunters team if they are not the runner
			if (!player.equals(runner)) huntersTeam.addEntry(player.getName());
		}
	}
}
