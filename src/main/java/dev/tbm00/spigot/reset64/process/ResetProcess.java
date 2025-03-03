package dev.tbm00.spigot.reset64.process;

import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.clip.placeholderapi.PlaceholderAPI;

import dev.tbm00.spigot.reset64.Reset64;

public class ResetProcess {
    private Reset64 javaPlugin;
    private CommandSender sender;
    private Player player;
    private boolean passedEcoDivide;
    private boolean passedRankHalve;
    private boolean passedJobsHalve;

    public ResetProcess(Reset64 javaPlugin, CommandSender sender, Player player) {
        this.javaPlugin = javaPlugin;
        this.player = player;
        this.sender = sender;

        if (canProcess()) {
            passedEcoDivide = divideEco();
            passedRankHalve = halveRank();
            passedJobsHalve = halveJobs();
            javaPlugin.runCommand("lp user " + player.getName() + " permission set mc.r64.processed true");
            javaPlugin.sendMessage(sender, ChatColor.GREEN + "Reset process for " + player.getName() + " \n" +
                        "passedEcoDivide: " + passedEcoDivide + " \n" +
                        "passedRankHalve: " + passedRankHalve + " \n" +
                        "passedJobsHalve: " + passedJobsHalve);
        }
    }

    /**
     * Checks if the player's data can be reset.
     * (not applicable if already checked, reset, or newbie)
     *
     * @return {@code true} if the player is able to be processed, {@code false} otherwise.
     */
    private boolean canProcess() {
        if (player.hasPermission("mc.r64.checked")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "Reset process for " + player.getName() + " prevented by CHECKED status!");
            return false;
        }

        if (player.hasPermission("mc.r64.processed")) {
            javaPlugin.sendMessage(sender, ChatColor.RED + "Reset process for " + player.getName() + " prevented by PROCESSED status!");
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
                javaPlugin.sendMessage(sender, ChatColor.RED + "Reset process for " + player.getName() + " prevented by playtime error!");
                return false;
            }
        } if (current_play_ticks < 3600) {
            javaPlugin.runCommand("lp user " + player.getName() + " permission set mc.r64.checked true");
            javaPlugin.sendMessage(sender, ChatColor.WHITE + "Reset process for " + player.getName() + " prevented by NEWBIE status!");
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

        javaPlugin.runCommand("eco set " + player.getName() + " " + pocketInt);
        javaPlugin.runCommand("bp set " + player.getName() + " " + bankInt);
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
        int createLimit;
        
        switch (rank) {
            case "thirtyfive":
                nextRank = "eighteen";
                createLimit = 24;
                break;
            case "thirtyfour":
                nextRank = "seventeen";
                createLimit = 24;
                break;
            case "thirtythree":
                nextRank = "seventeen";
                createLimit = 24;
                break;
            case "thirtytwo":
                nextRank = "sixteen";
                createLimit = 24;
                break;
            case "thirtyone":
                nextRank = "sixteen";
                createLimit = 24;
                break;
            case "thirty":
                nextRank = "fifteen";
                createLimit = 20;
                break;
            case "twentynine":
                nextRank = "fifteen";
                createLimit = 20;
                break;
            case "twentyeight":
                nextRank = "fourteen";
                createLimit = 20;
                break;
            case "twentyseven":
                nextRank = "fourteen";
                createLimit = 20;
                break;
            case "twentysix":
                nextRank = "thirteen";
                createLimit = 20;
                break;
            case "twentyfive":
                nextRank = "thirteen";
                createLimit = 20;
                break;
            case "twentyfour":
                nextRank = "twelve";
                createLimit = 20;
                break;
            case "twentythree":
                nextRank = "twelve";
                createLimit = 16;
                break;
            case "twentytwo":
                nextRank = "eleven";
                createLimit = 16;
                break;
            case "twentyone":
                nextRank = "eleven";
                createLimit = 16;
                break;
            case "twenty":
                nextRank = "ten";
                createLimit = 16;
                break;
            case "nineteen":
                nextRank = "ten";
                createLimit = 16;
                break;
            case "eighteen":
                nextRank = "nine";
                createLimit = 16;
                break;
            case "seventeen":
                nextRank = "nine";
                createLimit = 16;
                break;
            case "sixteen":
                nextRank = "eight";
                createLimit = 12;
                break;
            case "fifteen":
                nextRank = "eight";
                createLimit = 12;
                break;
            case "fourteen":
                nextRank = "seven";
                createLimit = 12;
                break;
            case "thirteen":
                nextRank = "seven";
                createLimit = 12;
                break;
            case "twelve":
                nextRank = "six";
                createLimit = 12;
                break;
            case "eleven":
                nextRank = "six";
                createLimit = 12;
                break;
            case "ten":
                nextRank = "five";
                createLimit = 12;
                break;
            case "nine":
                nextRank = "five";
                createLimit = 6;
                break;
            case "eight":
                nextRank = "four";
                createLimit = 6;
                break;
            case "seven":
                nextRank = "four";
                createLimit = 6;
                break;
            case "six":
                nextRank = "three";
                createLimit = 6;
                break;
            case "five":
                nextRank = "three";
                createLimit = 6;
                break;
            case "four":
                nextRank = "two";
                createLimit = 6;
                break;
            case "three":
                nextRank = "two";
                createLimit = 6;
                break;
            case "two":
                nextRank = "one";
                createLimit = 4;
                break;
            default:
                return false;
        }
        javaPlugin.runCommand("lp user " + player.getName() + " permission set meta.griefdefender\\.create-limit." + createLimit);
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
                    javaPlugin.sendMessage(player, "&l&bHey! &rIn our latest update, we overhauled our economy to be significantly more competitive and player-driven. \n" +
                        "After a lot of thought, we decided to deflate everyone's pocket balance, bank balance, and rank, as well as completely remove the AdminShop and nerf job income. Now when you do /shop, it will pull up a category GUI for you to find other players' shops to buy from/sell to. With these changes, we suspect new and old shop owners will be the richest! \n" +
                        "We've also re-added /coinflip and /bounty, as well as started rebuilding the /market in a new location -- come get a plot!");
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