# Stop the Speedrunner
This plugin allows playing the stop the speedrunner game mode (referred to as SpeedrunnerVsHunters in the program because of a name change during development), commonly played on SB737's streams. In this game mode, one player is selected to be the "speedrunner" and has to enter the ender portal after the ender dragon is defeated, also known as completing Minecraft. However, there are other players on the server. They are called "hunters", and their job is to kill the speedrunner before they can complete the game.

## How the plugin works
* This plugin automates the logic needed for the game mode. 
* Firstly, a runner will be selected. This can be through the automatic selection process, which will select a random runner after there is the set amount of players on the server for the set amount of time, or by using the /startgame command.
* Once a runner is selected, they will be teleported to the game world. They will have the time set in the config to gather resources before...
* The runners are release, aka they are teleported to the game world. Though they will not know where the runner is, they can gather supplies, and if they see the runner they can attack them. Hunters cannot attack each other
* Every couple of minutes, as specified in the config, the runner's location will be revealed. The hunters can then use this location to attempt to find the runner. They can also craft a compass, to point them in the direction of the runner.
* If the runner enters a nether portal, the location of that portal will be revealed. That way hunters can easily find the portal used by the runner. The runner's location will still update in the nether, but compasses will no longer work. Once the runner leaves the nether, the portal location will be removed.
* Once the runner enters the end, the location reveal timer will stop. The location of the end portal will be shared, but the location of the runner will be hidden. Once the runner jumps into the portal to exit the end, they will win and the game will end.
* If the runner dies at any point through this, for any reason, the hunters will win and the game will end.
* Once the game has ended, all players will be put into spectator mode. This allows them to look over the world, and what they and others have done. They can also use the /goto command to teleport to another player. After a period of time set in the config, the server will automatically reset and generate new worlds for the next game, leaving no need to restart the server.

## Features of the plugin
* Can picks a random runner and start the game
* Allows the runner to gather resources for a customizable time, without hunters
* Releases the hunters after that time
* Shares the location of the runner at intervals set in the config
* Sets the players compass location to point to the shared location
* Shares the location of the portal the runner used to leave the overworld
* Disables bed explosions to stop the bed strat in the nether and end, if enabled
* Ends the game if the hunters or runner wins
* Generates a new game world automatically without restarting the server

## Requirements
This plugin needs certain other programs to work
* The server must be running [PaperMC](https://papermc.io/), **not Spigot**
* Minecraft version 1.16+ is recommended, no other versions have been tested.
* The [WorldGuard plugin](https://worldguard.enginehub.org) must be installed

## Installation
1. [Create a PaperMC server](https://paper.readthedocs.io/en/latest/server/getting-started.html)
2. [Install WorldGuard](https://worldguard.enginehub.org/en/latest/install/)
3. Download the latest jar from the ["Releases" section on GitHub](https://github.com/RedTheITGuy/SpeedrunnerVsHunters/releases)
4. Place the jar in the plugins folder of the server
5. Run the server, then stop it
6. Edit the con fig (*serverFolder*/plugins/SpeedrunnerVsHunter/config.yml) to your liking
7. Run the server
Now the plugin is installed. Either wait for enough players to join for the auto start, or use /startgame to start a run manually.

## Disclaimer
This plugin was **not** made for public use. Although you are allowed to run plugin on your own server, **I will not guarantee any support for anything related to the plugin**.

This plugin was released under the [GPL-3.0 License](https://github.com/RedTheITGuy/SpeedrunnerVsHunters/blob/master/LICENSE). Any modifications to the code must adhere to this license!
