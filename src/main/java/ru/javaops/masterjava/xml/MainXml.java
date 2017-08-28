package ru.javaops.masterjava.xml;

import ru.javaops.masterjava.xml.schema.*;
import ru.javaops.masterjava.xml.util.JaxbParser;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Main operations with XML.
 *
 * @author Vadim Gamaliev <a href="mailto:gamaliev-vadim@yandex.com">gamaliev-vadim@yandex.com</a>
 */
public class MainXml {


    /*
        Public
     */

    /**
     * Search for users, from source, who participate in specified projects ({@code projectName}),
     * and print their names in ascending order.
     *
     * @param projectName       Project name for searching.
     * @param source            XML-source.
     * @throws JAXBException    If any unexpected errors occur while unmarshalling.
     */
    public static void printUsersInProjectJaxb(final String projectName, final InputStream source) throws JAXBException {
        checkParameters(projectName, source);

        final JaxbParser parser = new JaxbParser(ObjectFactory.class);
        final Payload payload = parser.unmarshal(source);
        findAndPrintUsersInProject(projectName, payload);
    }

    /**
     * Search for users, from source, who participate in specified projects ({@code projectName}),
     * and print their names in ascending order.
     *
     * @param projectName       Project name for searching.
     * @param source            XML-source.
     * @throws JAXBException    If any unexpected errors occur while unmarshalling.
     */
    public static void printUsersInProjectJaxb(final String projectName, final Reader source) throws JAXBException {
        checkParameters(projectName, source);

        final JaxbParser parser = new JaxbParser(ObjectFactory.class);
        final Payload payload = parser.unmarshal(source);
        findAndPrintUsersInProject(projectName, payload);
    }

    /**
     * Search for users, from source, who participate in specified projects ({@code projectName}),
     * and print their names in ascending order.
     *
     * @param projectName       Project name for searching.
     * @param source            XML-source.
     * @throws JAXBException    If any unexpected errors occur while unmarshalling.
     */
    public static void printUsersInProjectJaxb(final String projectName, final String source) throws JAXBException {
        checkParameters(projectName, source);

        final JaxbParser parser = new JaxbParser(ObjectFactory.class);
        final Payload payload = parser.unmarshal(source);
        findAndPrintUsersInProject(projectName, payload);
    }


    /*
        Private
     */

    /**
     * Check parameters for null and empty values.
     * @param projectName   First param.
     * @param source        Second param.
     * @throws IllegalArgumentException Throw an exception if the parameters are invalid.
     */
    private static void checkParameters(final String projectName, final Object source) throws IllegalArgumentException {
        if (projectName == null) throw new IllegalArgumentException("projectName is null.");
        if (projectName.length() == 0) throw new IllegalArgumentException("projectName is empty.");
        if (source == null) throw new IllegalArgumentException("source is null.");
    }

    /**
     * Search for users who participate in specified projects ({@code projectName}), and print their names in ascending order.
     * @param projectName   Project name for searching.
     * @param payload       An object that contains users that contain project names.
     */
    private static void findAndPrintUsersInProject(final String projectName, final Payload payload) {
        final List<User> result = new ArrayList<>();

        start:
        for (User user : payload.getUsers().getUser()) {
            final List<Object> groups = user.getGroups();
            for (Object group : groups) {
                final String pName = ((Project) ((Group) group).getParent()).getName();
                if (projectName.equalsIgnoreCase(pName)) {
                    result.add(user);
                    continue start;
                }
            }
        }

        result.sort(new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getFullName().compareTo(o2.getFullName());
            }
        });
        for (User user : result) System.out.println(user.getFullName());
    }
}
