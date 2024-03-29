The `filtered:` property of a channel lets you specify whether a message sent to a channel should be filtered first by the enabled filters and message replacers defined in the `chat_filters` and `message_replacers` section in the dedicated [`filters.yml`](config-files) file. To use a filter, ensure the channel you want to be filtered has `filtered` enabled and that the chat filters are correctly enabled and configured.

## Replacer
Message replacers will alter the contents of the message, such as by replacing certain character combinations with emoji.

* `emoji_replacer` - Replaces certain character strings with the correct Unicode emoji. Note that if you have the `ascii_filter` enabled, this will still work and display unicode emoji characters in chat.

## Filters
Chat filters will prevent a user from sending a message based on certain conditions.

* `advertising_filter` - Prevents players from sending messages that contain IP or web addresses.
* `caps_filter` - Prevents players from sending messages that are comprised of over a certain specifiable percentage (as a decimal number, 0.0 to 1.0 representing 0% to 100%)
* `spam_filter` - Prevents players from sending messages too fast in chat (i.e. rate limits them). Specify how many messages players should be able to send in a period.
* `profanity_filter` - Uses a profanity-check machine learning algorithm to determine if a message contains English profanity. See below for more information on how to set this up as it requires a bit more work.
* `repeat_filter` - Prevents players from sending repeat messages. Checks against a specifiable number of the players previous messages.
* `ascii_filter` - Prevents players from using non-ASCII (i.e. Unicode/UTF-8) characters in chat. If members of your server need to use non-latin characters when talking in your community's language, you probably want to turn this off.

### Bypassing filters
You can use the `huskchat.bypass_filters` permission to allow a user's messages to not be run through the filters (although messages will still be run through replacers). 

In addition, you can use the `huskchat.ignore_filters.<filter_name>` node to let users bypass specific filters. The bypass will work in all channels.
* `huskchat.ignore_filters.advertising` - Advertising filter
* `huskchat.ignore_filters.caps` - Caps filter
* `huskchat.ignore_filters.spam` - Spam filter
* `huskchat.ignore_filters.profanity` - Profanity filter
* `huskchat.ignore_filters.repeat` - Repeat messages filter
* `huskchat.ignore_filters.ascii` - ASCII filter
* `huskchat.ignore_filters.regex` - Regex filter

You can also disable individual types of replacers with the following permissions:
* `huskchat.ignore_filters.emoji_replacer` - Emoji replacer

## Profanity filter 
The `profanity_filter` uses a Python machine learning algorithm (alt-profanity-check) that uses Scikit-learn to predict whether messages contain profanity. It's imperfect and unable to catch elongated or modified slurs, but it's quite effective (and let's face it, if people are going to be bad actors and use bad language, they'll find a way around any swear filter). The profanity checker is only trained on English words.

### Shared hosts
If you're on a **shared host**, unfortunately, you probably won't be able to use this feature unless your host is utterly amazing and doesn't mind helping you with this. Note that due to the complexities associated with doing this feature, I consider this feature for **advanced users only**, and I can't provide support for setting it up beyond directing you here.

### Setup
To use it, you'll need to install Python 3.8+ and Jep onto your server and ensure that the Jep driver is correctly present in your Java classpath for your system. You can install Jep with `pip install jep`. In addition to Jep, you will also need to run `pip install alt-profanity-check` to install the profanity checker and prerequisites.

You will then need to make sure HuskChat recognises your Jep driver by specifying the path to it if your system doesn't do so automatically. The name of the Jep driver varies based on platform. It's `libjep.so` on Linux, `libjep.jnilib` on macOS, and `jep.dll` on Windows.

You can do this in one of a few ways:
* Adding Jep's library path to your Java library environment variable
    - On Linux, this is `LD_LIBRARY_PATH`.
    - On macOS, this is `DYLD_LIBRARY_PATH`.
    - On Windows, this is `PATH`.
* Adding Jep's driver to your startup command by adding the `-Djava.library.path=<path>` argument
* Adding Jep's driver path to the `library_path` config option provided by HuskChat.

Your path should point to the folder containing the jep driver, not the driver itself. If you get an error when starting the profanity filter, you can try [troubleshooting through Jep's guide](https://github.com/ninia/jep/wiki/FAQ#how-do-i-fix-unsatisfied-link-error-no-jep-in-javalibrarypath).

### Usage
Once you've set up the prerequisites and the server is starting the profanity checker without issue, you can change the
settings. By default, the checker will use `AUTOMATIC` mode to determine if the message contains profanity, but if you'd
like to fine tune how sensitive the checker is, you can set `mode` to `TOLERANCE` and change the `tolerance` value
below. Lower values mean the checker will be more strict.
