package dev.tbm00.spigot.data64.process;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

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

    public ResetProcess(Data64 javaPlugin, CommandSender sender, Player player, boolean force) {
        this.javaPlugin = javaPlugin;
        this.player = player;
        this.sender = sender;

        boolean passedEcoDivide, passedRankHalve, passedJobsHalve;

        if (force || canProcess()) {
            javaPlugin.sendMessage(sender, ChatColor.WHITE + "Reset process for " + player.getName() + " starting..!");

            passedEcoDivide = divideEco();
            passedRankHalve = halveRank();
            passedJobsHalve = halveJobs();
            
            javaPlugin.runCommand("lp user " + player.getName() + " permission set mc.d64.processed true");
            javaPlugin.sendMessage(sender, ChatColor.GREEN + "Reset process for " + player.getName() + " \n" +
                        "passedEcoDivide: " + passedEcoDivide + " \n" +
                        "passedRankHalve: " + passedRankHalve + " \n" +
                        "passedJobsHalve: " + passedJobsHalve);
            sendNotif(player);
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
            //javaPlugin.sendMessage(sender, ChatColor.RED + "Reset process for " + player.getName() + " prevented by prior NEWBIE status!");
            return false;
        }

        if (player.hasPermission("mc.d64.processed")) {
            //javaPlugin.sendMessage(sender, ChatColor.RED + "Reset process for " + player.getName() + " prevented by prior PROCESSED status!");
            //sendNotif(player);
            return false;
        }

        Date firstDate = javaPlugin.logHook.getLogManager().getFirstJoin(player.getName());
        if (firstDate != null && !firstDate.equals(null)) {
            LocalDate localUpdateDate = LocalDate.of(2025, 6, 1);
            Date updateDate = Date.valueOf(localUpdateDate);
            if (firstDate.after(updateDate)) {
                javaPlugin.runCommand("lp user " + player.getName() + " permission set mc.d64.newbie true");
                javaPlugin.sendMessage(sender, ChatColor.WHITE + "Reset process for " + player.getName() + " prevented by new NEWBIE status! (1)");
                return false;
            }
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
            javaPlugin.sendMessage(sender, ChatColor.WHITE + "Reset process for " + player.getName() + " prevented by new NEWBIE status! (2)");
            return false;
        }
        
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
        halveJob("woodcutter", "Woodcutter");
        halveJob("miner", "Miner");
        halveJob("hunter", "Hunter");
        halveJob("fisherman", "Fisherman");
        halveJob("farmer", "farmer");
        halveJob("digger", "Digger");
        halveJob("brewer", "Brewer");
        return true;
    }

    private void halveJob(String job, String jobName) {
        Integer lvl = 0;
        String ogLvl = parsePH("%jobsr_user_jlevel_"+job+"%");
        if (!ogLvl.equals("0")) {
            try {
                lvl = Integer.parseInt(ogLvl);
                lvl = lvl / 2;
            } catch (Exception e1) {
                try {
                    lvl = Integer.valueOf(ogLvl);
                    lvl = lvl / 2;
                } catch (Exception e2) {
                    e2.printStackTrace();
                    return;
                } 
            }
            
            javaPlugin.runCommand("jobs exp " + player.getName() + " "+jobName+" set 0");
            javaPlugin.runCommand("jobs lvl " + player.getName() + " "+jobName+" set " + lvl);
            javaPlugin.sendMessage(sender, ChatColor.YELLOW + "Reset " + player.getName() + "'s "+jobName+" lvl: " + ogLvl + " -> " + lvl);
        }
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
                    javaPlugin.sendMessage(player, "&eHey &6@&n"+player.getName()+"&r&e, in our latest update we significantly defalted the economy and divided EVERYONE's ranks in half! We also added many new fun features -- read more about it in #annoucements on our &l&a/discord&r&e!");
                }
            }
        }.runTaskLater(javaPlugin, 100);
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