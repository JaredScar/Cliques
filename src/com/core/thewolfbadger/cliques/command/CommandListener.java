package com.core.thewolfbadger.cliques.command;

import com.core.thewolfbadger.cliques.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: TheWolfBadger
 * Date: 7/19/14
 * Time: 1:44 PM
 */
public class CommandListener implements CommandExecutor, Listener {
    Main m;
    public CommandListener(Main m) {
        this.m = m;
    }
    private HashMap<UUID, String> invites = new HashMap<UUID, String>();
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(m.getRating(e.getPlayer()) == -1553D) {
            m.setRating(e.getPlayer(), 1500D);
        }
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if(e.getEntity().getKiller() !=null) {
                double d = m.getRating(e.getEntity())*m.getSettings().getDouble("PercentGainedFromKill");
                double toSet2 = m.getRating(e.getEntity().getKiller())+d;
                m.setRating(e.getEntity().getKiller(), toSet2);
                e.getEntity().getKiller().sendMessage(ChatColor.translateAlternateColorCodes('&', m.getSettings().getString("Messages.GainedMoreRatingFromKill")).replace("{KILLED}", e.getEntity().getName()));
        }
        double i = m.getRating(e.getEntity())*m.getSettings().getDouble("PercentLostFromDeath");
        double toSet = m.getRating(e.getEntity())-i;
        m.setRating(e.getEntity(), toSet);
        e.getEntity().sendMessage(ChatColor.translateAlternateColorCodes('&', m.getSettings().getString("Messages.LostRatingFromDeath")).replace("{KILLER}", e.getEntity().getKiller().getName()));
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(cmd.getName().equalsIgnoreCase("clique")) {
            if(sender instanceof Player) {
            Player p = (Player) sender;
            switch (args.length) {
                case 0:
                    //Help menu
                    p.sendMessage(ChatColor.GOLD+
                            "./clique - Lists Clique plugin's commands. \n" +  ChatColor.GRAY +
                            "./clique rating - Describes your current rating.  \n" + ChatColor.GOLD +
                            "./clique rating <clique> - Describes specified Clique's rating. \n" + ChatColor.GRAY +
                            "./clique board <page> - Shows Cliques in order from Highest to Lowest Ratings. (6 Cliques per page) \n" + ChatColor.GOLD +
                            "./clique promote <player> - Promote the specified member to a leader. \n" + ChatColor.GRAY +
                            "./clique demote <player> - Demote the specified leader to a member. \n" +  ChatColor.GOLD +
                            "./clique kick <player> - Kick the specified player from your clique. \n" + ChatColor.GRAY +
                            "./clique close - Close the clique if you are the creator. \n" + ChatColor.GOLD +
                            "./clique create <clique> - Create a clique with the specified name. \n" + ChatColor.GRAY +
                            "./clique join <clique> - Join the specified clique if you were invited. \n" + ChatColor.GOLD +
                            "./clique invite <player> - Invite the specified player to your clique. \n" + ChatColor.GRAY +
                            "./clique ff <true/false> - Allow or Deny friendly fire within the clique.");
                    break;
                case 1:
                    if(args[0].equalsIgnoreCase("rating")) {
                        if(m.hasClique(p)) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getSettings().getString("BoardFormat")).replace("{CLIQUE}", m.getClique(p)).replace("{RATING}", String.valueOf(m.getCliqueRating(m.getClique(p)))));
                            p.sendMessage(ChatColor.AQUA+
                                    "          *** How to get Ratings up ***\n" +   ChatColor.GRAY+
                                    " Clique Ratings is determined by the average of all the Player's individual Ratings binded together. (All members' ratings added up together) divided by amount of members.");
                        } else {
                            p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You do not have a Clique!");
                        }
                    } else
                    if(args[0].equalsIgnoreCase("close")) {
                        if(m.hasClique(p)) {
                            if(m.isOwner(p, m.getClique(p))) {
                                m.closeClique(p, m.getClique(p));
                            } else {
                                p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You are not the owner of your Clique!");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You do not have a Clique!");
                        }
                    } else
                    if(args[0].equalsIgnoreCase("board")) {
                        m.openBoardPage(p, 1);
                    }
                    break;
                case 2:
                    if(args[0].equalsIgnoreCase("create")) {
                        if(!m.hasClique(p)) {
                            m.makeClique(p, args[1]);
                        } else {
                            p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You already have a Clique!");
                        }
                    } else
                    if(args[0].equalsIgnoreCase("rating")) {
                        if(m.cliqueExists(args[1])) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getSettings().getString("BoardFormat")).replace("{CLIQUE}", args[1]).replace("{RATING}", String.valueOf(m.getCliqueRating(args[1]))));
                        } else {
                            p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"That Clique does not exist!");
                        }
                    } else
                    if(args[0].equalsIgnoreCase("board")) {
                        m.openBoardPage(p, Integer.parseInt(args[1]));
                    } else
                    if(args[0].equalsIgnoreCase("promote")) {
                        if(m.hasClique(p)) {
                            if(m.isLeader(p, m.getClique(p))) {
                                if(Bukkit.getPlayer(args[1]) !=null) {
                                    Player pl = Bukkit.getPlayer(args[1]);
                                    if(m.hasClique(pl)) {
                                        if(m.getClique(pl).equals(m.getClique(p))) {
                                            if(!m.isLeader(pl, m.getClique(pl))) {
                                                m.promote(p, args[1], m.getClique(p));
                                            } else {
                                                p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"This player is already a leader of your Clique!");
                                            }
                                        } else {
                                            p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"This player is not in your Clique!");
                                        }
                                    } else {
                                        p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"This player is not in your Clique!");
                                    }
                                } else {
                                    p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You can not promote an online player!");
                                }
                            } else {
                                p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You are not a leader nor a owner of your Clique!");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You do not have a Clique!");
                        }
                    } else
                    if(args[0].equalsIgnoreCase("demote")) {
                        if(m.hasClique(p)) {
                            if(m.isLeader(p, m.getClique(p))) {
                                if(Bukkit.getPlayer(args[1]) !=null) {
                                    Player pl = Bukkit.getPlayer(args[1]);
                                    if(m.hasClique(pl)) {
                                        if(m.getClique(pl).equals(m.getClique(p))) {
                                            if(!m.isLeader(pl, m.getClique(pl))) {
                                m.demoteLeader(p, args[1], m.getClique(p));
                                            } else {
                                                p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You can not demote another leader of the Clique!!");
                                            }
                                        } else {
                                            p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"This player is not in your Clique!");
                                        }
                                    } else {
                                        p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"This player is not in your Clique!");
                                    }
                                } else {
                                    p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You can not demote an offline player!");
                                }
                            } else {
                                p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You are not a leader nor a owner of your Clique!");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You do not have a Clique!");
                        }
                    } else
                    if(args[0].equalsIgnoreCase("invite")) {
                        if(m.hasClique(p)) {
                            if(m.isLeader(p, m.getClique(p))) {
                                if(Bukkit.getPlayer(args[1]) !=null) {
                                    //Online
                                    if(m.hasClique(Bukkit.getPlayer(args[1]))) {
                                        if(!m.getClique(p).equals(m.getClique(Bukkit.getPlayer(args[1])))) {
                                            invites.put(Bukkit.getPlayer(args[1]).getUniqueId(), m.getClique(p));
                                            //Message
                                            Bukkit.getPlayer(args[1]).sendMessage(ChatColor.translateAlternateColorCodes('&', m.getSettings().getString("Messages.YouHaveBeenInvited")).replace("{CLIQUE}", m.getClique(p)));
                                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getSettings().getString("Messages.YouHaveInvitedPlayer")).replace("{INVITED}", args[1]));
                                        } else {
                                            p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"This player is already in your Clique!");
                                        }
                                    }
                                } else {
                                    p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You can not invite an offline player to your Clique!");
                                }
                            } else {
                                p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You are not a leader nor a owner of your Clique!");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You do not have a Clique!");
                        }
                    } else
                    if(args[0].equalsIgnoreCase("join")) {
                        if(!m.hasClique(p)) {
                            if(invites.containsKey(p.getUniqueId())) {
                                if(invites.get(p.getUniqueId()).equals(args[1])) {
                                    m.addPlayer(p, invites.get(p.getUniqueId()));
                                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getSettings().getString("Messages.YouHaveJoined")).replace("{CLIQUE}", m.getClique(p)));
                                    //TODO Send message to Owner and the members of the Clique
                                    invites.remove(p.getUniqueId());
                                } else {
                                    p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You do not have a invite to this Clique!");
                                }
                            } else {
                                p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You do not have any pending invites!");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You already have a Clique!");
                        }
                    } else
                    if(args[0].equalsIgnoreCase("ff")) {
                        if(m.hasClique(p)) {
                            if(m.isLeader(p, m.getClique(p))) {
                                m.toggleFF(p, m.getClique(p), Boolean.parseBoolean(args[1]));
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getSettings().getString("Messages.ToggledFF")).replace("{CLIQUE}", m.getClique(p)).replace("{VALUE}", args[1].toUpperCase()));
                            } else {
                                p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You are not a leader nor a owner of your Clique!");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You do not have a Clique!");
                        }
                    } else
                    if(args[0].equalsIgnoreCase("kick")) {
                        if(m.hasClique(p)) {
                            if(m.isLeader(p, m.getClique(p))) {
                                if(Bukkit.getPlayer(args[1]) !=null) {
                                    if(m.hasClique(Bukkit.getPlayer(args[1]))) {
                                        if(m.getClique(Bukkit.getPlayer(args[1])).equals(m.getClique(p))) {
                                            if(!m.isOwner(Bukkit.getPlayer(args[1]), m.getClique(Bukkit.getPlayer(args[1])))) {
                                m.kickPlayer(args[1], p, m.getClique(p));
                                            }
                                        } else {
                                            p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"This player is not in your Clique!");
                                        }
                                    } else {
                                        p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"This player is not in your Clique!");
                                    }
                                } else {
                                    p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You can not kick an offline player!");
                                }
                            } else {
                                p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You are not a leader nor a owner of your Clique!");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED+""+ChatColor.BOLD+"You do not have a Clique!");
                        }
                    }
                    break;
                }
            }
        }
        return true;
    }
}
