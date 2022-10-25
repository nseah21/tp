package seedu.rc4hdb.logic.commands.modelcommands;

import static java.util.Objects.requireNonNull;
import static seedu.rc4hdb.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.List;

import seedu.rc4hdb.logic.commands.exceptions.CommandException;
import seedu.rc4hdb.model.Model;
import seedu.rc4hdb.model.resident.fields.ResidentField;

public abstract class ColumnManipulatorCommand implements ModelCommand {

    public static final String RESTORE_FULL_VIEW = "Use the reset command to restore the full table view.";

    public static final String MESSAGE_SUCCESS_FORMAT = "Successfully %s the specified columns. ";

    public static final String MESSAGE_SUCCESS_FORMAT_RESTORE_FULL_VIEW = MESSAGE_SUCCESS_FORMAT + RESTORE_FULL_VIEW;

    public static final String AT_LEAST_ONE_VISIBLE_COLUMN = "You must have at least one column visible at all times!";

    public static final String INVALID_SUBSET = "Please enter columns to show or hide "
            + "that are currently in the table view.\n"
            + "To display columns outside of the current view, use the reset command.\n";

    public static final List<String> ALL_FIELDS = ResidentField.LOWERCASE_FIELDS;
    protected final List<String> fieldsToShow;
    protected final List<String> fieldsToHide;

    public ColumnManipulatorCommand(List<String> fieldsToShow, List<String> fieldsToHide) {
        requireAllNonNull(fieldsToShow, fieldsToHide);
        this.fieldsToShow = fieldsToShow;
        this.fieldsToHide = fieldsToHide;
    }

    public static List<String> generateComplementListFrom(List<String> inputList) {
        requireNonNull(inputList);
        List<String> complementList = new ArrayList<>(ALL_FIELDS);
        complementList.removeAll(inputList);
        return complementList;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof ColumnManipulatorCommand) {
            ColumnManipulatorCommand otherCommand = (ColumnManipulatorCommand) other;

            // Bi-directional subset implies equality
            boolean hasEqualFieldsToShow = this.fieldsToShow.containsAll(otherCommand.fieldsToShow)
                    && otherCommand.fieldsToShow.containsAll(this.fieldsToShow);
            boolean hasEqualFieldsToHide = this.fieldsToHide.containsAll(otherCommand.fieldsToHide)
                    && otherCommand.fieldsToHide.containsAll(this.fieldsToHide);

            return hasEqualFieldsToShow && hasEqualFieldsToHide;
        }
        return false;
    }

    private static List<String> getAlreadyVisibleFields(Model model) {
        return model.getVisibleFields();
    }

    private static List<String> getAlreadyHiddenFields(Model model) {
        return model.getHiddenFields();
    }

    private static boolean isValidSubsetOfAlreadyVisibleFields(Model model, List<String> inputList) {
        List<String> alreadyVisibleFields = getAlreadyVisibleFields(model);
        return alreadyVisibleFields.containsAll(inputList);
    }

    public static void requireAtLeastOneVisibleColumn(List<String> fieldsToShow) throws CommandException {
        if (fieldsToShow.isEmpty()) {
            throw new CommandException(AT_LEAST_ONE_VISIBLE_COLUMN);
        }
    }

    public static void requireValidList(Model model, List<String> inputList) throws CommandException {
        if (!isValidSubsetOfAlreadyVisibleFields(model, inputList)) {
            throw new CommandException(INVALID_SUBSET);
        }
    }

    public static List<String> getUnionOfFieldsToHideAndAlreadyHiddenFields(Model model, List<String> inputList) {
        List<String> unionList = new ArrayList<>(getAlreadyHiddenFields(model));
        unionList.addAll(inputList);
        return unionList;
    }
}
