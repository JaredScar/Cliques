package com.core.thewolfbadger.cliques.main;

import com.core.thewolfbadger.cliques.command.CommandListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: TheWolfBadger
 * Date: 7/19/14
 * Time: 1:23 PM
 */
public class Main extends JavaPlugin {
    CommandListener cmX;
    private FileConfiguration settings;
    public void onEnable() {
        this.cmX = new CommandListener(this);
        getCommand("clique").setExecutor(this.cmX);
        this.saveDefaultConfig();
        this.settings = getConfig();
        this.getServer().getPluginManager().registerEvents(this.cmX, this);
    }
    public FileConfiguration getSettings() {
        return this.settings;
    }
    public void onDisable() {}
    /*
     * Commands:
     *  ./clique - Lists Clique plugin's commands. DONE
     *  ./clique rating - Describes your current rating.  DONE
     *  ./clique rating <clique> - Describes specified Clique's rating. DONE
     *  ./clique board <page> - Shows Cliques in order from Highest to Lowest Ratings. (6 Cliques per page) DONE
     *  ./clique profile - Shows your current Clique's stats and info. DONE
     *  ./clique profile <clique> - Shows specified Clique's stats and info. DONE
     *  ./clique promote <player> - Promote the specified member to a leader. DONE
     *  ./clique demote <player> - Demote the specified leader to a member. DONE
     *  ./clique kick <player> - Kick the specified player from your clique. DONE
     *  ./clique close - Close the clique if you are the creator. DONE
     *  ./clique create <clique> - Create a clique with the specified name. DONE
     *  ./clique join <clique> - Join the specified clique if you were invited. DONE
     *  ./clique invite <player> - Invite the specified player to your clique. DONE
     *  ./clique ff <allow/deny> - Allow or Deny friendly fire within the clique. DONE
     *
     *  How to get Ratings up:
     *   Clique Ratings is determined by the average of all the Player's individual Ratings binded together. (Member1 + Member2) divided by amount of members.
     */
    public void makeClique(Player sender, String name) {
        File f = new File(this.getDataFolder(), File.separator+"cliques.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yCon = YamlConfiguration.loadConfiguration(f);
        List<String> list = yCon.getStringList("CliquesList");
        if(!list.contains(name)) {
        list.add(name);
        yCon.set("CliquesList", list);
        yCon.set("Cliques."+name+".Owner", sender.getUniqueId().toString());
        yCon.set("Cliques."+name+".OwnerName", sender.getName());
        yCon.set("Cliques."+name+".FriendlyFire", false);
        yCon.set("Cliques."+name+".Leaders", new ArrayList<String>());
        List<String> listt = yCon.getStringList("Cliques."+name+".Members");
        listt.add(sender.getUniqueId().toString());
        yCon.set("Cliques."+name+".Members", listt);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getSettings().getString("Messages.MadeClique")).replace("{CLIQUE}", name));
            try {
                yCon.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //Clique exists and will not be made.
            sender.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"That clique already exists!");
        }
    }
    public FileConfiguration getCliques() {
        File f = new File(this.getDataFolder(), File.separator+"cliques.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(f);
    }
    public void toggleFF(Player sender, String clique, Boolean b) {
        File f = new File(this.getDataFolder(), File.separator+"cliques.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yCon = YamlConfiguration.loadConfiguration(f);
        if(yCon.getList("CliquesList").contains(clique)) {
            yCon.set("Cliques."+clique+".FriendlyFire", b);
            try {
                yCon.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void addPlayer(Player p, String clique) {
        File f = new File(this.getDataFolder(), File.separator+"cliques.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yCon = YamlConfiguration.loadConfiguration(f);
        if(yCon.getStringList("CliquesList").contains(clique)) {
            List<String> list = yCon.getStringList("Cliques."+clique+".Members");
            list.add(p.getUniqueId().toString());
            yCon.set("Cliques."+clique+".Members", list);
            try {
                yCon.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void closeClique(Player sender, String name) {
        File f = new File(this.getDataFolder(), File.separator+"cliques.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getSettings().getString("Messages.YouHaveClosedYourClique")).replace("{CLIQUE}", getClique(sender)));
        YamlConfiguration cliques = YamlConfiguration.loadConfiguration(f);
        cliques.set("Cliques."+name, null);
        List<String> list = cliques.getStringList("CliquesList");
        list.remove(name);
        cliques.set("CliquesList", list);
        try {
            cliques.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void kickPlayer(String getKicked, Player sender, String clique) {
        File f = new File(this.getDataFolder(), File.separator+"cliques.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration cliques = YamlConfiguration.loadConfiguration(f);
        if(Bukkit.getPlayer(getKicked) !=null) {
            //Online
            Player p = Bukkit.getPlayer(getKicked);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', getSettings().getString("Messages.YouHaveBeenKicked")).replace("{CLIQUE}", getClique(p)));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getSettings().getString("Messages.YouHaveKickedPlayer")).replace("{PLAYER}", p.getName()));
            List<String> list = cliques.getStringList("Cliques."+clique+".Members");
            List<String> listt = cliques.getStringList("Cliques."+clique+".Leaders");
            if(list.contains(p.getUniqueId().toString())) {
                list.remove(p.getUniqueId().toString());
                try {
                    cliques.save(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(listt.contains(p.getUniqueId().toString())) {
                listt.remove(p.getUniqueId().toString());
                try {
                    cliques.save(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You can only kick a player when they are online!");
        }
    }
    public boolean isOwner(Player p, String clique) {
        File f = new File(this.getDataFolder(), File.separator+"cliques.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration cliques = YamlConfiguration.loadConfiguration(f);
        if(cliques.getString("Cliques."+clique+".Owner").equals(p.getUniqueId().toString())) {
            return true;
        }
        return false;
    }
    public double getCliqueRating(String name) {
        File ratings = new File(this.getDataFolder(), File.separator+"ratings.yml");
        if(!ratings.exists()) {
            try {
                ratings.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File f = new File(this.getDataFolder(), File.separator+"cliques.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration cliques = YamlConfiguration.loadConfiguration(f);
        YamlConfiguration ratingsy = YamlConfiguration.loadConfiguration(ratings);
        for(String strings : cliques.getStringList("CliquesList")) {
            List<String> members = cliques.getStringList("Cliques."+strings+".Members");
            List<Double> rateList = new ArrayList<Double>();
            for(String s : members) {
                double rates = ratingsy.getDouble("Ratings."+s+".Rating");
                rateList.add(rates);
            }
            double last = 0;
            for(Double num : rateList) {
                last = last+num;
            }
            last = last/rateList.size();
            return last;
        }
        return 1500;
    }
    public boolean cliqueExists(String name) {
        File f = new File(this.getDataFolder(), File.separator+"cliques.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yCon = YamlConfiguration.loadConfiguration(f);
        if(yCon.getList("CliquesList").contains(name)) {
            return true;
        }
        return false;
    }
    public void demoteLeader(Player sender, String demote, String clique) {
        if(Bukkit.getPlayer(demote) !=null) {
            //Online
            File f = new File(this.getDataFolder(), File.separator+"cliques.yml");
            if(!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            YamlConfiguration cliques = YamlConfiguration.loadConfiguration(f);
            List<String> list = cliques.getStringList("Cliques."+clique+".Leaders");
            if(list.contains(Bukkit.getPlayer(demote).getUniqueId().toString())) {
                list.remove(Bukkit.getPlayer(demote).getUniqueId().toString());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getSettings().getString("Messages.YouHaveDemotedPlayer")).replace("{DEMOTED}", demote));
                Bukkit.getPlayer(demote).sendMessage(ChatColor.translateAlternateColorCodes('&', getSettings().getString("Messages.YouHaveBeenDemoted")).replace("{DEMOTER}", sender.getName()));
                try {
                    cliques.save(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                sender.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"The specified player is not a leader or is owner of the clique!");
            }
        } else {
            sender.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You can only demote a player if they are online!");
        }
    }
    public String getClique(Player p) {
        File f = new File(this.getDataFolder(), File.separator+"cliques.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yCon = YamlConfiguration.loadConfiguration(f);
        for(String cliques : yCon.getStringList("CliquesList")) {
            if(yCon.getList("Cliques."+cliques+".Members").contains(p.getUniqueId().toString())) {
                return cliques;
            }
        }
        return null;
    }
    public boolean hasClique(Player p) {
        File f = new File(this.getDataFolder(), File.separator+"cliques.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yCon = YamlConfiguration.loadConfiguration(f);
        if(!yCon.getStringList("CliquesList").isEmpty()) {
        for(String cliques : yCon.getStringList("CliquesList")) {
            if(yCon.getList("Cliques."+cliques+".Members").contains(p.getUniqueId().toString())) {
                return true;
                }
            }
        }
        return false;
    }
    @Deprecated
    public void showProfile(Player sender, String clique) {} //TODO Do this later
    public boolean isLeader(Player p, String clique) {
        File f = new File(this.getDataFolder(), File.separator+"cliques.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration cliques = YamlConfiguration.loadConfiguration(f);
        if(cliques.getList("Cliques."+clique+".Leaders").contains(p.getUniqueId().toString()) || cliques.getString("Cliques." + clique + ".Owner").equals(p.getUniqueId().toString())) {
            return true;
        }
        return false;
    }
    public void promote(Player sender, String name, String clique) {
        if(Bukkit.getPlayer(name) !=null) {
            Player p = Bukkit.getPlayer(name);
        File f = new File(this.getDataFolder(), File.separator+"cliques.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yCon = YamlConfiguration.loadConfiguration(f);
        List<String> list = yCon.getStringList("Cliques."+clique+".Leaders");
        if(!list.contains(p.getUniqueId().toString())) {
        list.add(p.getUniqueId().toString());
        yCon.set("Cliques."+clique+".Leaders", list);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getSettings().getString("Messages.RecentlyPromoted")).replace("{LEADER}", sender.getName()));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getSettings().getString("Messages.PromotedThePlayer")).replace("{RECENTLYPROMOTED}", p.getName()));
        try {
            yCon.save(f);
        } catch (IOException e) {
            e.printStackTrace();
                }
            } else {
            sender.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"This player is already a leader of the Clique!");
        }
        } else {
            //Action cannot be done unless the player is online.
            sender.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You can only promote a member if they are online!");
        }
    }
    public void openBoardPage(Player opener, int startPage) {
        File ratings = new File(this.getDataFolder(), File.separator+"ratings.yml");
        if(!ratings.exists()) {
            try {
                ratings.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File f = new File(this.getDataFolder(), File.separator+"cliques.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration cliques = YamlConfiguration.loadConfiguration(f);
        YamlConfiguration ratingsy = YamlConfiguration.loadConfiguration(ratings);
        TreeMap<Double, List<String>> ratingList = new TreeMap<Double, List<String>>();
        for(String cliqs : cliques.getStringList("CliquesList")) {
            //
            //Members
            double last = 0;
            double rate = 0;
            for(String mems : cliques.getStringList("Cliques." + cliqs + ".Members")) {
                rate = ratingsy.getDouble("Ratings."+mems+".Rating")+last;
                last = ratingsy.getDouble("Ratings."+mems+".Rating");
            }
            double setThis = rate/cliques.getStringList("Cliques."+cliqs+".Members").size();
            if(ratingList.containsKey(setThis)) {
                //Have to add to current list
                List<String> list = ratingList.get(setThis);
                list.add(cliqs);
                ratingList.put(setThis, list);
            } else {
                //Not currently on list;
                List<String> list = new ArrayList<String>();
                list.add(cliqs);
                ratingList.put(setThis, list);
            }
        }
        if(ratingList.firstEntry() !=null && ratingList.firstKey() !=null) {
            opener.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getSettings().getString("PageFormat")).replace("{PAGE}", String.valueOf(startPage)));
        } else {
            //Error
            opener.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"ERROR: NON-EXISTING PAGE");
        }
        int stopper = (startPage-1)*6;
        for(Double cRatings : ratingList.descendingKeySet()) {
            if(stopper <= startPage*6) {
                if(ratingList.get(cRatings) !=null) {
                    if(ratingList.get(cRatings).size() > 1) {
                        for(String strings : ratingList.get(cRatings)) {
                            opener.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getSettings().getString("BoardFormat")).replace("{CLIQUE}", strings).replace("{RATING}", String.valueOf(cRatings)));
                            stopper++;
                            }
                        } else {
                        opener.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getSettings().getString("BoardFormat")).replace("{CLIQUE}", ratingList.get(cRatings).get(0)).replace("{RATING}", String.valueOf(cRatings)));
                        stopper++;
                        }
                    }
                }
            }
        }
    @Deprecated
    public double getRating(Player p) {
        File ratings = new File(this.getDataFolder(), File.separator+"ratings.yml");
        if(!ratings.exists()) {
            try {
                ratings.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yCon = YamlConfiguration.loadConfiguration(ratings);
        if(yCon.contains("Ratings."+p.getUniqueId().toString()+".Rating")) {
            return yCon.getDouble("Ratings." + p.getUniqueId().toString() + ".Rating");
        }
        return -1553D;
    }
    public void setRating(Player p, double rating) {
        File ratings = new File(this.getDataFolder(), File.separator+"ratings.yml");
        if(!ratings.exists()) {
            try {
                ratings.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yCon = YamlConfiguration.loadConfiguration(ratings);
        yCon.set("Ratings."+p.getUniqueId().toString()+".Rating", rating);
        try {
            yCon.save(ratings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getOwner(String name) {
        File f = new File(this.getDataFolder(), File.separator+"cliques.yml");
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yCon = YamlConfiguration.loadConfiguration(f);
        List<String> list = yCon.getStringList("CliquesList");
        if(list.contains(name)) {
            return yCon.getString("Cliques."+name+".OwnerName");
        }
        return "NULL";
    }
}
