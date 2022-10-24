package seedu.rc4hdb.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.rc4hdb.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.rc4hdb.logic.commands.modelcommands.ModelCommandTestUtil.EMAIL_DESC_AMY;
import static seedu.rc4hdb.logic.commands.modelcommands.ModelCommandTestUtil.GENDER_DESC_AMY;
import static seedu.rc4hdb.logic.commands.modelcommands.ModelCommandTestUtil.HOUSE_DESC_AMY;
import static seedu.rc4hdb.logic.commands.modelcommands.ModelCommandTestUtil.MATRIC_NUMBER_DESC_AMY;
import static seedu.rc4hdb.logic.commands.modelcommands.ModelCommandTestUtil.NAME_DESC_AMY;
import static seedu.rc4hdb.logic.commands.modelcommands.ModelCommandTestUtil.PHONE_DESC_AMY;
import static seedu.rc4hdb.logic.commands.modelcommands.ModelCommandTestUtil.ROOM_DESC_AMY;
import static seedu.rc4hdb.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.rc4hdb.logic.commands.CommandResult;
import seedu.rc4hdb.logic.commands.exceptions.CommandException;
import seedu.rc4hdb.logic.commands.misccommands.HelpCommand;
import seedu.rc4hdb.logic.commands.modelcommands.AddCommand;
import seedu.rc4hdb.logic.commands.modelcommands.ListCommand;
import seedu.rc4hdb.logic.commands.storagecommands.filecommands.FileCommand;
import seedu.rc4hdb.logic.commands.storagecommands.filecommands.jsonfilecommands.FileCreateCommand;
import seedu.rc4hdb.logic.commands.storagecommands.filecommands.jsonfilecommands.FileSwitchCommand;
import seedu.rc4hdb.logic.parser.exceptions.ParseException;
import seedu.rc4hdb.model.Model;
import seedu.rc4hdb.model.ReadOnlyResidentBook;
import seedu.rc4hdb.model.ResidentBook;
import seedu.rc4hdb.model.resident.Resident;
import seedu.rc4hdb.storage.Storage;

public class LogicManagerTest {

    private Model model;
    private Storage storage;
    private Logic logic;

    @BeforeEach
    public void setUp() {
        model = new ModelStubForLogicManagerTest();
        storage = new StorageStubForLogicManagerTest();
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertExceptionFromExecution(invalidCommand, MESSAGE_UNKNOWN_COMMAND, ParseException.class);
    }

    @Test
    public void execute_validMiscCommand_success() throws Exception {
        String helpCommand = HelpCommand.COMMAND_WORD;
        assertCommandSuccess(helpCommand, HelpCommand.SHOWING_HELP_MESSAGE);
    }

    @Test
    public void execute_validModelCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        model = new ModelStubForListCommand();
        logic = new LogicManager(model, storage);
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS);
    }

    @Test
    public void execute_validStorageCommand_success() throws Exception {
        String fileCreateCommand = FileCommand.COMMAND_WORD + " " + FileCreateCommand.COMMAND_WORD + " test";
        storage = new StorageStubForFileCreate();
        logic = new LogicManager(model, storage);
        assertCommandSuccess(fileCreateCommand, String.format(FileCreateCommand.MESSAGE_SUCCESS, "test.json"));
    }

    @Test
    public void execute_validStorageModelCommand_success() throws Exception {
        String fileSwitchCommand = FileCommand.COMMAND_WORD + " " + FileSwitchCommand.COMMAND_WORD + " residentBook1";
        model = new ModelStubForFileSwitch();
        storage = new StorageStubForFileSwitch();
        logic = new LogicManager(model, storage);
        assertCommandSuccess(fileSwitchCommand, String.format(FileSwitchCommand.MESSAGE_SUCCESS, "residentBook1.json"));
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        // Setup LogicManager with JsonResidentBookIoExceptionThrowingStub
        model = new ModelStubForIoExceptionCase();
        storage = new StorageIoExceptionThrowingStub();
        logic = new LogicManager(model, storage);

        // Execute add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY
                + ROOM_DESC_AMY + GENDER_DESC_AMY + HOUSE_DESC_AMY + MATRIC_NUMBER_DESC_AMY;
        String expectedMessage = LogicManager.FILE_OPS_ERROR_MESSAGE
                + StorageIoExceptionThrowingStub.DUMMY_IO_EXCEPTION;
        assertExceptionFromExecution(addCommand, expectedMessage, CommandException.class);
    }

    @Test
    public void getFilteredResidentList_modifyList_throwsUnsupportedOperationException() {
        model = new ModelStubForGetFilteredResidentListMethod();
        logic = new LogicManager(model, storage);
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredResidentList().remove(0));
    }

    //======================== Start of helper functions ===============================================

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage)
            throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     */
    private void assertExceptionFromExecution(String inputCommand, String expectedMessage,
            Class<? extends Throwable> expectedException) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
    }

    //======================== Start of model stubs ===============================================

    /**
     * A Model stub to ignore the methods that {@code LogicManager} invokes on {@code Model}.
     */
    private static class ModelStubForLogicManagerTest extends ModelStub {
        @Override
        public ReadOnlyResidentBook getResidentBook() {
            return new ResidentBook();
        }
    }

    /**
     * A model stub for testing {@code LogicManager}, {@code StorageModelCommand} case, which uses
     * {@code FileSwitchCommand}.
     */
    private static class ModelStubForFileSwitch extends ModelStubForLogicManagerTest {
        @Override
        public void setResidentBook(ReadOnlyResidentBook residentBook) {
            // does nothing
        }

        @Override
        public void setResidentBookFilePath(Path filePath) {
            // does nothing
        }
    }

    /**
     * A model stub for testing {@code LogicManager}, Storage throws IOException case, which uses
     * {@code AddCommand}.
     */
    private static class ModelStubForIoExceptionCase extends ModelStubForLogicManagerTest {
        @Override
        public boolean hasResident(Resident resident) {
            return false;
        }

        @Override
        public void addResident(Resident resident) {
            // does nothing
        }
    }

    /**
     * A model stub for testing {@code LogicManager}, {@code ModelCommand} case, which uses {@code ListCommand}.
     */
    private static class ModelStubForListCommand extends ModelStubForLogicManagerTest {
        @Override
        public void updateFilteredResidentList(Predicate<Resident> predicate) {
            // do nothing
        }

        @Override
        public void setVisibleFields(List<String> fieldsToShow) {
            // do nothing
        }

        @Override
        public void setHiddenFields(List<String> fieldsToHide) {
            // do nothing
        }
    }

    /**
     * A model stub for testing {@code LogicManager}, {@code ModelCommand} failure case, which uses
     * {@code DeleteCommand}.
     */
    private static class ModelStubForDeleteCommand extends ModelStubForLogicManagerTest {
        @Override
        public ObservableList<Resident> getFilteredResidentList() {
            return new FilteredList<>(FXCollections.unmodifiableObservableList(
                    FXCollections.observableArrayList()));
        }
    }

    /**
     * A model stub for testing {@code LogicManager} getFilteredResidentList method. Invocation of
     * getFilteredResidentList method results in an empty FilteredList.
     */
    private static class ModelStubForGetFilteredResidentListMethod extends ModelStubForDeleteCommand {
        // same as ModelStubForDeleteCommand
    }

    //======================== Start of storage stubs ===============================================

    /**
     * A storage stub class to ignore the methods that {@code LogicManager} invokes on {@code Storage}.
     */
    private static class StorageStubForLogicManagerTest extends StorageStub {
        @Override
        public void saveResidentBook(ReadOnlyResidentBook residentBook) throws IOException {
            // does nothing
        }
    }

    /**
     * A storage stub class to throw an {@code IOException} when the save method is called.
     */
    private static class StorageIoExceptionThrowingStub extends StorageStubForLogicManagerTest {
        public static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy exception");

        @Override
        public void saveResidentBook(ReadOnlyResidentBook residentBook) throws IOException {
            throw DUMMY_IO_EXCEPTION;
        }
    }

    /**
     * A storage stub for testing {@code LogicManager}, {@code StorageModelCommand} case, which uses
     * {@code FileSwitchCommand}.
     */
    private static class StorageStubForFileSwitch extends StorageStubForLogicManagerTest {
        public static final String DUMMY_PATH_STRING_NO_DIR = "dummy";
        public static final Path DUMMY_PATH = Paths.get("data", DUMMY_PATH_STRING_NO_DIR + ".json");

        @Override
        public Optional<ReadOnlyResidentBook> readResidentBook(Path filePath) {
            return Optional.of(new ResidentBook());
        }

        @Override
        public void setResidentBookFilePath(Path filePath) {
            // does nothing
        }

        @Override
        public Path getResidentBookFilePath() {
            return DUMMY_PATH;
        }
    }

    /**
     * A storage stub for testing {@code LogicManager}, {@code StorageCommand} case, which uses
     * {@code FileCreateCommand}.
     */
    private static class StorageStubForFileCreate extends StorageStubForLogicManagerTest {
        @Override
        public void createResidentBookFile(Path filePath) throws IOException {
            // does nothing
        }

        @Override
        public Path getResidentBookFilePath() {
            return null;
        }
    }
}
