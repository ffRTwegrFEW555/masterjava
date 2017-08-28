package ru.javaops.masterjava.xml;

import com.google.common.io.Resources;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
        Tests. Exceptions
     */

    @Test
    public void checkExceptions() throws Exception {
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

        assertEquals(7, exceptionCounter);
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