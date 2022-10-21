package seedu.rc4hdb.logic.parser.commandparsers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import seedu.rc4hdb.logic.commands.modelcommands.ShowCommand;
import seedu.rc4hdb.logic.parser.Parser;
import seedu.rc4hdb.logic.parser.exceptions.ParseException;
import seedu.rc4hdb.model.resident.fields.ResidentFields;

public class ShowCommandParser implements Parser<ShowCommand> {

    public static final String INTENDED_USAGE = "Please (only) enter some fields after the show command\n"
            + "Example: show name phone email";

    @Override
    public ShowCommand parse(String args) throws ParseException {
        requireNonNull(args);

        if (args.isEmpty()) {
            throw new ParseException(INTENDED_USAGE);
        }

        // Process global list of fields into lowercase list first
        List<String> allFields = ResidentFields.FIELDS.stream().map(String::toLowerCase).collect(Collectors.toList());

        // Create result list
        List<String> fieldsToExclude = new ArrayList<>(allFields);

        String[] specifiedFields = getSpecifiedFields(args);

        populateFieldLists(specifiedFields, fieldsToExclude);

        return new ShowCommand(fieldsToExclude);
    }

    private String[] getSpecifiedFields(String args) {
        return args.trim().toLowerCase().split(" ");
    }

    private void populateFieldLists(String[] specifiedFields, List<String> fieldsToExclude) {
        for (String field : specifiedFields) {
            fieldsToExclude.remove(field);
        }
    }
}
