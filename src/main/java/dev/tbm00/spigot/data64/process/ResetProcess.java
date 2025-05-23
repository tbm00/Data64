package dev.tbm00.spigot.data64.process;

import java.math.BigDecimal;

import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.clip.placeholderapi.PlaceholderAPI;
import me.pulsi_.bankplus.economy.BPEconomy;
import me.pulsi_.bankplus.values.ConfigValues;
import dev.tbm00.spigot.data64.Data64;

public class ResetProcess {
    private Data64 javaPlugin;
    private CommandSender sender;
    private Player player;

    public ResetProcess(Data64 javaPlugin, CommandSender sender, Player player) {
        this.javaPlugin = javaPlugin;
        this.player = player;
        this.sender = sender;

        boolean passedEcoDivide, passedRankHalve, passedJobsHalve;

        if (canProcess()) {
            passedEcoDivide = divideEco();
            passedRankHalve = halveRank();
            passedJobsHalve = halveJobs();
            
            javaPlugin.runCommand("lp user " + player.getName() + " permission set mc.d64.processed true");
            javaPlugin.sendMessage(sender, ChatColor.GREEN + "Reset process for " + player.getName() + " \n" +
                        "passedEcoDivide: " + passedEcoDivide + " \n" +
                        "passedRankHalve: " + passedRankHalve + " \n" +
                        "passedJobsHalve: " + passedJobsHalve);
        }
    }

    /**
     * Checks if the player's data can be reset.
     * (not applicable if already processed or newbie)
     *
     * @return {@code true} if the player is able to be processed, {@code false} otherwise.
     */
    private boolean canProcess() {
        if (player.hasPermission("mc.d64.newbie")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "Reset process for " + player.getName() + " prevented by prior NEWBIE status!");
            return false;
        }

        if (player.hasPermission("mc.d64.processed")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "Reset process for " + player.getName() + " prevented by prior PROCESSED status!");
            sendNotif(player);
            return false;
        }

        int current_play_ticks;
        try {
            current_play_ticks = player.getStatistic(Statistic.valueOf("PLAY_ONE_MINUTE"));
        } catch (Exception e) {
            try {
                current_play_ticks = player.getStatistic(Statistic.valueOf("PLAY_ONE_TICK"));
            } catch (Exception e2) {
                javaPlugin.log(ChatColor.RED, "Caught exception getting player statistic PLAY_ONE_MINUTE: " + e.getMessage());
                javaPlugin.log(ChatColor.RED, "Caught exception getting player statistic PLAY_ONE_TICK: " + e2.getMessage());
                javaPlugin.sendMessage(sender, ChatColor.RED + "Reset process for " + player.getName() + " started with a playtime error!");
                current_play_ticks = 3600;
            }
        } if (current_play_ticks < 3600) {
            javaPlugin.runCommand("lp user " + player.getName() + " permission set mc.d64.newbie true");
            javaPlugin.sendMessage(sender, ChatColor.WHITE + "Reset process for " + player.getName() + " prevented by new NEWBIE status!");
            return false;
        }
        
        javaPlugin.sendMessage(sender, ChatColor.WHITE + "Reset process for " + player.getName() + " starting..!");
        sendNotif(player);
        return true;
    }

    /**
     * Divides the player's total economy balance (pocket + bank) gradually.
     *
     * @return {@code true} if the economy values were successfully parsed and updated, {@code false} otherwise.
     */
    private boolean divideEco() {
        String pocketString = parsePH("%vault_eco_balance%");
        String bankString = parsePH("%bankplus_balance_long%");

        Double pocket, bank;
        try {
            pocket = Double.parseDouble(pocketString);
            bank = Double.parseDouble(bankString);
        } catch (Exception e) {
            javaPlugin.log(ChatColor.RED, "Caught exception parsing economy double from placeholder: " + e.getMessage());
            return false;
        }

        // Combine pocket+bank & save the ratio
        Double total = pocket + bank;
        double pRatio = pocket / total;

        // Divide total value
        double mid = 8e6, // Midpoint
                k = 0.9,  // Steepness
                lb = 0.08, // Lowerbound
                ub = 0.4; // Upperbound
        double keptRatio = lb + (ub - lb) / (1 + Math.pow(total / mid, k));
        total = total * keptRatio;
        //total = total/10;

        // Apply values with correct ratio
        pocket = total * pRatio;
        bank = total - (total * pRatio);

        // Round the values
        int pocketInt = (int) Math.round(pocket);
        int bankInt = (int) Math.round(bank);

        BPEconomy economy = BPEconomy.get(ConfigValues.getMainGuiName());
        BigDecimal bankDec = BigDecimal.valueOf((double) bankInt);

        javaPlugin.runCommand("eco set " + player.getName() + " " + pocketInt);
        economy.setBankBalance(player, bankDec);
        javaPlugin.sendMessage(sender, ChatColor.YELLOW + "Reset " + player.getName() + "'s pocket: $" + pocketString + " -> $" + pocketInt);
        javaPlugin.sendMessage(sender, ChatColor.YELLOW + "Reset " + player.getName() + "'s pocket: $" + bankString + " -> $" + bankInt);
        return true;
    }


    /**
     * Halves the player's rank based on their current rank.
     *
     * @return {@code true} if the player's rank was successfully halved, {@code false} otherwise.
     */
    private boolean halveRank() {
        String nextRank, rank = parsePH("%luckperms_current_group_on_track_rank%");
        int extraRows = 0;
        
        switch (rank) {
            case "thirtyfive":
                nextRank = "eighteen";
                extraRows = 3;
                break;
            case "thirtyfour":
                nextRank = "seventeen";
                extraRows = 3;
                break;
            case "thirtythree":
                nextRank = "seventeen";
                extraRows = 3;
                break;
            case "thirtytwo":
                nextRank = "sixteen";
                extraRows = 3;
                break;
            case "thirtyone":
                nextRank = "sixteen";
                extraRows = 3;
                break;
            case "thirty":
                nextRank = "fifteen";
                extraRows = 3;
                break;
            case "twentynine":
                nextRank = "fifteen";
                extraRows = 3;
                break;
            case "twentyeight":
                nextRank = "fourteen";
                extraRows = 3;
                break;
            case "twentyseven":
                nextRank = "fourteen";
                extraRows = 3;
                break;
            case "twentysix":
                nextRank = "thirteen";
                extraRows = 3;
                break;
            case "twentyfive":
                nextRank = "thirteen";
                extraRows = 2;
                break;
            case "twentyfour":
                nextRank = "twelve";
                extraRows = 2;
                break;
            case "twentythree":
                nextRank = "twelve";
                extraRows = 2;
                break;
            case "twentytwo":
                nextRank = "eleven";
                extraRows = 2;
                break;
            case "twentyone":
                nextRank = "eleven";
                extraRows = 2;
                break;
            case "twenty":
                nextRank = "ten";
                extraRows = 2;
                break;
            case "nineteen":
                nextRank = "ten";
                extraRows = 2;
                break;
            case "eighteen":
                nextRank = "nine";
                extraRows = 1;
                break;
            case "seventeen":
                nextRank = "nine";
                extraRows = 1;
                break;
            case "sixteen":
                nextRank = "eight";
                extraRows = 1;
                break;
            case "fifteen":
                nextRank = "eight";
                extraRows = 1;
                break;
            case "fourteen":
                nextRank = "seven";
                extraRows = 1;
                break;
            case "thirteen":
                nextRank = "seven";
                extraRows = 1;
                break;
            case "twelve":
                nextRank = "six";
                extraRows = 1;
                break;
            case "eleven":
                nextRank = "six";
                break;
            case "ten":
                nextRank = "five";
                break;
            case "nine":
                nextRank = "five";
                break;
            case "eight":
                nextRank = "four";
                break;
            case "seven":
                nextRank = "four";
                break;
            case "six":
                nextRank = "three";
                break;
            case "five":
                nextRank = "three";
                break;
            case "four":
                nextRank = "two";
                break;
            case "three":
                nextRank = "two";
                break;
            case "two":
                nextRank = "one";
                break;
            default:
                return false;
        }
        if (extraRows!=0) {
            switch (extraRows) {
                case 3:
                    javaPlugin.runCommand("lp user " + player.getName() + " permission set group.ender3");
                    break;
                case 2: {
                    if (!player.hasPermission("group.ender3")) {
                        javaPlugin.runCommand("lp user " + player.getName() + " permission set group.ender2");
                    }
                    break;
                }
                case 1: {
                    if (!player.hasPermission("group.ender2") && !player.hasPermission("group.ender3")) {
                        javaPlugin.runCommand("lp user " + player.getName() + " permission set group.ender1");
                    }
                    break;
                }
                default:
                    break;
            }
        }
        javaPlugin.runCommand("lp user " + player.getName() + " permission unset group." + rank);
        javaPlugin.runCommand("lp user " + player.getName() + " permission set group." + nextRank + " true");
        javaPlugin.sendMessage(sender, ChatColor.YELLOW + "Reset " + player.getName() + "'s rank: " + rank + " -> " + nextRank);
        return true;
    }

    /**
     * Halves the level of each job the player has.
     *
     * @return {@code true} after checking and halving all jobs.
     */
    private boolean halveJobs() {
        Integer lvl = 0;

        String lvlBrewer = parsePH("%jobsr_user_jlevel_brewer%");
        if (!lvlBrewer.equals("0")) {
            lvl = Integer.parseInt(lvlBrewer);
            lvl = lvl / 2;
            javaPlugin.runCommand("jobs exp " + player.getName() + " Brewer set 0");
            javaPlugin.runCommand("jobs lvl " + player.getName() + " Brewer set " + lvl);
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "Reset " + player.getName() + "'s Brewer lvl: " + lvlBrewer + " -> " + lvl);
        }
    
        String lvlDigger = parsePH("%jobsr_user_jlevel_digger%");
        if (!lvlDigger.equals("0")) {
            lvl = Integer.parseInt(lvlDigger);
            lvl = lvl / 2;
            javaPlugin.runCommand("jobs exp " + player.getName() + " Digger set 0");
            javaPlugin.runCommand("jobs lvl " + player.getName() + " Digger set " + lvl);
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "Reset " + player.getName() + "'s Digger lvl: " + lvlDigger + " -> " + lvl);
        }
    
        String lvlFarmer = parsePH("%jobsr_user_jlevel_farmer%");
        if (!lvlFarmer.equals("0")) {
            lvl = Integer.parseInt(lvlFarmer);
            lvl = lvl / 2;
            javaPlugin.runCommand("jobs exp " + player.getName() + " Farmer set 0");
            javaPlugin.runCommand("jobs lvl " + player.getName() + " Farmer set " + lvl);
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "Reset " + player.getName() + "'s Farmer lvl: " + lvlFarmer + " -> " + lvl);
        }
    
        String lvlFisherman = parsePH("%jobsr_user_jlevel_fisherman%");
        if (!lvlFisherman.equals("0")) {
            lvl = Integer.parseInt(lvlFisherman);
            lvl = lvl / 2;
            javaPlugin.runCommand("jobs exp " + player.getName() + " Fisherman set 0");
            javaPlugin.runCommand("jobs lvl " + player.getName() + " Fisherman set " + lvl);
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "Reset " + player.getName() + "'s Fisherman lvl: " + lvlFisherman + " -> " + lvl);
        }

        String lvlHunter = parsePH("%jobsr_user_jlevel_hunter%");
        if (!lvlHunter.equals("0")) {
            lvl = Integer.parseInt(lvlHunter);
            lvl = lvl/2;
            javaPlugin.runCommand("jobs exp "+player.getName()+" Hunter set 0");
            javaPlugin.runCommand("jobs lvl "+player.getName()+" Hunter set " + lvl);
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "Reset " + player.getName() + "'s Hunter lvl: " + lvlHunter + " -> " + lvl);
        }
    
        String lvlMiner = parsePH("%jobsr_user_jlevel_miner%");
        if (!lvlMiner.equals("0")) {
            lvl = Integer.parseInt(lvlMiner);
            lvl = lvl / 2;
            javaPlugin.runCommand("jobs exp " + player.getName() + " Miner set 0");
            javaPlugin.runCommand("jobs lvl " + player.getName() + " Miner set " + lvl);
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "Reset " + player.getName() + "'s Miner lvl: " + lvlMiner + " -> " + lvl);
        }
    
        String lvlWoodcutter = parsePH("%jobsr_user_jlevel_woodcutter%");
        if (!lvlWoodcutter.equals("0")) {
            lvl = Integer.parseInt(lvlWoodcutter);
            lvl = lvl / 2;
            javaPlugin.runCommand("jobs exp " + player.getName() + " Woodcutter set 0");
            javaPlugin.runCommand("jobs lvl " + player.getName() + " Woodcutter set " + lvl);
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "Reset " + player.getName() + "'s Woodcutter lvl: " + lvlWoodcutter + " -> " + lvl);
        }
        
        return true;
    }

    /**
     * Sends target a pre-defined notification later.
     * 
     * @param player the player to message
     */
    private void sendNotif(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player!=null) {
                    javaPlugin.sendMessage(player, "&6Hey @&n"+player.getName()+"&r&6, we recently released our biggest update ever:\n" +
                        "&e - deflated economy and ranks (divided EVERYONE's fairly)\n" +
                        "&e - replaced AdminShop with player-to-player /shop\n" +
                        "&e - added Bedrock support\n" +
                        "&e - built a new /market\n" +
                        "&e + added new /quests, VoiceChat mod, gambling /games, and more\n" +
                        "&6Check out #annoucements on &l&a/discord &r&6to learn more!");
                }
            }
        }.runTaskLater(javaPlugin, 300);
    }

    /**
     * Parses target's placeholder a message to a target CommandSender.
     * 
     * @param placeholder the placeholder to parse
     * @return the String value of the player's placeholder
     */
    private String parsePH(String placeholder) {
        return PlaceholderAPI.setPlaceholders(player, placeholder);
    }
}