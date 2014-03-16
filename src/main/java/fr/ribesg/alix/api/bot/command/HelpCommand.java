package fr.ribesg.alix.api.bot.command;
import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.Source;
import fr.ribesg.alix.api.enums.Codes;

public class HelpCommand extends Command {

	public HelpCommand(final CommandManager manager) {
		super(manager, "help", new String[] {"[command] - Get help about a command, or list every commands"}, "h");
	}

	@Override
	public void exec(final Server server, final Channel channel, final Source user, final String[] args) {
		if (args.length > 1) {
			sendUsage(user);
			return;
		}

		if (args.length == 1) {
			final String cmdName = args[0].toLowerCase();
			final String realCmd = manager.aliases.get(cmdName) == null ? cmdName : manager.aliases.get(cmdName);
			final Command cmd = manager.commands.get(realCmd);
			if (cmd == null) {
				user.sendMessage(Codes.RED + "Unknown command: " + cmdName);
				return;
			} else {
				cmd.sendUsage(user);
			}
		} else {
			for (final Command cmd : manager.commands.values()) {
				cmd.sendUsage(user);
			}
		}
	}
}
