package me.RedTheITGuy.SpeedrunnerVsHunters;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
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
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class GameLogic {
	public void playGame(final Player runner) {
		// Loads the config
		FileConfiguration config = Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters").getConfig();
		// Gets info from the config
		final int headStart = config.getInt("game.headStart");
		final int locationRevealTime = config.getInt("game.locationRevealTime");
		final int resetTime = config.getInt("resetTime");
		final int runnerLeaveTime = config.getInt("game.runnerLeaveTime");
		
		// Gets the region container to load the regions
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		// Runs for every world
		for (World world : Bukkit.getServer().getWorlds()) {
			// Goes to the next world if the world is not a game world
			if (!world.getName().contains("svh-")) continue;
			
			// Converts the world to a world edit world
			com.sk89q.worldedit.world.World WorldEditWorld = BukkitAdapter.adapt(world);
			// Gets the global region for the world
			ProtectedRegion globalRegion = container.get(WorldEditWorld).getRegion("__global__");
			// Runs if the region doesn't exist
			if (globalRegion == null) {
				// Creates the global region because I have to create it because... ?
				globalRegion = new GlobalProtectedRegion("__global__");
				// Adds the region to the world
				container.get(WorldEditWorld).addRegion(globalRegion);
			}
			// Enables PVP in that region
			globalRegion.setFlag(Flags.PVP, StateFlag.State.ALLOW);
			// Enables mining and placing in the region
			globalRegion.setFlag(Flags.PASSTHROUGH, StateFlag.State.ALLOW);
			// Enables movement in the region
			globalRegion.setFlag(Flags.ENTRY, StateFlag.State.ALLOW);
			// Enables creeper explosions in the region
			globalRegion.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.ALLOW);
			// Enables creeper explosions in the region
			globalRegion.setFlag(Flags.GHAST_FIREBALL, StateFlag.State.ALLOW);
			// Enables creeper explosions in the region
			globalRegion.setFlag(Flags.MOB_DAMAGE, StateFlag.State.ALLOW);
			// Enables creeper explosions in the region
			globalRegion.setFlag(Flags.USE, StateFlag.State.ALLOW);
			// Enables creeper explosions in the region
			globalRegion.setFlag(Flags.FALL_DAMAGE, StateFlag.State.ALLOW);
		}

		// Clears the runner's inventory
		runner.getInventory().clear();
		// Resets runners xp
		runner.setExp(0);
		// Heals runner to full health
		runner.setHealth(20);
		// Restores runner's hunger
		runner.setFoodLevel(20);
		// Restores runners saturation
		runner.setSaturation(5);
		// Restores runners exhaustion
		runner.setExhaustion(0);
		
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
		final BukkitScheduler scheduler = Bukkit.getScheduler();
		// Stops all tasks scheduled by this plugin
		scheduler.cancelTasks(Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters"));
		// Runs every 20 ticks (second)
		scheduler.runTaskTimer(Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters"), new Runnable() {
			public void run() {
				// Gets the objective
				Objective infoBoard = scoreboard.getObjective("svhGameInfo");
				// Gets the boss bar
				KeyedBossBar bossBar = Bukkit.getServer().getBossBar(barKey);
				// Runs if the game is still running
				if (!infoBoard.getScore(ChatColor.AQUA + "Winner: ").isScoreSet() && (bossBar == null || !(bossBar.getTitle().contains("Game ending in") && bossBar.isVisible()))) {
					// Gets the current run time with minutes and seconds split
					String[] runTime = scoreboard.getTeam("runTime").getSuffix().split(":");
					// Creates variables to store the times
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
				
				// Runs if there is a boss bar
				if (bossBar != null && bossBar.isVisible()) {
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
			    	if (secsLeft < 0) {
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
			    					player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, SoundCategory.VOICE, 10F, 1F);
			    					
			    					// Sends the player to the new world if they are not already in it
			    					if (!player.getWorld().getName().contains("svh-")) spawner.spawnPlayer(player);
			    					
			    					// Returns if the player is the runner
			    					if (scoreboard.getTeam("runnerName").getSuffix().equalsIgnoreCase(player.getDisplayName())) continue;

			    					// Clears the player's inventory
			    					player.getInventory().clear();
			    					// Resets players xp
			    					player.setExp(0);
			    					// Heals player to full health
			    					player.setHealth(20);
			    					// Restores player's hunger
			    					player.setFoodLevel(20);
			    					// Restores players saturation
			    					player.setSaturation(5);
			    					// Restores players exhaustion
			    					player.setExhaustion(0);
			    				}
			    				
			    				// Enables the daylight cycle
			    				Bukkit.getWorld("svh-overworld").setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
			    				
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
			    			// Runs if the timer is for the location reveal
					    	else if (barTitle.contains("Location revealed in")) {
					    		// Adds an empty entry for better spacing if there is none
			    				if (!infoBoard.getScore(ChatColor.LIGHT_PURPLE.toString()).isScoreSet()) infoBoard.getScore(ChatColor.LIGHT_PURPLE.toString()).setScore(13);
			    				
			    				// Adds the player location label if it isn't there
			    				if (!infoBoard.getScore(ChatColor.DARK_AQUA + "Player Location:").isScoreSet()) infoBoard.getScore(ChatColor.DARK_AQUA + "Player Location:").setScore(12);
			    				
			    				// Stores the runners location
			    				Location runnerLocation = runner.getLocation();
			    				
			    				// Runs if the player is not in the overworld
			    				if (!runnerLocation.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
			    					// Gets the world team
				    				Team worldTeam = scoreboard.getTeam("world");
				    				// Creates the world team if it doesn't exist
				    				if (worldTeam == null) worldTeam = scoreboard.registerNewTeam("world");
				    				// Adds the entry to the team
				    				worldTeam.addEntry(ChatColor.AQUA + "World: ");
				    				// Runs if the player is in the nether
				    				if (runnerLocation.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
					    				// Sets the world to the nether
					    				worldTeam.setSuffix("Nether");
				    				}
				    				// Runs if the player is in the end
				    				else if (runnerLocation.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
				    					// Sets the world to the end
				    					worldTeam.setSuffix("The End");
				    				}
				    				// Sets the score for the entry to display it in the objective
				    				infoBoard.getScore(ChatColor.AQUA + "World: ").setScore(11);
			    				}
			    				// Runs if the player is in the overworld
			    				else {
			    					// Deletes the world entry is it is there
			    					if (infoBoard.getScore(ChatColor.AQUA + "World: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "World: ");
			    				}
			    				
			    				// Runs if the player is not in the end
			    				if (!runnerLocation.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
			    					// Gets the x coord team
				    				Team xCoordTeam = scoreboard.getTeam("xCoord");
				    				// Creates the x coord team if it doesn't exist
				    				if (xCoordTeam == null) xCoordTeam = scoreboard.registerNewTeam("xCoord");
				    				// Adds the entry to the team
				    				xCoordTeam.addEntry(ChatColor.AQUA + "X: ");
				    				// Adds the info for the entry
				    				xCoordTeam.setSuffix(runnerLocation.getBlockX() + "");
				    				// Sets the score for the entry to display it in the objective
				    				infoBoard.getScore(ChatColor.AQUA + "X: ").setScore(10);

			    					// Gets the y coord team
				    				Team yCoordTeam = scoreboard.getTeam("yCoord");
				    				// Creates the y coord team if it doesn't exist
				    				if (yCoordTeam == null) yCoordTeam = scoreboard.registerNewTeam("yCoord");
				    				// Adds the entry to the team
				    				yCoordTeam.addEntry(ChatColor.AQUA + "Y: ");
				    				// Adds the info for the entry
				    				yCoordTeam.setSuffix(runnerLocation.getBlockY() + "");
				    				// Sets the score for the entry to display it in the objective
				    				infoBoard.getScore(ChatColor.AQUA + "Y: ").setScore(9);

			    					// Gets the z coord team
				    				Team zCoordTeam = scoreboard.getTeam("zCoord");
				    				// Creates the z coord team if it doesn't exist
				    				if (zCoordTeam == null) zCoordTeam = scoreboard.registerNewTeam("zCoord");
				    				// Adds the entry to the team
				    				zCoordTeam.addEntry(ChatColor.AQUA + "Z: ");
				    				// Adds the info for the entry
				    				zCoordTeam.setSuffix(runnerLocation.getBlockZ() + "");
				    				// Sets the score for the entry to display it in the objective
				    				infoBoard.getScore(ChatColor.AQUA + "Z: ").setScore(8);
			    				}
			    				// Runs if the player is in the end
			    				else {
			    					// Deletes the x entry is it is there
			    					if (infoBoard.getScore(ChatColor.AQUA + "X: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "X: ");
			    					// Deletes the y entry is it is there
			    					if (infoBoard.getScore(ChatColor.AQUA + "Y: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Y: ");
			    					// Deletes the z entry is it is there
			    					if (infoBoard.getScore(ChatColor.AQUA + "Z: ").isScoreSet()) scoreboard.resetScores(ChatColor.AQUA + "Z: ");
			    				}
			    				
			    				// Runs for every player
			    				for (Player player : Bukkit.getOnlinePlayers()) {
			    					// Sends an action bar to let the player know the location has been revealed
			    					player.sendActionBar(ChatColor.GOLD + "The runner's location has been revealed.");
			    					// Plays a sound to draw attention to the location reveal
			    					player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.VOICE, 10F, 1F);
			    				}
					    	}
					    	// Runs if the timer is for the reset after runner leaves
					    	else if (barTitle.contains("Game ending in")) {
					    		// Gets the game ender class
								GameEnder gameEnder = new GameEnder();
								// Runs the method to end the game
								gameEnder.forfeitGame();
								// Exits the method
								return;
					    	}
			    			// Runs if the timer is for reseting the server
					    	else if (barTitle.contains("Server reseting in")) {
					    		// Stops all tasks scheduled by this plugin
					    		scheduler.cancelTasks(Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters"));
					    		
					    		// Runs for every player
			    				for (Player player : Bukkit.getOnlinePlayers()) {
			    					// Kicks the player from the server
			    					player.kickPlayer("The game has ended, setting up the next game.");
			    				}
					    		// Enables the whitelist
					    		Bukkit.setWhitelist(true);
					    		
					    		// Makes the boss bar invisible
					    		bossBar.setVisible(false);
					    		// Removes all players from the boss bar
					    		bossBar.removeAll();
					    		
					    		// Gets the scoreboard manager
					    		manageScoreboard scoreboardManager = new manageScoreboard();
					    		// Resets the scoreboard
					    		scoreboardManager.resetBoard();
					    		
					    		// Gets the world generator
					    		GenerateWorlds worldGenerator = new GenerateWorlds();
					    		// Deletes old worlds and creates new ones
					    		worldGenerator.generate(true);
					    		
					    		// Exits the method
					    		return;
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
			    	// Runs if the timer is for the reset after runner leaves
			    	else if (barTitle.contains("Game ending in")) {
			    		// Calculates the bar progress
			    		barProgress = (((double) minsLeft * 60) + secsLeft) / (runnerLeaveTime * 60);
			    	}
			    	// Runs if the timer is for reseting the server
			    	else if (barTitle.contains("Server reseting in")) {
			    		// Calculates the bar progress
			    		barProgress = (((double) minsLeft * 60) + secsLeft) / (resetTime * 60);
			    	}
			    	// Sets the bars progress
			    	bossBar.setProgress(barProgress);
				}
			}
		}, 20, 20);
	}
}
