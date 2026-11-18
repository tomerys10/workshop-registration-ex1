package patmal.workshops.student.impl.logic;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * E-mail validation used by every creation of a person. A null or invalid
 * address is rejected with an IllegalArgumentException before any state
 * changes.
 */
final class Emails {

    private Emails() {
    }

    static void requireValid(String email) {
        if (email == null || !EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException("Invalid e-mail address: " + email);
        }
    }
}
