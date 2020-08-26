package me.RedTheITGuy.SpeedrunnerVsHunters;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class GameLogic {
	public void playGame(final Player runner) {
		// Loads the config
		FileConfiguration config = Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters").getConfig();
		// Gets info from the config
		final int headStart = config.getInt("game.headStart");
		final int locationRevealTime = config.getInt("game.locationRevealTime");
		
		// Teleports the runner into the world
		runner.teleport(Bukkit.getWorld("svh-overworld").getSpawnLocation());
		
		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		final Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
		
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
		final NamespacedKey barKey = new NamespacedKey(Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters"), "svhBar");
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

		// Runs for every player
		for (Player player : Bukkit.getOnlinePlayers()) {
			// Displays the bar to the player
			bossBar.addPlayer(player);
			// Sends a title to the player to let them know the game has started
			player.sendTitle(ChatColor.GOLD + runner.getDisplayName() + " is the runner!", ChatColor.AQUA + "Let the games begin...", 10, 70, 20);
			// Plays a sound to draw attention to the start of the game
			player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, SoundCategory.VOICE, 10F, 1F);
			
			// Adds the player to the hunters team if they are not the runner
			if (!player.equals(runner)) huntersTeam.addEntry(player.getName());
		}
		
		// Gets the bukkit scheduler
		BukkitScheduler scheduler = Bukkit.getScheduler();
		// Runs every 20 ticks (second)
		scheduler.runTaskTimer(Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters"), new Runnable() {
			public void run() {
				// Gets the objective
				Objective infoBoard = scoreboard.getObjective("svhGameInfo");
				// Runs if the game is still running
				if (!infoBoard.getScore(ChatColor.AQUA + "Winner: ").isScoreSet()) {
					// Gets the current run time with minutes and seconds split
					String[] runTime = scoreboard.getTeam("runTime").getSuffix().split(":");
					int hoursSurvived = 0;
					int minsSurvived = 0;
					int secsSurvived = 0;
					if (runTime.length <= 2) {
						// Converts the minutes and seconds to integers
				    	minsSurvived = Integer.parseInt(runTime[0]);
				    	secsSurvived = Integer.parseInt(runTime[1]);
					}
					else {
						// Converts the hours, minutes and seconds to integers
						hoursSurvived = Integer.parseInt(runTime[0]);
				    	minsSurvived = Integer.parseInt(runTime[1]);
				    	secsSurvived = Integer.parseInt(runTime[2]);
					}
			    	
			    	// Adds 1 to the seconds
			    	secsSurvived++;
			    	// Runs if seconds if 60+
			    	if (secsSurvived > 59) {
			    		// Resets the seconds to 0
			    		secsSurvived = 0;
			    		// Adds 1 to the minutes
			    		minsSurvived++;
			    		// Runs if minutes is 60+
			    		if (minsSurvived > 59) {
			    			// Resets the minutes to 0
			    			minsSurvived = 0;
			    			// Adds 1 to the hours
			    			hoursSurvived++;
			    		}
			    	}
			    	
			    	// Creates the string to store the time suffix
			    	String runTimeString = "";
			    	// Runs if there are hours in the run time
			    	if (hoursSurvived > 0) {
			    		runTimeString = hoursSurvived + ":" + String.format("%02d", minsSurvived) + ":" + String.format("%02d", secsSurvived);
			    	}
			    	else {
			    		runTimeString = minsSurvived + ":" + String.format("%02d", secsSurvived);
			    	}
			    	
			    	// Sets the new time in the scoreboard
			    	scoreboard.getTeam("runTime").setSuffix(runTimeString);
				}
				
				// Gets the boss bar
				KeyedBossBar bossBar = Bukkit.getServer().getBossBar(barKey);
				// Runs if there is a boss bar
				if (bossBar != null) {
					// Gets the boss bar title
					String barTitle = bossBar.getTitle();
					// Strips the colour so it doesn't mess up the code
					barTitle = ChatColor.stripColor(barTitle);
					// Gets the time from the boss bar
					String[] barTime = barTitle.replaceAll("[^:\\d]", "").split(":");
					// Converts the minutes and seconds to integers
			    	int minsLeft = Integer.parseInt(barTime[0]);
			    	int secsLeft = Integer.parseInt(barTime[1]);
			    	
			    	// Subtracts 1 from the seconds
			    	secsLeft--;
			    	// Runs if the seconds left has reached 0
			    	if (secsLeft < 1) {
			    		// Resets the seconds to 59
			    		secsLeft = 59;
			    		// Subtracts 1 from the minutes
			    		minsLeft--;
			    		// Runs if the minutes left has reached 0
			    		if (minsLeft < 0) {
			    			// Runs is the bar is for hunter release
			    			if (barTitle.contains("Hunters released in")) {
			    				// Gets the class for spawning the player
			    				SpawnManager spawner = new SpawnManager();
			    				// Runs for every player
			    				for (Player player : Bukkit.getOnlinePlayers()) {
			    					// Sends a title to the player to let them know the hunters have been released
			    					player.sendTitle(ChatColor.GOLD + "Hunters released!", ChatColor.AQUA + "Good luck, you'll need it...", 10, 70, 20);
			    					// Plays a sound to draw attention to the hunters release
			    					player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, SoundCategory.VOICE, 10F, 1F);
			    					
			    					// Sends the player to the new world if they are not already in it
			    					if (!player.getWorld().getName().contains("svh-")) spawner.spawnPlayer(player);
			    				}
			    				
			    				// Adds an empty entry for better spacing
			    				infoBoard.getScore(ChatColor.DARK_AQUA.toString()).setScore(3);
			    				
			    				// Gets the kill count team
			    				Team killCountTeam = scoreboard.getTeam("killCount");
			    				// Creates the runner name team if it doesn't exist
			    				if (killCountTeam == null) killCountTeam = scoreboard.registerNewTeam("killCount");
			    				// Adds the entry to the team
			    				killCountTeam.addEntry(ChatColor.AQUA + "Kills: ");
			    				// Adds the info for the entry
			    				killCountTeam.setSuffix("0");
			    				// Sets the score for the entry to display it in the objective
			    				infoBoard.getScore(ChatColor.AQUA + "Kills: ").setScore(2);
			    			}
			    			
			    			// Sets the seconds to 0
			    			secsLeft = 0;
			    			// Sets the minutes to the location reveal time
			    			minsLeft = locationRevealTime;
			    			// Sets the bar title to show it's now for location reveal
			    			barTitle = "Location revealed in ";
			    		}
			    	}
			    	// Changes the title string to what the title will be
			    	barTitle = barTitle.replaceAll("[:\\d]", "") + minsLeft + ":" + String.format("%02d", secsLeft);
			    	// Sets the bars title
			    	bossBar.setTitle(ChatColor.GOLD + barTitle);
			    	
			    	// Creates a double to store the bar progress
			    	double barProgress = 0;
			    	// Runs if the timer is for the initial release
			    	if (barTitle.contains("Hunters released in")) {
			    		// Calculates the bar progress
			    		barProgress = (((double) minsLeft * 60) + secsLeft) / (headStart * 60);
			    	}
			    	// Runs if the timer is for the location reveal
			    	else if (barTitle.contains("Location revealed in")) {
			    		// Calculates the bar progress
			    		barProgress = (((double) minsLeft * 60) + secsLeft) / (locationRevealTime * 60);
			    	}
			    	// Sets the bars progress
			    	bossBar.setProgress(barProgress);
				}
			}
		}, 20, 20);
	}
}
