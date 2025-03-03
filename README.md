# Reset64
A spigot plugin that resets player data.

Created by tbm00 for play.mc64.wtf.


## Features
- Reset (scale-back) players' pocket balance (Vault), bank balance (BankPlus), rank (LuckPerms), job levels (Jobs Reborn), and shop (DisplayShops).


## Dependencies
- **Java 17+**: REQUIRED
- **Spigot 1.18.1+**: UNTESTED ON OLDER VERSIONS
- **PlaceholderAPI**: REQUIRED
- **Vault**: OPTIONAL
- **BankPlus**: OPTIONAL
- **LuckPerms**: OPTIONAL
- **Jobs Reborn**: OPTIONAL
- **DisplayShops**: OPTIONAL


## Commands
#### Player Commands
- none

#### Admin Commands
- `/reset <player>` Reset a player's economy, rank, and job levels


## Permissions
#### Player Permissions
- none

#### Admin Permissions
- `reset64.cmd` Ability to use the /reset command *(default: op)*


## Config
```
# Reset64 v0.0.2-beta by @tbm00
# https://github.com/tbm00/Reset64

enabled: true

lang:
  prefix: "&8[&fR64&8] &7"

resetOnJoin:
  enabled: false

displayShopReset:
  enabled: false
```