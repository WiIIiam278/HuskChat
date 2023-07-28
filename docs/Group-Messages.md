Group Messages are a unique feature of HuskChat that lets you send messages to a group of players, instead of just a single player. This is really useful for parties of players working together on group projects.

## Configuring Group Messages
To enable Group Messages, ensure the `group_messages` feature is enabled in the `message_command` section of the config.yml file. You can also set a maximum number of players that can be included in a group message, to prevent players from messaging the entire server!


<details>
  <summary>Config.yml</summary>

```yaml
  group_messages: # Whether to allow sending and replying to a message in a group (/msg User1,User2 <message>)
    enabled: true
    max_size: 10
```
</details>

## Sending and replying in a group
To send a group message, simply use the `/msg` command with a list of players to send to, separated by commas. For example, `/msg User1,User2,User3 Hello!` will send the message `Hello!` to `User1`, `User2` and `User3`. The message sent in chat will have a small (`[+N]`) postfixed to the recipient name to indicate how many players are in the group. Hover over the name to see the full list of players in the group, and click the name to paste the full `/msg` command into your chat window.

The `/r` (reply) command also supports replying to a group message. If you receive a group message from `User1`, `User2` and `User3`, you can reply to all of them by using `/r Hello!`.

If you're stuck in a long or spammy chain of /r messages, you can use `/optoutmsg` to silently leave the conversation.