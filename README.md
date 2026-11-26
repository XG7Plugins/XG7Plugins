# XG7Plugins
## Core Plugin for other plugins of XG7 series

> [!IMPORTANT]
> This plugin requires the PacketEvents-API to function properly. \
> [Download from GitHub](https://github.com/retrooper/packetevents/releases/download/v2.11.1/packetevents-spigot-2.11.1.jar)

Rate us on spigot!
https://www.spigotmc.org/resources/xg7plugins.123062/

Support us:
https://www.patreon.com/cw/XG7Plugins

See some of our projects:
https://github.com/XG7Plugins/XG7Lobby

## Docs:
https://xg7plugins.gitbook.io/doc

## Development

> [!TIP]
> If you only want to use the XG7Plugins-API, you can find the documentation [here](https://xg7plugins.gitbook.io/doc/development/core-api).

### Coding & Compiling

Use Java 21 for compiling the plugin. Yet it is designed to work with Spigot 1.8+ and Java 8+. Anyways we recommend using Paper 1.8+ with Java 21.

### Libraries

Most dependencies are shaded into the plugin.

| Group                         | Artifact                        | Version |
|-------------------------------|---------------------------------|---------|
| com.github.ben-manes.caffeine | caffeine                        | 2.9.3   |
| com.github.cryptomorin        | XSeries                         | 13.6.0  |
| com.mysql                     | mysql-connector-j               | 9.3.0   |
| com.zaxxer                    | HikariCP                        | 4.0.3   |
| net.kyori                     | adventure-api                   | 4.26.1  |
| net.kyori                     | adventure-platform-bukkit       | 4.3.2   |
| net.kyori                     | adventure-platform-bungeecord   | 4.3.2   |
| net.kyori                     | adventure-text-minimessage      | 4.26.1  |
| net.kyori                     | adventure-text-serializer-plain | 4.26.1  |
| org.mariadb.jdbc              | mariadb-java-client             | 3.4.3   |
| redis.clients                 | jedis                           | 5.2.0   |

Due to license conflicts, PacketEvents (GPL-3.0) is not shaded and included in the public builds. \
If you want to include it for yourself, you can change the dependency from `compileOnly` to `implementation` and run `./gradlew build`:

```diff
  dependencies {
  
      compileOnly 'com.github.LoneDev6:api-itemsadder:4301b5d2ea'
-     compileOnly 'com.github.retrooper:packetevents-spigot:2.11.1'
+     implementation 'com.github.retrooper:packetevents-spigot:2.11.1'
      compileOnly 'com.mojang:authlib:1.5.25'
      [...]
  }
```