package seedu.rc4hdb.logic.parser.commandparsers;

import java.util.List;

import static java.util.Objects.requireNonNull;
import seedu.rc4hdb.logic.commands.modelcommands.HideOnlyCommand;
import seedu.rc4hdb.logic.parser.exceptions.ParseException;

public class HideOnlyCommandParser extends ColumnManipulatorCommandParser {
    @Override
    public HideOnlyCommand parse(String args) throws ParseException {
        requireNonNull(args);
        requireNonEmpty(args);

        List<String> fieldsToHide = getBaseFieldList(args);
        List<String> fieldsToShow = getComplementFieldList(args);

        return new HideOnlyCommand(fieldsToShow, fieldsToHide);
    }

    @Override
    public String getCommandWord() {
        return HideOnlyCommand.COMMAND_WORD;
    }

    @Override
    public String getCommandPresentTense() {
        return HideOnlyCommand.COMMAND_PRESENT_TENSE;
    }
}
