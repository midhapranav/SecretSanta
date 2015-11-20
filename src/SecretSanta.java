import com.sun.mail.smtp.SMTPTransport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.Security;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by midhapranav on 11/17/15.
 * The code uses Gmail Api to send emails
 * You will have to allow less secure apps to sign in to your google account
 *
 * Merry Christmas
 */

public class SecretSanta {

    static class elves {
        String name;
        String email;
    }

    public static List<elves> mElves = new ArrayList<elves>();
    public static String mFilename = "/Users/midhapranav/IdeaProjects/SecretSanta/src/elves.txt";
    public static String mFileContent = "";

    public static void main(String args[]) {

        try {
            FileReader mFileReader = new FileReader(mFilename);

            BufferedReader mBufferedReader = new BufferedReader(mFileReader);

            while((mFileContent = mBufferedReader.readLine()) != null) {
                String[] split = mFileContent.split("-");
                if(!(split[0].equals("Name"))) {
                    elves mNewElf = new elves();
                    mNewElf.name = split[0];
                    mNewElf.email = split[1];
                    mElves.add(mNewElf);
                }
            }
            HashMap<elves, elves> mSecretSantaMapping = secretSantaMapping();
            sendEmails(mSecretSantaMapping);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<elves, elves> secretSantaMapping() {
        HashMap<elves, elves> mSecretSantaMapping = new HashMap<elves, elves>();
        //HashMap<personWhoGetsTheGift, SecretSanta>
        int numberOfElves = mElves.size();

        for(int i=0; i<numberOfElves;) {
            Random integerGenerator = new Random();
            int randomElfIndex = integerGenerator.nextInt(numberOfElves);
            if (randomElfIndex == i) {
                continue;
            } else if (mSecretSantaMapping.containsKey(mElves.get(randomElfIndex))) {
                continue;
            } else {
                mSecretSantaMapping.put(mElves.get(randomElfIndex),mElves.get(i));
                i++;
            }
        }
        return mSecretSantaMapping;
    }

    public static void sendEmails(HashMap<elves, elves> secretSantaMapping) {

        for(HashMap.Entry<elves, elves> mapping : secretSantaMapping.entrySet()){
            elves secretSanta = mapping.getValue();
            elves elf = mapping.getKey();
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
            // Get a Properties object
            Properties props = System.getProperties();
            props.setProperty("mail.smtps.host", "smtp.gmail.com");
            props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.port", "465");
            props.setProperty("mail.smtp.socketFactory.port", "465");
            props.setProperty("mail.smtps.auth", "true");

        /*
        If set to false, the QUIT command is sent and the connection is immediately closed. If set
        to true (the default), causes the transport to wait for the response to the QUIT command.

        ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
                http://forum.java.sun.com/thread.jspa?threadID=5205249
                smtpsend.java - demo program from javamail
        */
            props.put("mail.smtps.quitwait", "false");

            Session session = Session.getInstance(props, null);

            // -- Create a new message --
            final MimeMessage msg = new MimeMessage(session);

            // -- Set the FROM and TO fields --
            try {
                msg.setFrom(new InternetAddress("yourGmailUsername@gmail.com"));
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(secretSanta.email, false));


                msg.setSubject("Congratulations! You have been socked!!");
                msg.setText("Hey "+secretSanta.name+",\n\n\n You have a chance to make this christmas merry for "+ elf.name + "\n\n Regards, \n\n Big Fat Daddy \n PS: Midha is super aweseome", "utf-8");
                msg.setSentDate(new Date());

                SMTPTransport t = (SMTPTransport) session.getTransport("smtps");

                t.connect("smtp.gmail.com", "yourGmailUsername@gmail.com", "password");
                t.sendMessage(msg, msg.getAllRecipients());
                t.close();
                System.out.println("Email sent successfully");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
