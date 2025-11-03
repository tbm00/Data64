# Data64
A spigot plugin that manages player data in various ways.

Created by tbm00 for play.mc64.wtf.


## Features
- Reset (scale-back) players' pocket balances, bank balances, ranks, job levels, and shop balances.
- Transfer data from player to player:
  - rank/perms, inv, ec, pocket, bank, playershops64, claims, claim blocks, sethomes, warps, gang, pets, and job stats


## Dependencies
- **Java 17+**: REQUIRED
- **Spigot 1.18.1+**: UNTESTED ON OLDER VERSIONS
- **PlaceholderAPI**: REQUIRED
- **LuckPerms**: REQUIRED
- **EssentialsX**: REQUIRED
- **Vault**: REQUIRED
- **BankPlus**: REQUIRED
- **PlayerShops64**: REQUIRED
- **PlayerWarps**: REQUIRED
- **GriefDefender**: REQUIRED
- **Jobs Reborn**: REQUIRED
- **GangsPlus**: REQUIRED
- **MyPet**: REQUIRED
- **PVPStats**: REQUIRED


## Commands
- `/dataadmin reset <player>` Reset a player's economy, rank, and job levels
- `/dataadmin transfer <playerFrom> <playerTo>` Transfer player's data to another account


## Permissions
- `data64.cmd.reset` Ability to use the /reset command *(default: op)*
- `data64.cmd.transfer` Ability to use the /reset command *(default: op)*