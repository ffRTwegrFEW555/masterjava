package ru.javaops.masterjava.dto;

/**
 * @author Vadim Gamaliev <a href="mailto:gamaliev-vadim@yandex.com">gamaliev-vadim@yandex.com</a>
 */
public class UserNameEmail {

    /*
        Fields
     */

    public String name;
    public String email;


    /*
        Init
     */

    public UserNameEmail() {}

    public UserNameEmail(final String name, final String email) {
        this.name = name;
        this.email = email;
    }


    /*
        Getters
     */

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }


    /*
        Setters
     */

    public void setName(final String name) {
        this.name = name;
    }

    public void setEmail(final String email) {
        this.email = email;
    }
}
