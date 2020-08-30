package me.RedTheITGuy.SpeedrunnerVsHunters;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class CommandEndGame implements CommandExecutor {
	// Called when the command is run
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Gets the scoreboard manager
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		// Gets the scoreboard
		Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
		// Loads the objective
		Objective infoBoard = scoreboard.getObjective("svhGameInfo");
		
		// Runs if there is not objective or the objective does not have the runner's name (a game is not running)
		if (infoBoard == null || !infoBoard.getScore(ChatColor.AQUA + "Runner: ").isScoreSet()) {
			// Tells the sender a game is running
			sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[SVH] " + ChatColor.RESET + "There is no game currently running.");
			// Exits the command
			return true;
		}
		
		// Gets the game ender class
		GameEnder gameEnder = new GameEnder();
		// Runs the method to end the game
		gameEnder.forfeitGame();
		
		// Tells the server the command was run successfully
		return true;
	}
}
