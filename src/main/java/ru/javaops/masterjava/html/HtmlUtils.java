package ru.javaops.masterjava.html;

import ru.javaops.masterjava.dto.UserNameEmail;

import java.util.List;

/**
 * @author Vadim Gamaliev <a href="mailto:gamaliev-vadim@yandex.com">gamaliev-vadim@yandex.com</a>
 */
public class HtmlUtils {

    /**
     * Example:
     * <pre>
     * &lt;table border="1">
     *   &lt;caption>User name / email table&lt;/caption>
     *   &lt;tr>&lt;th>Name&lt;/th>&lt;th>Email&lt;/th>&lt;/tr>
     *   &lt;tr>&lt;td>Full Name&lt;/td>&lt;td>gmail@gmail.com&lt;/td>&lt;/tr>
     *   &lt;tr>&lt;td>Admin&lt;/td>&lt;td>admin@javaops.ru&lt;/td>&lt;/tr>
     * &lt;/table>
     * </pre>
     *
     * @param users              List of users.
     * @param indentFromPrevious Common indent. If < 0 then indent = 0.
     * @param indent             internal indentation. If < 0 then indent = 0. If = 0 then indent = 4 (default).
     * @return String containing html-code.
     */
    public static String convertUserNameEmailToHtmlTable(
            final List<UserNameEmail> users, int indentFromPrevious, int indent) {

        String indentS = null;
        final StringBuilder indentSb = new StringBuilder();
        if (indent < 0) indent = 0;
        else if (indent == 0) indent = 4;
        for (int i = 0; i < indent; i++) indentSb.append(" ");
        indentS = indentSb.toString();

        String indentSPrev = null;
        final StringBuilder indentSbPrev = new StringBuilder();
        if (indentFromPrevious < 0) indentFromPrevious = 0;
        for (int i = 0; i < indentFromPrevious; i++) indentSbPrev.append(" ");
        indentSPrev = indentSbPrev.toString();

        final StringBuilder result = new StringBuilder();
        result  .append(indentSPrev).append("<table border=\"1\">\n")
                .append(indentSPrev).append(indentS).append("<caption>User name / email table</caption>\n")
                .append(indentSPrev).append(indentS).append("<tr><th>Name</th><th>Email</th></tr>\n");

        for (UserNameEmail user : users) {
            result  .append(indentSPrev).append(indentS).append("<tr><td>")
                    .append(user.getName())
                    .append("</td><td>")
                    .append(user.getEmail())
                    .append("</td></tr>\n");
        }

        result.append(indentSPrev).append("</table>");
        return result.toString();
    }
}
