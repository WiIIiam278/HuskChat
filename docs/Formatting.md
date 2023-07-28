The channel `format` defines how messages sent on that channel will be formatted. The content of the message sent by the player itself is always appended after the formatting. Note that any uncleared formatting will persist and apply to the message contents. The format of private messages, group private messages and broadcasts can also be customized.

## MineDown
You can make use of [MineDown formatting](https://github.com/Phoenix616/MineDown) to use modern (1.16+) hexadecimal colors and easily
make use of advanced text effects such as gradients. You can embed this formatting within the prefix and suffix contents of LuckPerms groups as well.

## Placeholders
Within channel formats you can make use of the following placeholders, which will be replaced with the formatted text.

### Regular placeholders
* `%name%` - Username
* `%full_name%` - Role prefix, player username & role suffix
* `%prefix%` - Role prefix
* `%suffix%` - Role suffix
* `%role%` - User's (primary) group name
* `%role_display_name%` - User's (primary) group display name
* `%ping%` - User's ping
* `%uuid%` - User's UUID
* `%server%` - Server the user is on (on single-server setups, this is always `server`)
* `%local_players_online%` - Number of players on the server the user is on

### Time placeholders
These display the current system time.
* `%timestamp%` - yyyy/MM/dd HH:mm:ss
* `%current_time%` - HH:mm:ss
* `%current_time_short%` - HH:mm
* `%current_date%` - yyyy/MM/dd
* `%current_date_uk%` - dd/MM/yyyy
* `%current_date_day%` - dd
* `%current_date_month%` - MM
* `%current_date_year%` - yyyy

### PlaceholderAPI support
By installing [PAPIProxyBridge](https://modrinth.com/plugin/papiproxybridge) on both your proxy (Bungee or Velocity) and backend (Paper or Fabric) servers, you can use PlaceholderAPI placeholders in channel and message formats.

### Private message placeholders
In private messages, the placeholders are applied to the sender of the message for inbound messages, and the receiver for outbound messages. You can use all placeholders above after the `sender_` or `receiver_` prefix. (e.g. `%sender_(placeholder)%` and `%receiver_(placeholder)%`).

There are additional placeholders for group private messages:
* `%group_amount%` (number of members in the group private message)
* `%group_amount_subscript%` (number of members in the group private message, in subscript font)
* `%group_members_comma_separated%` (comma separated list of members in the group private message)
* `%group_members%` (newline separated list of members in the group private message)

The social spy message formatting lets you format both the message sender and receiver with the same placeholders listed above. The sender and receiver are disambiguated with prefixes, so you can use both; i.e. `%sender_(placeholder)%` and `%receiver_(placeholder)%`.