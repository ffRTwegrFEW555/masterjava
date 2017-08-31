package ru.javaops.masterjava.xml;

import com.google.common.io.Resources;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.javaops.masterjava.dto.UserNameEmail;
import ru.javaops.masterjava.html.HtmlUtils;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * @author Vadim Gamaliev <a href="mailto:gamaliev-vadim@yandex.com">gamaliev-vadim@yandex.com</a>
 */
public class MainXmlTest {

    /* ... */
    private PrintStream outBackup = null;
    private ByteArrayOutputStream baos = null;
    private String expected = "AdminFull Name";


    /*
        Init
     */

    @Before
    public void before() {
        outBackup = System.out;
        System.setOut(new PrintStream(baos = new ByteArrayOutputStream()));
    }

    @After
    public void after() throws IOException {
        System.setOut(outBackup);
        outBackup = null;
        baos.close();
        baos = null;
    }


    /*
        Tests
     */

    /*
        JAXB
     */

    @Test
    public void printUsersInProjectJaxbInputStream() throws Exception {
        final InputStream source = Resources.getResource("payload.xml").openStream();
        MainXml.printUsersInProjectJaxb("topjava", source);
        source.close();
        final String actual = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        // #
        assertEquals(expected, actual.replace("\n", "").replace("\r", ""));
    }

    @Test
    public void printUsersInProjectJaxbReader() throws Exception {
        final Reader reader =
                new BufferedReader(
                        new InputStreamReader(
                                Resources.getResource("payload.xml").openStream()));
        MainXml.printUsersInProjectJaxb("topjava", reader);
        reader.close();
        final String actual = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        // #
        assertEquals(expected, actual.replace("\n", "").replace("\r", ""));
    }

    @Test
    public void printUsersInProjectJaxbString() throws Exception {
        final InputStream is = Resources.getResource("payload.xml").openStream();
        MainXml.printUsersInProjectJaxb("topjava", convertStreamToString(is));
        is.close();
        final String actual = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        // #
        assertEquals(expected, actual.replace("\n", "").replace("\r", ""));
    }


    /*
        JAXB. Exceptions
     */

    @Test
    public void checkExceptionsJaxb() throws Exception {
        int exceptionCounter = 0;
        final InputStream source = Resources.getResource("payload.xml").openStream();

        try {
            MainXml.printUsersInProjectJaxb(null, source);
        } catch (JAXBException ignored) {
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        try {
            MainXml.printUsersInProjectJaxb("", source);
        } catch (JAXBException ignored) {
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        final Reader reader = new BufferedReader(new InputStreamReader(source));

        try {
            MainXml.printUsersInProjectJaxb(null, reader);
        } catch (JAXBException ignored) {
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        try {
            MainXml.printUsersInProjectJaxb("", reader);
        } catch (JAXBException ignored) {
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        try {
            MainXml.printUsersInProjectJaxb(null, "");
        } catch (JAXBException ignored) {
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        try {
            MainXml.printUsersInProjectJaxb("", "");
        } catch (JAXBException ignored) {
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }


        try (InputStream is = Resources.getResource("payloadWrong.xml").openStream()){
            MainXml.printUsersInProjectJaxb("topjava", is);
        } catch (JAXBException e) {
            exceptionCounter++;
        } catch (IOException ignored) {
        }

        reader.close();

        // #
        assertEquals(7, exceptionCounter);
    }


    /*
        DOM parser
     */

    @Test
    public void getUsersInProjectDom() throws Exception {
        // final File xml = Paths.get("src/main/resources/payload.xml").toFile();
        // final File xsd = Paths.get("src/main/resources/payload.xsd").toFile();
        final File xml = new File(ClassLoader.getSystemResource("payload.xml").getFile());
        final File xsd = new File(ClassLoader.getSystemResource("payload.xsd").getFile());

        final List<UserNameEmail> users = MainXml.getUsersInProjectDom("topjava", xml, xsd);

        // #
        assertEquals(2, users.size());

        // #
        UserNameEmail user = users.get(0);
        assertEquals("Full Name gmail@gmail.com" , user.name + " " + user.email);
        user = users.get(1);
        assertEquals("Admin admin@javaops.ru" , user.name + " " + user.email);
    }


    /*
        DOM parser. Exceptions
     */

    @Test
    public void getUsersInProjectDomExceptionInvalidXml() throws Exception {
        final File xml = new File(ClassLoader.getSystemResource("payloadWrong.xml").getFile());
        final File xsd = new File(ClassLoader.getSystemResource("payload.xsd").getFile());
        final List<UserNameEmail> users = MainXml.getUsersInProjectDom("topjava", xml, xsd);

        // #
        assertEquals(0, users.size());
    }

    @Test
    public void getUsersInProjectDomExceptionInvalidParams() throws Exception {
        int exceptionCounter = 0;

        final File xml = new File(ClassLoader.getSystemResource("payload.xml").getFile());
        final File xsd = new File(ClassLoader.getSystemResource("payload.xsd").getFile());

        try {
            MainXml.getUsersInProjectDom(null, xml, xsd);
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        try {
            MainXml.getUsersInProjectDom("", xml, xsd);
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        try {
            MainXml.getUsersInProjectDom("topjava", null, xsd);
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        try {
            MainXml.getUsersInProjectDom("topjava", xml, null);
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        // #
        assertEquals(4, exceptionCounter);
    }


    /*
        SAX parser
     */

    @Test
    public void getUsersInProjectSax() throws Exception {
        // final File xml = Paths.get("src/main/resources/payload.xml").toFile();
        // final File xsd = Paths.get("src/main/resources/payload.xsd").toFile();
        final File xml = new File(ClassLoader.getSystemResource("payload.xml").getFile());
        final File xsd = new File(ClassLoader.getSystemResource("payload.xsd").getFile());

        final List<UserNameEmail> users = MainXml.getUsersInProjectSax("topjava", xml, xsd);

        // #
        assertEquals(2, users.size());

        // #
        UserNameEmail user = users.get(0);
        assertEquals("Full Name gmail@gmail.com" , user.name + " " + user.email);
        user = users.get(1);
        assertEquals("Admin admin@javaops.ru" , user.name + " " + user.email);
    }


    /*
        SAX parser. Exceptions
     */

    @Test
    public void getUsersInProjectSaxExceptionInvalidXml() throws Exception {
        final File xml = new File(ClassLoader.getSystemResource("payloadWrong.xml").getFile());
        final File xsd = new File(ClassLoader.getSystemResource("payload.xsd").getFile());
        final List<UserNameEmail> users = MainXml.getUsersInProjectSax("topjava", xml, xsd);

        // #
        assertEquals(0, users.size());
    }

    @Test
    public void getUsersInProjectSaxExceptionInvalidParams() throws Exception {
        int exceptionCounter = 0;

        final File xml = new File(ClassLoader.getSystemResource("payload.xml").getFile());
        final File xsd = new File(ClassLoader.getSystemResource("payload.xsd").getFile());

        try {
            MainXml.getUsersInProjectSax(null, xml, xsd);
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        try {
            MainXml.getUsersInProjectSax("", xml, xsd);
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        try {
            MainXml.getUsersInProjectSax("topjava", null, xsd);
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        try {
            MainXml.getUsersInProjectSax("topjava", xml, null);
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        // #
        assertEquals(4, exceptionCounter);
    }


    /*
        StAX parser
     */

    @Test
    public void getUsersInProjectStaxStream() throws Exception {
        final InputStream source = Resources.getResource("payload.xml").openStream();
        final List<UserNameEmail> users = MainXml.getUsersInProjectStaxStream("topjava", source);
        source.close();

        System.setOut(outBackup);
        System.out.println(HtmlUtils.convertUserNameEmailToHtmlTable(users, 4, 0));

        // #
        assertEquals(2, users.size());

        // #
        UserNameEmail user = users.get(0);
        assertEquals("Full Name gmail@gmail.com" , user.name + " " + user.email);
        user = users.get(1);
        assertEquals("Admin admin@javaops.ru" , user.name + " " + user.email);
    }


    /*
        StAX parser. Exceptions
     */

    @Test
    public void getUsersInProjectStaxStreamInvalidXml() throws Exception {
        final InputStream source = Resources.getResource("payloadWrong.xml").openStream();
        final List<UserNameEmail> users = MainXml.getUsersInProjectStaxStream("topjava", source);
        source.close();

        // #
        assertEquals(0, users.size());
    }

    @Test
    public void getUsersInProjectStaxStreamInvalidParams() throws Exception {
        int exceptionCounter = 0;

        final InputStream source = Resources.getResource("payload.xml").openStream();

        try {
            MainXml.getUsersInProjectStaxStream(null, source);
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        try {
            MainXml.getUsersInProjectStaxStream("", source);
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        try {
            MainXml.getUsersInProjectStaxStream("topjava", null);
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        source.close();

        // #
        assertEquals(3, exceptionCounter);
    }


    /*
        Transformation by XSLT
     */

    @Test
    public void transformByXslt() throws Exception {
        final File xml = new File(ClassLoader.getSystemResource("payload.xml").getFile());
        final File xsl = new File(ClassLoader.getSystemResource("payloadToHtmlWithProjectAndGroups.xsl").getFile());

        String actual = MainXml.transformByXslt("tOpjAva", xml, xsl);
        System.setOut(outBackup);
        System.out.println(actual);

        final String topJavaExpect =
                "<h1>List of groups of \"tOpjAva\" project.</h1>" +
                        "<ul>" +
                        "  <li>TopJava07</li>" +
                        "  <li>TopJava08</li>" +
                        "</ul>";

        // #
        assertEquals(topJavaExpect, actual.replace("\n", "").replace("\r", ""));

        actual = MainXml.transformByXslt("masterjaVA", xml, xsl);
        System.out.println(actual);

        final String masterJavaExpect =
                "<h1>List of groups of \"masterjaVA\" project.</h1>" +
                "<ul>" +
                "  <li>MasterJava01</li>" +
                "</ul>";

        // #
        assertEquals(masterJavaExpect, actual.replace("\n", "").replace("\r", ""));
    }


    /*
        Transformation by XSLT. Exceptions
     */

    @Test
    public void transformByXsltInvalidXml() throws Exception {
        final File xml = new File(ClassLoader.getSystemResource("payloadWrong.xml").getFile());
        final File xsl = new File(ClassLoader.getSystemResource("payloadToHtmlWithProjectAndGroups.xsl").getFile());
        MainXml.transformByXslt("topjava", xml, xsl);
    }

    @Test
    public void transformByXsltInvalidParams() throws Exception {
        int exceptionCounter = 0;

        final File xml = new File(ClassLoader.getSystemResource("payloadWrong.xml").getFile());
        final File xsl = new File(ClassLoader.getSystemResource("payloadToHtmlWithProjectAndGroups.xsl").getFile());

        try {
            MainXml.transformByXslt(null, xml, xsl);
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        try {
            MainXml.transformByXslt("", xml, xsl);
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        try {
            MainXml.transformByXslt("topjava", null, xsl);
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        try {
            MainXml.transformByXslt("topjava", xml, null);
        } catch (IllegalArgumentException e) {
            exceptionCounter++;
        }

        // #
        assertEquals(4, exceptionCounter);
    }


    /*
        Utils
     */

    private static String convertStreamToString(final InputStream is) {
        if (is == null) return "";

        final Scanner sc = new Scanner(is);
        sc.useDelimiter("\\A");

        final String streamString = sc.hasNext() ? sc.next() : "";

        sc.close();

        return streamString;
    }
}