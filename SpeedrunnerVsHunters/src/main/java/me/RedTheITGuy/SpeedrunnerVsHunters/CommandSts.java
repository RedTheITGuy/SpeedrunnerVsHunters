package me.RedTheITGuy.SpeedrunnerVsHunters;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class CommandSts implements CommandExecutor {
	// Called when the command is run
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Gets the plugin description
		PluginDescriptionFile pluginDescription = Bukkit.getPluginManager().getPlugin("SpeedrunnerVsHunters").getDescription();
		
		// Creates a string to store the authors names
		String authors = "";
		// Runs for all the authors
		for (String name : pluginDescription.getAuthors()) {
			// Adds formating to the string if needed
			if (!authors.equals("")) name = ", " + name;
			// Adds the name to the names string
			authors += name;
		}
		
		// Creates the message to be sent (Bungee component for allowing clicking)
		ComponentBuilder message = new ComponentBuilder("[STS] ").color(ChatColor.GOLD).bold(true);
		// Adds the main info to the message
		message.append("This server is running Stop the Speedrunner version " + pluginDescription.getVersion() + " by " + authors + ".\n").color(ChatColor.WHITE);
		
		// Creates the component for the clickable section
		TextComponent clickableText = new TextComponent("For more information, click here!");
		// Set's the hover for the text
		clickableText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GOLD + "Click here to go to the plugin's GitHub and download it for yourself.")));
		// Sets the click event for the text
		clickableText.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, pluginDescription.getWebsite()));
		// Adds the clickable text to the message
		message.append(clickableText).color(ChatColor.AQUA);
		
		// Sends the command sender the message
		sender.spigot().sendMessage(message.create());
		
		// Exits the command
		return true;
	}
}
