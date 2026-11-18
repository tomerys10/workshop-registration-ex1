package patmal.workshops.student.impl.logic;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * E-mail validation used by every creation of a person. A null or invalid
 * address is rejected with an IllegalArgumentException before any state
 * changes.
 */
final class Emails {

    private Emails() {
    }

    private static final Logger log = LogManager.getLogger(Emails.class);

    static void requireValid(String email) {
        if (email == null || !EmailValidator.getInstance().isValid(email)) {
            log.warn("Rejected creation: invalid e-mail address '{}'", email);
            throw new IllegalArgumentException("Invalid e-mail address: " + email);
        }
    }
}
