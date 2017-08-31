package ru.javaops.masterjava.xml;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ru.javaops.masterjava.dto.UserNameEmail;
import ru.javaops.masterjava.exceptions.BreakException;
import ru.javaops.masterjava.exceptions.ExceptionHandler;
import ru.javaops.masterjava.xml.schema.*;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.*;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Main operations with XML.
 *
 * @author Vadim Gamaliev <a href="mailto:gamaliev-vadim@yandex.com">gamaliev-vadim@yandex.com</a>
 */
public class MainXml {

    /*
        Constants
     */

    public static final String TAG_USER = "User";
    public static final String TAG_USERS = "Users";
    public static final String TAG_PROJECT = "Project";
    public static final String TAG_GROUP = "Group";
    public static final String TAG_NAME = "Name";
    public static final String TAG_FULLNAME = "fullName";

    public static final String ATTRIBUTE_ID = "id";
    public static final String ATTRIBUTE_PARENT_PROJECT = "parent";
    public static final String ATTRIBUTE_GROUPS = "groups";
    public static final String ATTRIBUTE_EMAIL = "email";

    public static final String PARAM_PROJECT = "param_project";

    public static final String STRING_EMPTY = "";


    /*
        JAXB
     */

    /**
     * Search for users, from source, who participate in specified project ({@code projectName}),
     * and print their names in ascending order.<br/>
     * If an error occurs during execution, nothing happens
     *
     * @param projectName Project name for searching. Can't be null or empty.
     * @param source      XML-source. Can't be null.
     * @throws JAXBException            If any unexpected errors occur while unmarshalling.
     * @throws IllegalArgumentException Throw an exception if the parameters are invalid.
     */
    public static void printUsersInProjectJaxb(final String projectName, final InputStream source) throws JAXBException {
        checkParameters(projectName, source);

        final JaxbParser parser = new JaxbParser(ObjectFactory.class);
        final Payload payload = parser.unmarshal(source);
        findAndPrintUsersInProject(projectName, payload);
    }

    /**
     * Search for users, from source, who participate in specified project ({@code projectName}),
     * and print their names in ascending order.<br/>
     * If an error occurs during execution, nothing happens
     *
     * @param projectName Project name for searching. Can't be null or empty.
     * @param source      XML-source. Can't be null.
     * @throws JAXBException            If any unexpected errors occur while unmarshalling.
     * @throws IllegalArgumentException Throw an exception if the parameters are invalid.
     */
    public static void printUsersInProjectJaxb(final String projectName, final Reader source) throws JAXBException {
        checkParameters(projectName, source);

        final JaxbParser parser = new JaxbParser(ObjectFactory.class);
        final Payload payload = parser.unmarshal(source);
        findAndPrintUsersInProject(projectName, payload);
    }

    /**
     * Search for users, from source, who participate in specified project ({@code projectName}),
     * and print their names in ascending order.<br/>
     * If an error occurs during execution, nothing happens
     *
     * @param projectName Project name for searching. Can't be null or empty.
     * @param source      XML-source. Can't be null.
     * @throws JAXBException            If any unexpected errors occur while unmarshalling.
     * @throws IllegalArgumentException Throw an exception if the parameters are invalid.
     */
    public static void printUsersInProjectJaxb(final String projectName, final String source) throws JAXBException {
        checkParameters(projectName, source);

        final JaxbParser parser = new JaxbParser(ObjectFactory.class);
        final Payload payload = parser.unmarshal(source);
        findAndPrintUsersInProject(projectName, payload);
    }


    /*
        DOM Parser
     */

    /**
     * Search for users, from source, who participate in specified project ({@code projectName}),
     * and return list of {@link UserNameEmail}. Using DOM-parser.<br/>
     * If an error occurs during execution, it will return empty immutable list.
     *
     * @param projectName Project name for searching. Can't be null or empty.
     * @param xml         XML-source. Can't be null.
     * @param xsd         XSD-source for validate {@code xml}. Can't be null.
     * @return List of {@link UserNameEmail}.
     * @throws IllegalArgumentException Throw an exception if the parameters are invalid.
     */
    public static List<UserNameEmail> getUsersInProjectDom(final String projectName, final File xml, final File xsd) {
        checkParameters(projectName, xml, xsd);

        final List<UserNameEmail> users = new ArrayList<>();
        String projectIdExpected = null;
        final Set<String> groupsIdExpected = new HashSet<>();
        int length = 0;
        int length2 = 0;

        try {
            final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            final Schema schema = schemaFactory.newSchema(xsd);
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setSchema(schema);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(xml);

            // Find expected project id by project name
            final NodeList projectsNodeList = document.getElementsByTagName(TAG_PROJECT);
            length = projectsNodeList.getLength();
            start:
            for (int i = 0; i < length; i++) {
                final NodeList childNodes = projectsNodeList.item(i).getChildNodes();
                length2 = childNodes.getLength();
                for (int j = 0; j < length2; j++) {
                    final Node node = childNodes.item(j);
                    if (TAG_NAME.equalsIgnoreCase(node.getNodeName())) {
                        if (projectName.equalsIgnoreCase(node.getTextContent())) {
                            projectIdExpected =
                                    projectsNodeList.item(i).getAttributes().getNamedItem(ATTRIBUTE_ID).getNodeValue();
                        }
                        break start;
                    }
                }
            }

            if (projectIdExpected == null) return Collections.emptyList();

            // Find expected groups, that an user must contain
            final NodeList groupsNodeList = document.getElementsByTagName(TAG_GROUP);
            length = groupsNodeList.getLength();
            for (int i = 0; i < length; i++) {
                final NamedNodeMap attributes = groupsNodeList.item(i).getAttributes();
                if (projectIdExpected.equalsIgnoreCase(
                        attributes.getNamedItem(ATTRIBUTE_PARENT_PROJECT).getNodeValue())) {
                    groupsIdExpected.add(attributes.getNamedItem(ATTRIBUTE_ID).getNodeValue());
                }
            }

            if (groupsIdExpected.size() == 0) return Collections.emptyList();

            // Find users
            final NodeList usersNodeList = document.getElementsByTagName(TAG_USER);
            length = usersNodeList.getLength();
            for (int i = 0; i < length; i++) {
                final NamedNodeMap attributes = usersNodeList.item(i).getAttributes();
                final String[] groups = attributes.getNamedItem(ATTRIBUTE_GROUPS).getNodeValue().split(" ");
                for (String group : groups) {
                    if (groupsIdExpected.contains(group)) {
                        final NodeList childNodes = usersNodeList.item(i).getChildNodes();
                        length2 = childNodes.getLength();
                        String name = null;
                        for (int j = 0; j < length2; j++) {
                            if (TAG_FULLNAME.equalsIgnoreCase(childNodes.item(j).getNodeName())) {
                                name = childNodes.item(j).getTextContent();
                                break;
                            }
                        }
                        final String email = attributes.getNamedItem(ATTRIBUTE_EMAIL).getNodeValue();
                        users.add(new UserNameEmail(
                                name == null ? STRING_EMPTY : name,
                                email == null ? STRING_EMPTY : email));
                        break;
                    }
                }
            }

        } catch (ParserConfigurationException e) {
            ExceptionHandler.handleException(e);
            e.printStackTrace();
            return Collections.emptyList();

        } catch (SAXException e) {
            ExceptionHandler.handleException(e);
            e.printStackTrace();
            return Collections.emptyList();

        } catch (IOException e) {
            ExceptionHandler.handleException(e);
            e.printStackTrace();
            return Collections.emptyList();
        }

        return users;
    }


    /*
        SAX parser
     */

    /**
     * Search for users, from source, who participate in specified project ({@code projectName}),
     * and return list of {@link UserNameEmail}. Using SAX-parser.<br/>
     * If an error occurs during execution, it will return empty immutable list.
     *
     * @param projectName Project name for searching. Can't be null or empty.
     * @param xml         XML-source. Can't be null.
     * @param xsd         XSD-source for validate {@code xml}. Can't be null.
     * @return List of {@link UserNameEmail}.
     * @throws IllegalArgumentException Throw an exception if the parameters are invalid.
     */
    public static List<UserNameEmail> getUsersInProjectSax(final String projectName, final File xml, final File xsd) {
        checkParameters(projectName, xml, xsd);

        final List<UserNameEmail> users = new ArrayList<>();

        try {
            final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            final Schema schema = schemaFactory.newSchema(xsd);
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setSchema(schema);
            factory.setValidating(true);
            final SAXParser parser = factory.newSAXParser();

            parser.parse(xml, new DefaultHandler() {
                UserNameEmail user = null;
                boolean needNameTag = false;
                boolean needNameContent = false;

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if (needNameTag && TAG_FULLNAME.equalsIgnoreCase(qName)) {
                        needNameContent = true;
                        needNameTag = false;
                    } else if (!needNameContent && TAG_USER.equalsIgnoreCase(qName)) {
                        if (attributes.getValue(ATTRIBUTE_GROUPS).toLowerCase()
                                .contains(projectName.toLowerCase())) {
                            user = new UserNameEmail();
                            user.setEmail(attributes.getValue(ATTRIBUTE_EMAIL));
                            needNameTag = true;
                        }
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    if (TAG_USERS.equalsIgnoreCase(qName)) {
                        throw new BreakException("Break parsing. Goal completed.");
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    if (needNameContent) {
                        user.setName(new String(ch, start, length));
                        users.add(user);
                        user = null;
                        needNameContent = false;
                    }
                }
            });

        } catch (BreakException ignored) {
        } catch (SAXException e) {
            ExceptionHandler.handleException(e);
            e.printStackTrace();
            return Collections.emptyList();

        } catch (ParserConfigurationException e) {
            ExceptionHandler.handleException(e);
            e.printStackTrace();
            return Collections.emptyList();

        } catch (IOException e) {
            ExceptionHandler.handleException(e);
            e.printStackTrace();
            return Collections.emptyList();
        }

        return users;
    }


    /*
        StAX-parser
     */

    /**
     * Search for users, from source, who participate in specified project ({@code projectName}),
     * and return list of {@link UserNameEmail}. Using StAX-parser.<br/>
     * If an error occurs during execution, it will return empty immutable list.
     *
     * @param projectName Project name for searching. Can't be null or empty.
     * @param source      XML-source. Can't be null.
     * @return List of {@link UserNameEmail}.
     * @throws IllegalArgumentException Throw an exception if the parameters are invalid.
     */
    public static List<UserNameEmail> getUsersInProjectStaxStream(final String projectName, final InputStream source) {
        checkParameters(projectName, source);

        final List<UserNameEmail> users = new ArrayList<>();

        try (StaxStreamProcessor p = new StaxStreamProcessor(source)) {
            if (p.doUntil(XMLStreamConstants.START_ELEMENT, TAG_USERS)) {
                while (p.doUntilEndTag(XMLStreamConstants.START_ELEMENT, TAG_USER, MainXml.TAG_USERS)) {
                    final String groups = p.getReader().getAttributeValue(STRING_EMPTY, ATTRIBUTE_GROUPS);
                    if (groups != null && groups.toLowerCase().contains(projectName.toLowerCase())) {
                        final String email = p.getReader().getAttributeValue(STRING_EMPTY, ATTRIBUTE_EMAIL);
                        final String name = p.getElementValue(TAG_FULLNAME);
                        users.add(new UserNameEmail(name, email));
                    }
                }
            }
        } catch (XMLStreamException e) {
            ExceptionHandler.handleException(e);
            e.printStackTrace();
            return Collections.emptyList();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
            e.printStackTrace();
            return Collections.emptyList();
        }

        return users;
    }


    /**
     * Does not work.
     * TODO: implement.
     *
     * @param projectName Project name for searching. Can't be null or empty.
     * @param source      XML-source. Can't be null.
     * @return List of {@link UserNameEmail}.
     * @throws IllegalArgumentException Throw an exception if the parameters are invalid.
     */
    public static List<UserNameEmail> getUsersInProjectStaxEvent(final String projectName, final InputStream source) {
        checkParameters(projectName, source);
        final List<UserNameEmail> users = new ArrayList<>();
        return users;
    }


    /*
        Transform by XSLT
     */

    /**
     * Transform an XML-source using an XSLT-transform.
     *
     * @param projectName   Project name. Using as a parameter in the XSL-stylesheet.
     * @param sourceXml     The XML-source for the transformation.
     * @param stylesheetXsl XSL-stylesheet.
     * @return The result of the transformation. Or null, if an exception occurs.
     * @throws IllegalArgumentException Throw an exception if the parameters are invalid.
     */
    public static String transformByXslt(final String projectName, final File sourceXml, final File stylesheetXsl) {
        checkParameters(projectName, sourceXml, stylesheetXsl);

        try {
            final StringWriter result = new StringWriter();
            final TransformerFactory factory = TransformerFactory.newInstance();
            final Transformer transformer = factory.newTransformer(new StreamSource(stylesheetXsl));

            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setParameter(PARAM_PROJECT, projectName);

            transformer.transform(new StreamSource(sourceXml), new StreamResult(result));
            return result.toString();
        } catch (TransformerConfigurationException e) {
            ExceptionHandler.handleException(e);
            e.printStackTrace();
        } catch (TransformerException e) {
            ExceptionHandler.handleException(e);
            e.printStackTrace();
        }

        return null;
    }


    /*
        Utils
     */

    /**
     * Check parameters for null and empty values.
     *
     * @param projectName First param.
     * @param source      Second param.
     * @throws IllegalArgumentException Throw an exception if the parameters are invalid.
     */
    private static void checkParameters(final String projectName, final Object... source) throws IllegalArgumentException {
        if (projectName == null) throw new IllegalArgumentException("projectName is null.");
        if (projectName.length() == 0) throw new IllegalArgumentException("projectName is empty.");
        if (source == null) throw new IllegalArgumentException("source is null.");
        for (Object o : source) {
            if (o == null) throw new IllegalArgumentException("source is null.");
        }
    }

    /**
     * Search for users who participate in specified project ({@code projectName}), and print their names in ascending order.
     *
     * @param projectName Project name for searching.
     * @param payload     An object that contains users that contain project names.
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
