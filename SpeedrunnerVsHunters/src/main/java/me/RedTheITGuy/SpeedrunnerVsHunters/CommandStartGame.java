package me.RedTheITGuy.SpeedrunnerVsHunters;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class CommandStartGame implements CommandExecutor {
	// Called when the command is run
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Exits if there are no arguments
		if (args.length < 1) return false;
		
		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		Scoreboard scoreboard = scoreboardManager.getMainScoreboard();

		// Loads the objective
		Objective infoBoard = scoreboard.getObjective("svhGameInfo");
		// Runs if there is a objective and that objective has the runner's name (a game is running)
		if (infoBoard != null && infoBoard.getScore(ChatColor.AQUA + "Runner: ").isScoreSet()) {
			// Tells the sender a game is running
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[SVH] " + ChatColor.RESET + "There is a game already running. Please wait until that game is finished before starting a new game.");
			// Exits the command
			return true;
		}
		
		// Gets the player from the name provided
		Player runner = Bukkit.getServer().getPlayer(args[0]);
		// Runs if the player is not online/does not exist
		if (runner == null) {
			// Tells the sender the player could not be found
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[SVH] " + ChatColor.RESET + "The player " + args[0] + " could not be found. Please ensure the player is online and their named is spelled correctly.");
			// Exits the command and tells the player how to use the command
			return false;
		}
		
		// Loads the game logic class
		GameLogic gameLogic = new GameLogic();
		// Runs the method to play the game
		gameLogic.playGame(runner);
		
		// Tells the server the command was run successfully
		return true;
	}
}
