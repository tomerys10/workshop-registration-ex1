package patmal.workshops.student.impl.logic;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import patmal.workshops.api.model.Student;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Bonus: registers students from a UTF-8 CSV file with the exact header
 * id,name,email. Lines are processed in file order; the import stops at the
 * first rejected line (the students before it stay registered). A file that
 * cannot be read results in an UncheckedIOException.
 */
class CsvImporter {

    private static final Logger log = LogManager.getLogger(CsvImporter.class);
    private static final List<String> HEADER = List.of("id", "name", "email");

    private final StudentManager studentManager;

    CsvImporter(StudentManager studentManager) {
        this.studentManager = studentManager;
    }

    List<Student> importStudents(File source) {
        List<Student> registered = new ArrayList<>();
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .get();
        try (Reader reader = new InputStreamReader(new FileInputStream(source), StandardCharsets.UTF_8);
             CSVParser parser = format.parse(reader)) {
            if (!HEADER.equals(parser.getHeaderNames())) {
                log.warn("Rejected CSV import from '{}': wrong header {}", source, parser.getHeaderNames());
                throw new IllegalArgumentException(
                        "Wrong CSV header: expected id,name,email but found " + parser.getHeaderNames());
            }
            for (CSVRecord record : parser) {
                if (record.size() != HEADER.size()) {
                    log.warn("Rejected CSV import from '{}': wrong number of values at line {}",
                            source, record.getRecordNumber() + 1);
                    throw new IllegalArgumentException(
                            "Wrong number of values at line " + (record.getRecordNumber() + 1));
                }
                registered.add(studentManager.create(
                        record.get("id"), record.get("name"), record.get("email")));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read CSV file " + source, e);
        }
        log.info("Imported {} students from '{}'", registered.size(), source);
        return registered;
    }
}
