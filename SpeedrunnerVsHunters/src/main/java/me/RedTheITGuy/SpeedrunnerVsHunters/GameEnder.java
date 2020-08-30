package me.RedTheITGuy.SpeedrunnerVsHunters;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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

public class GameEnder {
	public void endGame(boolean runnerWin) {
		// Loads the config
		FileConfiguration config = Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters").getConfig();
		// Gets info from the config
		final int resetTime = config.getInt("resetTime");
		
		// Creates a string to store the title
		String title = "";
		// Creates a string to store the subtitles
		String subtitleRunner = "";
		String subtitleHunter = "";
		// Creates a variable to store the game over sound
		Sound gameOverSound;

		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		final Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
		// Loads the objective
		Objective infoBoard = scoreboard.getObjective("svhGameInfo");
		
		// Gets the name of the runner
		String runnerName = scoreboard.getTeam("runnerName").getSuffix();
		
		// Removes the Player location label if it exists
		if (infoBoard.getScore(ChatColor.DARK_AQUA + "Player Location:").isScoreSet()) scoreboard.resetScores(ChatColor.DARK_AQUA + "Player Location:");
		// Deletes the world entry is it is there
		if (infoBoard.getScore(ChatColor.AQUA + "World: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "World: ");
		// Deletes the x if it exists
		if (infoBoard.getScore(ChatColor.AQUA + "X: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "X: ");
		// Deletes the y if it exists
		if (infoBoard.getScore(ChatColor.AQUA + "Y: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Y: ");
		// Deletes the z if it exists
		if (infoBoard.getScore(ChatColor.AQUA + "Z: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Z: ");
		// Deletes the portal location label if it exists
		if (infoBoard.getScore(ChatColor.DARK_AQUA + "Portal Location:").isScoreSet()) scoreboard.resetScores(ChatColor.DARK_AQUA + "Portal Location:");
		// Deletes the portal x if it exists
		if (infoBoard.getScore(ChatColor.AQUA + "Portal X: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Portal X: ");
		// Deletes the portal y if it exists
		if (infoBoard.getScore(ChatColor.AQUA + "Portal Y: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Portal Y: ");
		// Deletes the portal z if it exists
		if (infoBoard.getScore(ChatColor.AQUA + "Portal Z: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Portal Z: ");
		
		// Adds an empty entry for better spacing if there is none
		if (!infoBoard.getScore(ChatColor.LIGHT_PURPLE.toString()).isScoreSet()) infoBoard.getScore(ChatColor.LIGHT_PURPLE.toString()).setScore(13);
		
		// Gets the portal x coord team
		Team winnerTeam = scoreboard.getTeam("winner");
		// Creates the x coord team if it doesn't exist
		if (winnerTeam == null) winnerTeam = scoreboard.registerNewTeam("winner");
		// Adds the entry to the team
		winnerTeam.addEntry(ChatColor.AQUA + "Winner: ");
		
		// Runs if the runner won
		if (runnerWin) {
			// Adds the info for the entry
			winnerTeam.setSuffix("Speedrunner");
			
			// Sets the title to be sent to all players
			title = runnerName + " Wins!";
			// Sets the subtitle to be sent to the runner
			subtitleRunner = "Congrats, you're just to good!";
			// Sets the subtitle to be sent to the hunters
			subtitleHunter = "Better luck next time.";
			// Sets the sound to be played
			gameOverSound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
		}
		// Runs if the hunters won
		else {
			// Adds the info for the entry
			winnerTeam.setSuffix("Hunters");
			
			// Sets the title to be sent to all players
			title = "The Hunters Win!";
			// Sets the subtitle to be sent to the runner
			subtitleRunner = "You lost? You hate to see it.";
			// Sets the subtitle to be sent to the hunters
			subtitleHunter = "What a game, you're masters at this!";
			// Sets the sound to be played
			gameOverSound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
		}
		
		// Sets the score for the entry to display it in the objective
		infoBoard.getScore(ChatColor.AQUA + "Winner: ").setScore(12);
		
		// Creates the key for the boss bar
		final NamespacedKey barKey = new NamespacedKey(Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters"), "svhBar");
		// Gets the boss bar
		KeyedBossBar bossBar = Bukkit.getServer().getBossBar(barKey);
		// Runs if the bar doesn't exist
		if (bossBar == null) {
			// Creates the bar
			bossBar = Bukkit.getServer().createBossBar(barKey, "", BarColor.BLUE, BarStyle.SOLID);
		}
		// Sets the bar to visible
		bossBar.setVisible(true);
		// Sets the bar to full for the start
		bossBar.setProgress(1.0);
		// Sets the title of the boss bar
		bossBar.setTitle(ChatColor.GOLD + "Server reseting in " + resetTime + ":00");
		
		// Runs for every player
		for (Player player : Bukkit.getOnlinePlayers()) {
			// Sets the player to spectator mode
			player.setGameMode(GameMode.SPECTATOR);
			// Clears the player's inventory
			player.getInventory().clear();
			// Resets players xp
			player.setExp(0);
			
			// Runs if the player is the runner
			if (scoreboard.getTeam("runnerName").getSuffix().equalsIgnoreCase(player.getDisplayName())) {
				// Sends a title to the player to let them know the game has ended
				player.sendTitle(ChatColor.GOLD + title, ChatColor.AQUA + subtitleRunner, 10, 70, 20);
			}
			// Runs if the player is a hunter
			else {
				// Sends a title to the player to let them know the game has ended
				player.sendTitle(ChatColor.GOLD + title, ChatColor.AQUA + subtitleHunter, 10, 70, 20);
			}
			// Plays a sound to draw attention to the start of the game
			player.playSound(player.getLocation(), gameOverSound, SoundCategory.VOICE, 10F, 1F);
		}
	}
	
	public void forfeitGame() {
		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		final Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
		// Loads the objective
		Objective infoBoard = scoreboard.getObjective("svhGameInfo");
		
		// Removes the Player location label if it exists
		if (infoBoard.getScore(ChatColor.DARK_AQUA + "Player Location:").isScoreSet()) scoreboard.resetScores(ChatColor.DARK_AQUA + "Player Location:");
		// Deletes the world entry is it is there
		if (infoBoard.getScore(ChatColor.AQUA + "World: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "World: ");
		// Deletes the x if it exists
		if (infoBoard.getScore(ChatColor.AQUA + "X: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "X: ");
		// Deletes the y if it exists
		if (infoBoard.getScore(ChatColor.AQUA + "Y: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Y: ");
		// Deletes the z if it exists
		if (infoBoard.getScore(ChatColor.AQUA + "Z: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Z: ");
		// Deletes the portal location label if it exists
		if (infoBoard.getScore(ChatColor.DARK_AQUA + "Portal Location:").isScoreSet()) scoreboard.resetScores(ChatColor.DARK_AQUA + "Portal Location:");
		// Deletes the portal x if it exists
		if (infoBoard.getScore(ChatColor.AQUA + "Portal X: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Portal X: ");
		// Deletes the portal y if it exists
		if (infoBoard.getScore(ChatColor.AQUA + "Portal Y: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Portal Y: ");
		// Deletes the portal z if it exists
		if (infoBoard.getScore(ChatColor.AQUA + "Portal Z: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Portal Z: ");
		
		// Adds an empty entry for better spacing if there is none
		if (!infoBoard.getScore(ChatColor.LIGHT_PURPLE.toString()).isScoreSet()) infoBoard.getScore(ChatColor.LIGHT_PURPLE.toString()).setScore(13);
		
		// Gets the portal x coord team
		Team winnerTeam = scoreboard.getTeam("winner");
		// Creates the x coord team if it doesn't exist
		if (winnerTeam == null) winnerTeam = scoreboard.registerNewTeam("winner");
		// Adds the entry to the team
		winnerTeam.addEntry(ChatColor.AQUA + "Winner: ");
		// Adds the info for the entry
		winnerTeam.setSuffix("Hunters");
		// Sets the score for the entry to display it in the objective
		infoBoard.getScore(ChatColor.AQUA + "Winner: ").setScore(12);
		
		// Creates the key for the boss bar
		final NamespacedKey barKey = new NamespacedKey(Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters"), "svhBar");
		// Gets the boss bar
		KeyedBossBar bossBar = Bukkit.getServer().getBossBar(barKey);
		// Runs if the bar doesn't exist
		if (bossBar == null) {
			// Creates the bar
			bossBar = Bukkit.getServer().createBossBar(barKey, "", BarColor.BLUE, BarStyle.SOLID);
		}
		// Sets the bar to visible
		bossBar.setVisible(true);
		// Sets the bar to full for the start
		bossBar.setProgress(1.0);
		// Sets the title of the boss bar
		bossBar.setTitle(ChatColor.GOLD + "Server reseting in 0:15");
		
		// Runs for every player
		for (Player player : Bukkit.getOnlinePlayers()) {
			// Sets the player to spectator mode
			player.setGameMode(GameMode.SPECTATOR);
			// Clears the player's inventory
			player.getInventory().clear();
			// Resets players xp
			player.setExp(0);
			
			// Sends a title to the player to let them know the game has ended
			player.sendTitle(ChatColor.GOLD + "The Hunters Win!", ChatColor.AQUA + "The game was forfeited.", 10, 70, 20);
			// Plays a sound to draw attention to the start of the game
			player.playSound(player.getLocation(), Sound.ENTITY_GHAST_HURT, SoundCategory.VOICE, 10F, 1F);
		}
	}
}
