package me.RedTheITGuy.SpeedrunnerVsHunters;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class CommandGoTo implements CommandExecutor {
	// Called when the command is run
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Runs if the command was not executed by a player
		if (!(sender instanceof Player)) {
			// Tells the sender a game is running
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[SVH] " + ChatColor.RESET + "This command can only be run by a player.");
			// Exits the command
			return true;
		}
		
		// Gets the player that ran the command
		Player player = (Player) sender;
		
		// Runs if the player is not in spectator mode
		if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
			// Tells the sender a game is running
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[SVH] " + ChatColor.RESET + "This command can only be run in spectator mode.");
			// Exits the command
			return true;
		}
		
		// Exits if there are no arguments
		if (args.length < 1) return false;
		
		// Stores the player name to teleport to
		String playerToName = args[0];
		// Runs if the player wants the runner
		if (playerToName.equalsIgnoreCase("runner") || playerToName.equalsIgnoreCase("speedrunner")) {
			// Gets the scoreboard manager
			ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
			// Gets the scoreboard
			Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
			// Loads the objective
			Objective infoBoard = scoreboard.getObjective("svhGameInfo");
			
			// Runs if there is not objective or the objective does not have the runner's name (a game is not running)
			if (infoBoard == null || !infoBoard.getScore(ChatColor.AQUA + "Runner: ").isScoreSet()) {
				// Tells the sender a game is not running
				sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[SVH] " + ChatColor.RESET + "There is no game running at the moment.");
				// Exits the command
				return true;
			}
			
			// Sets the player name to teleport to to the runners name
			playerToName = scoreboard.getTeam("runnerName").getSuffix();
		}
		
		// Gets the player to teleport to
		Player target = Bukkit.getPlayer(playerToName);
		// Runs if the player could not be found
		if (target == null) {
			// Tells the sender a target could not be found
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[SVH] " + ChatColor.RESET + "Could not find a player with that name, are they online?");
			// Exits the command
			return true;
		}
		// Runs if the target is the sender
		else if (target.equals(player)) {
			// Tells the sender they cannot teleport to themselves
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[SVH] " + ChatColor.RESET + "You can't teleport to yourself.");
			// Exits the command
			return true;
		}
		
		// Teleports the sender to the target
		player.teleportAsync(target.getLocation());
		
		// Tells the server the command was run successfully
		return true;
	}
}
