package fr.ribesg.alix.api.bot.command;
import fr.ribesg.alix.api.Channel;
import fr.ribesg.alix.api.Receiver;
import fr.ribesg.alix.api.Server;
import fr.ribesg.alix.api.Source;
import fr.ribesg.alix.api.enums.Codes;

public class HelpCommand extends Command {

	private final CommandManager manager;

	public HelpCommand(final CommandManager manager) {
		super("help", new String[] {
				"Get help about a command, or list every commands",
				"Usage: ##<.<command>| [command]>"
		}, "h");
		this.manager = manager;
	}

	@Override
	public boolean exec(final Server server, final Channel channel, final Source user, final String primaryArgument, final String[] args) {
		final Receiver receiver = channel == null ? user : channel;
		if (args.length == 1 && primaryArgument != null || args.length > 1) {
			return false;
		}
		final String arg = args.length == 1 ? args[0] : primaryArgument;
		if (arg != null) {
			final String cmdName = arg.toLowerCase();
			final String realCmd = this.manager.aliases.get(cmdName) == null ? cmdName : this.manager.aliases.get(cmdName);
			final Command cmd = this.manager.commands.get(realCmd);
			if (cmd == null) {
				receiver.sendMessage(Codes.RED + "Unknown command: " + cmdName);
			} else {
				cmd.sendUsage(this.manager.getCommandPrefix(), receiver);
			}
		} else {
			if (channel != null) {
				channel.sendMessage(Codes.RED + user.getName() + ", check your private messages");
			}
			for (final Command cmd : this.manager.commands.values()) {
				cmd.sendUsage(this.manager.getCommandPrefix(), user);
			}
		}
		return true;
	}
}
