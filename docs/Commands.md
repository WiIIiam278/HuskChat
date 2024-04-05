HuskChat provides a number of commands, for switching channels, sending broadcasts and messaging players and groups of players. Channels can also be restricted behind send and receive permission nodes.

## List of Commands
| Command           | Usage                        | Aliases                                            | Description                                                      | Permission                                                  |
|-------------------|------------------------------|----------------------------------------------------|------------------------------------------------------------------|-------------------------------------------------------------|
| `/channel`        | `/channel <name> [message]`  | `/c`                                               | Send a message or switch to a chat channel                       | `huskchat.command.channel`                                  |
| `/huskchat`       | `/huskchat <about\|reload>`  | N/A                                                | View plugin information and reload                               | `huskchat.command.huskchat`                                 |
| `/msg`            | `/msg <player(s)> <message>` | `/m`, `/tell`, `/w`, `/whisper`, `/message`, `/pm` | Send a private message to a player                               | `huskchat.command.msg`                                      |
| `/reply`          | `/reply <message>`           | `/r`                                               | Quickly reply to a private message                               | `huskchat.command.msg.reply`                                |
| `/socialspy`      | `/socialspy [color]`         | `/ss`                                              | Lets you view other users' private messages                      | `huskchat.command.socialspy`                                |
| `/localspy`       | `/localspy [color]`          | `/ls`                                              | Lets you view messages sent in other local chat channels&dagger; | `huskchat.command.localspy`                                 |
| `/broadcast`      | `/broadcast <message>`       | `/alert`                                           | Lets you send a broadcast across the server                      | `huskchat.command.broadcast`                                |
| `/optoutmsg`      | `/optoutmsg`                 | N/A                                                | Lets you "opt-out" of a group private message you are in         | `huskchat.command.optoutmsg`                                |
| Shortcut commands | `/<command> <message>`       | N/A                                                | Quickly send a message in or switch to a chat channel            | Channel send permission, e.g. `huskchat.channel.staff.send` |

&dagger; `/localspy` is not available on single-server Spigot setups.

## Channel send and receive permissions
Channels also have their own permission to send and receive to.

You can configure these in the channel config file, but by default they are `huskchat.channel.<channel>.receive`. Channels without permissions set do not require the permission node to talk in.

## Chat formatting permissions
Formatting messages also has its own permission to allow users to use minedown. 

You can apply the node, `huskchat.formatted_chat`, to allow players to format there messages with any Minedown formatting. Any users attempting to use formatting without the node will simply send the format in chat as a normal message, having the format in the configuration file be used instead.