/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 */

package fr.ribesg.alix.api.enums;
/**
 * Lists all possible IRC commands defined by the protocol.
 * <p>
 * Sources:
 *
 * @author Ribesg
 * @see <a href="http://www.networksorcery.com/enp/protocol/irc.htm">
 * IRC Protocol @ NetworkSorcery</a>
 * @see <a href="http://www.networksorcery.com/enp/rfc/rfc2812.txt">
 * RFC 2812</a>
 * @see <a href="http://www.networksorcery.com/enp/rfc/rfc2813.txt">
 * RFC 2813</a>
 */
public enum Command {

   /**
    * Get information about the administrator of a server.
    */
   ADMIN,

   /**
    * Set an automatic reply string for any PRIVMSG commands.
    */
   AWAY,

   /**
    * Request a new network to another server immediately.
    */
   CONNECT,

   /**
    * Shutdown the server.
    */
   DIE,

   /**
    * Report a serious or fatal error to a peer.
    */
   ERROR,

   /**
    * Get information describing a server.
    */
   INFO,

   /**
    * Invite a user to a channel.
    */
   INVITE,

   /**
    * Determine if a nickname is currently on IRC.
    */
   IS_ON,

   /**
    * Join a channel.
    */
   JOIN,

   /**
    * Request the forced removal of a user from a channel.
    */
   KICK,

   /**
    * Close a client-server network by the server which has the actual
    * network.
    */
   KILL,

   /**
    * List all servernames which are known by the server answering the
    * query.
    */
   LINKS,

   /**
    * List channels and their topics.
    */
   LIST,

   /**
    * Get statistics about the size of the IRC network.
    */
   LUSERS,

   /**
    * User mode.
    */
   MODE,

   /**
    * Get the Message of the Day.
    */
   MOTD,

   /**
    * List all visible nicknames.
    */
   NAMES,

   /**
    * Define a nickname.
    */
   NICK,

   /**
    * Exchange the list of channel members for each channel between
    * servers.
    */
   NJOIN,

   /**
    * Like {@link #PRIVMSG} but the receiver MUST NOT auto-respond
    */
   NOTICE,

   /**
    * Obtain operator privileges.
    */
   OPER,

   /**
    * Leave a channel.
    */
   PART,

   /**
    * Set a network password.
    */
   PASS,

   /**
    * Test for the presence of an active client or server.
    */
   PING,

   /**
    * Reply to a PING message.
    */
   PONG,

   /**
    * Send private messages between users, as well as to send messages
    * to channels.
    */
   PRIVMSG,

   /**
    * Terminate the client session.
    */
   QUIT,

   /**
    * Force the server to re-read and process its configuration file.
    */
   REHASH,

   /**
    * Force the server to restart itself.
    */
   RESTART,

   /**
    * Register a new server.
    */
   SERVER,

   /**
    * Register a new service.
    */
   SERVICE,

   /**
    * List services currently connected to the network.
    */
   SERVLIST,

   /**
    * Like {@link #PRIVMSG} but the receiver MUST BE a service
    */
   SQUERY,

   /**
    * Disconnect a server link.
    */
   SQUIRT,

   /**
    * Break a local or remote server link.
    */
   SQUIT,

   /**
    * Get server statistics.
    */
   STATS,

   /**
    * Ask a user to join IRC.
    */
   SUMMON,

   /**
    * Get the local time from the specified server.
    */
   TIME,

   /**
    * Change or view the topic of a channel.
    */
   TOPIC,

   /**
    * Find the route to a server and information about it's peers.
    */
   TRACE,

   /**
    * Specify the username, hostname and realname of a new user.
    */
   USER,

   /**
    * Get a list of information about upto 5 nicknames.
    */
   USERHOST,

   /**
    * Get a list of users logged into the server.
    */
   USERS,

   /**
    * Get the version of the server program.
    */
   VERSION,

   /**
    * Send a message to all currently connected users who have set the 'w'
    * user mode.
    */
   WALLOPS,

   /**
    * List a set of users.
    */
   WHO,

   /**
    * Get information about a specific user.
    */
   WHOIS,

   /**
    * Get information about a nickname which no longer exists.
    */
   WHOWAS
}
