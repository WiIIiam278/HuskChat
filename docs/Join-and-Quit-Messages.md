HuskChat supports displaying special **join and quit messages** whenever a player joins/leaves your network (or single-server setup when installing the plugin standalone).

## Usage
To enable this feature, set `join_and_quit_messages.join.enabled` and/or `join_and_quit_messages.quit.enabled` to `true` in your `config.yml` file. You can then modify the `format` of either, which accepts placeholders and standard MineDown formatting.

### Broadcast scopes
You can set the `broadcast_scope` of join and quit messages in a similar fashion to how you can do this for [[Channels]]. See [Broadcast Scopes](channels#channel-scope) for more details on the available scopes.

Note that global, local and regular PASSTHROUGH scopes are only effective when running the plugin on a standalone Spigot/Paper server; when running HuskChat on a proxy (Velocity/Bungee) server, the _regular join/leave message won't be cancelled_. This is because the join/leave message is handled on the backend.

<details>
<summary>Example config.yml</summary>

```yaml
# Options for customizing player join and quit messages
join_and_quit_messages:
  join:
    enabled: false
    # Use the huskchat.join.[text] permission to override this per-group if needed
    format: '&e%name% joined the network'
  quit:
    enabled: false
    # Use the huskchat.quit.[text] permission to override this per-group if needed
    format: '&e%name% left the network'
  broadcast_scope: GLOBAL # Note that on Velocity/Bungee, PASSTHROUGH modes won't cancel local join/quit messages
```
</details>