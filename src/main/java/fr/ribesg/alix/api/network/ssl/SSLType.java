/*
 * Copyright (c) 2012-2014 Ribesg - www.ribesg.fr
 * This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt
 * Please contact me at ribesg[at]yahoo.fr if you improve this file!
 *
 * Project file:    Alix - Alix - SSLType.java
 * Full Class name: fr.ribesg.alix.api.network.ssl.SSLType
 */

package fr.ribesg.alix.api.network.ssl;
/**
 * Represents a type of SSL handling.
 *
 * @author Ribesg
 */
public enum SSLType {
   /**
    * No SSL at all
    */
   NONE,

   /**
    * Unsecured SSL connection, trusting any certificate
    */
   TRUSTING,

   /**
    * Secured SSL connection
    */
   SECURED
}
