package fr.ribesg.alix.api.bot.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PasteUtil {

   /**
    * Use own Logger as {@link fr.ribesg.alix.api.Log} calls may call this.
    */
   private static final Logger LOG = Logger.getRootLogger();

   public enum Mode {
      GIST,;
   }

   private static Mode mode = Mode.GIST;

   public static Mode getMode() {
      return PasteUtil.mode;
   }

   public static void setMode(final Mode mode) {
      PasteUtil.mode = mode;
   }

   public static String paste(final String toPaste) {
      switch (PasteUtil.mode) {
         case GIST:
            return PasteUtil.gist(toPaste);
         default:
            throw new UnsupportedOperationException("Not yet implemented: " + PasteUtil.mode);
      }
   }

   // ############### //
   // ## Gist Mode ## //
   // ############### //

   private static final String GITHUB_API_URL = "https://api.github.com/";
   private static final String GIST_API_LOCATION = "gists";
   private static final String GIST_URL = GITHUB_API_URL + GIST_API_LOCATION;
   private static final String DESCRIPTION = "Alix pasted this on ";

   /**
    * Paste a String to Gist then returns the link to the gist
    *
    * @param toGist the String to paste
    *
    * @return the link to the paste
    */
   public static String gist(final String toGist) {
      LOG.trace("Started to Gist something...");
      try {
         LOG.trace("Creating JSON object to send...");

         final JsonObject data = new JsonObject();
         data.addProperty("description", DESCRIPTION + date());
         data.addProperty("public", true);
         final JsonObject fileList = new JsonObject();
         final JsonObject file = new JsonObject();
         file.addProperty("content", toGist);
         fileList.add("AlixPaste-" + date().replace(' ', '-') + ".txt", file);
         data.add("files", fileList);
         final String jsonString = data.toString();

         LOG.trace("Json object created: " + jsonString);

         final String resultString = WebUtil.post(GIST_URL, "application/json; charset=utf-8", jsonString);

         LOG.trace("Response received: " + resultString);

         final JsonObject resultJson = new JsonParser().parse(resultString).getAsJsonObject();
         final String link = resultJson.get("html_url").getAsString();

         LOG.trace("Gist successful! Link: " + link);
         return link;
      } catch (IOException e) {
         LOG.error("Failed to Gist", e);
         LOG.error("This is what I was trying to Gist:\n##########\n" + toGist + "\n##########\n");
         LOG.error("Failed to Gist, error above.");
         return "Gist Error. Limit exceeded?";
      }
   }

   private static String date() {
      return new SimpleDateFormat("EEEE dd MMMM YYYY").format(new Date());
   }
}
