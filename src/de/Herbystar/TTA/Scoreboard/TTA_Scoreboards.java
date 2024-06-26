package de.Herbystar.TTA.Scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.base.Splitter;

import de.Herbystar.TTA.Main;

public class TTA_Scoreboards {
	
	public Scoreboard board;
	public Objective score;
	public Player player;
	public List<Team> teams = new ArrayList<Team>();
	public HashMap<Team, String> lot = new HashMap<Team, String>();
	public List<String> content = new ArrayList<String>();
	public List<String> title = new ArrayList<String>();
	public List<String> chlist = new ArrayList<String>();
	public HashMap<Integer, String> scores = new HashMap<Integer, String>();
		
	private int index = 15;
	private int titleindex = 0;
	private int maxchars = 32;
	public String replacer;
	
	public TTA_Scoreboards(Player player, List<String> headerContent, List<String> content) {
		this.title = headerContent;
		this.content = content;
		PlayerReceiveBoard e = new PlayerReceiveBoard(this.player, this.content, this.title, this);
		Bukkit.getPluginManager().callEvent(e);
		if(!e.isCancelled()) {
			lines();
			
			if(Main.boards.containsKey(this.player)) {
				((TTA_Scoreboards)Main.boards.get(this.player)).remove();
			}
			
			this.titleindex = e.getTitle().size();
			Main.boards.put(player, this);
			Main.allboards.add(this);
			this.player = player;
			//Check for existing scoreboards
			String scoreboardExists;
			if(player.getScoreboard() == null) {
				this.board = Bukkit.getScoreboardManager().getNewScoreboard();
				scoreboardExists = "New scoreboard";
			} else {
				scoreboardExists = "Existing scoreboard";
				this.board = player.getScoreboard();
				if(this.board.getObjective(DisplaySlot.SIDEBAR) != null) {
					this.board.getObjective(DisplaySlot.SIDEBAR).unregister();
					this.board.clearSlot(DisplaySlot.SIDEBAR);
				}
			}
			if(Main.instance.internalDebug == true) {
				Bukkit.getConsoleSender().sendMessage(scoreboardExists);
			}
			
			this.score = this.board.registerNewObjective("score", "dummy");
			this.score.setDisplaySlot(DisplaySlot.SIDEBAR);
			this.score.setDisplayName((String)e.getTitle().get(0));
			
			for(String s : e.getContent()) {
				Team t;
				if(this.board.getTeam("Team:" + this.index) == null) {
					t = this.board.registerNewTeam("Team:" + this.index);
				} else {
					t = this.board.getTeam("Team:" + this.index);
				}
				String o = s;
				String n = "";
				s = this.check(s);
				if(s.length() > 16) {
			        Iterator<String> iterator = this.Splitter(s);
			        String prefix = iterator.next();
			        t.setPrefix(prefix);
			        n = ChatColor.getLastColors(prefix) + iterator.next();
					if(Main.instance.internalDebug == true) {
						Bukkit.getConsoleSender().sendMessage("n: " + n);
					}

					this.SuffixController(iterator, prefix, s.length(), t);
				} else {
					t.setPrefix(s);
				}
				if(Main.instance.internalDebug == true) {
					Bukkit.getConsoleSender().sendMessage("Prefix: " + t.getPrefix() + " §fName: " + t.getName() + " §fSuffix: " + t.getSuffix());
				}

				String score = n + (String)this.chlist.get(this.index - 1);
				t.addEntry(score);
				
				this.score.getScore(score).setScore(this.index);
				this.scores.put(this.index, score);
				this.teams.add(t);
				this.lot.put(t, o);
				this.index -= 1;
			}
			player.setScoreboard(this.board);
		}			
	}
	   
	private void lines() {
		this.chlist.add("§1");
		this.chlist.add("§2");
		this.chlist.add("§3");
		this.chlist.add("§4");
		this.chlist.add("§5");
		this.chlist.add("§6");
		this.chlist.add("§7");
		this.chlist.add("§8");
		this.chlist.add("§9");
		this.chlist.add("§a");
		this.chlist.add("§b");
		this.chlist.add("§c");
		this.chlist.add("§d");
		this.chlist.add("§e");
		this.chlist.add("§f");
	}
	
	
	public void remove() {
		for(Iterator<Player> m = Main.boards.keySet().iterator(); Main.boards.keySet().iterator().hasNext();) {
			Player pt = m.next();
			if(Main.boards.containsKey(this.player) && this.player.equals(pt)) {
				Main.boards.remove(pt);
				Main.allboards.remove(this);
				this.player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				break;
			}
		}
		
	}
	
	public void hide() {
		this.player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
	}

	public void show() {
		this.player.setScoreboard(board);
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public void updateTitleData(List<String> newTitle) {
		Main.scoreboardtitle.cancel();
		this.title = newTitle;
		this.titleindex = 0;
		Main.instance.startScoreboardsTitle();
	}
	
	public void updateTitle() {
		if(this.title.size() == 0) {
			Main.scoreboardtitle.cancel();
			return;
		}
		if(this.titleindex > this.title.size() - 1) {
			this.titleindex = 0;
		}
		String s = this.title.get(this.titleindex);
		if(s == null) {
			return;
		}
		if(s.length() > this.maxchars) {
			s = s.substring(0, this.maxchars);
		}
		this.score.setDisplayName(s);
		this.titleindex += 1;
		
	}
	
	public void updateRow(int row, String content) {
		try {
			Team t = this.teams.get(row);
			int i = 15-row;
			this.updateRow(t, i, content);
		} catch(IndexOutOfBoundsException ex) {
			Bukkit.getConsoleSender().sendMessage(Main.instance.prefix + "§cRow update failed.");
			Bukkit.getConsoleSender().sendMessage(Main.instance.prefix + "§cRow §6" + row + " §cdoes not exists on scoreboard.");
		}
	}
	
	private void updateRow(Team t, int i, String content) {
		String n = "";
		String s = (String)this.lot.get(t);
		String score = "";
		//Compares row values
		if(content != null) {
			if(s.equals(content)) {
				return;
			} else {
				//Clears old row if longer then 16 chars
				if(s.length() > 16) {
					this.refreshScores(i, score, t);
				}
				s = content;
				this.lot.put(t, content);
			}
		}
		if(Main.instance.internalDebug == true) {
			Bukkit.getConsoleSender().sendMessage("ContentBeforeReplace: " + s);
		}
		if(s.length() > 16) {
	        Iterator<String> iterator = this.Splitter(s);
	        String prefix = iterator.next();
	        t.setPrefix(prefix);
	        n = ChatColor.getLastColors(prefix) + iterator.next();
			if(Main.instance.internalDebug == true) {
				Bukkit.getConsoleSender().sendMessage("n: " + n);
			}
			this.SuffixController(iterator, prefix, s.length(), t);
			score = n + (String)this.chlist.get(i - 1);
			Bukkit.getConsoleSender().sendMessage("Refresh scores  i: " + i + " - Score: " + score + " - t: " + t);
			this.refreshScores(i, score, t);
		} else {
			t.setPrefix(s);
		}
	}
	
	public void updateContent(List<String> rowContents) {
		if(rowContents == null) {
			updateContent();
		}
		for(String content : rowContents) {
			updateRow(rowContents.indexOf(content), content);
		}
	}
	
	private void updateContent() {
		try {
			int i = 15;
			for(Team t : this.teams) {
				Bukkit.getConsoleSender().sendMessage("Team Index: " + this.teams.indexOf(t) + " - i: " + i);
				this.updateRow(t, i, null);
				
				if(Main.instance.internalDebug == true) {
					Bukkit.getConsoleSender().sendMessage(this.player.getName());
					Bukkit.getConsoleSender().sendMessage(t.getEntries().size() + "");
				}
				i -= 1;
				
			}
		} catch(IllegalStateException ex) {
			Bukkit.getConsoleSender().sendMessage(Main.instance.prefix + "§4A Error appeared! (IllegalStateException)");
			Bukkit.getConsoleSender().sendMessage(Main.instance.prefix + "§4Be sure that you have no other Scoreboard plugins installed!");
		}
	}
	
	private void SuffixController(Iterator<String> iterator, String prefix, int length, Team team) {
		if(length > 32) {
			String colorCode = ChatColor.getLastColors(prefix);
			String suffix  = colorCode + iterator.next();
			if(suffix.length() > 16) {
				suffix = suffix.substring(0, 16);
			}
			team.setSuffix(suffix);
		} else {
			team.setSuffix("");
		}
	}
	
	private Iterator<String> Splitter(String s) {
        Iterator<String> iterator = Splitter.fixedLength(16).split(s).iterator();
        return iterator;
	}
	
	private String check(String text) {
		if(text.length() > 48) {
			text = text.substring(0, 47);
		}
		return text;
	}
		
	@SuppressWarnings("unlikely-arg-type")
	private void refreshScores(int scoreCount, String score, Team team) {
		if(this.board.getScores(this.scores.get(scoreCount)).equals(this.score.getScore(score))) {
			return;
		}
		this.board.resetScores(this.scores.get(scoreCount));
		if(!team.getEntries().contains(score)) {
			team.removeEntry(this.scores.get(scoreCount));
			team.addEntry(score);
		}
		this.scores.remove(scoreCount);
		this.score.getScore(score).setScore(scoreCount);
		this.scores.put(scoreCount, score);
	}
	
	/**
	 * Getter
	 */
	public static TTA_Scoreboards getScoreboardByPlayer(Player player) {
		if(Main.boards.containsKey(player)) {
			return Main.boards.get(player);
		}
		return null;
	}
	
	public static Collection<TTA_Scoreboards> getScoreboards() {
		return Main.boards.values();
	}
}
