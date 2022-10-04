package seedu.address.logic.commands;


import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.AttributesMatchKeywordsPredicate;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

public class FilterCommand extends Command {
    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": filters the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_FILTER_PERSON_SUCCESS = "filtered Person: %1$s";
    public static final String MESSAGE_NOT_FILTERED = "At least one field to filter must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final FilterPersonDescriptor filterPersonDescriptor;

    /**
     * @param filterPersonDescriptor details to filter the person with
     */
    public FilterCommand(FilterCommand.FilterPersonDescriptor filterPersonDescriptor) {
        requireNonNull(filterPersonDescriptor);
        this.filterPersonDescriptor = new FilterCommand.FilterPersonDescriptor(filterPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        //return new CommandResult("Command Still in progress");
        requireNonNull(model);
        AttributesMatchKeywordsPredicate predicate =
                new AttributesMatchKeywordsPredicate(filterPersonDescriptor);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    /**
     * Stores the details to filter the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class FilterPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Set<Tag> tags;

        public FilterPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public FilterPersonDescriptor(FilterCommand.FilterPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is filtered.
         */
        public boolean isAnyFieldfiltered() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Name getName() {
            return name;
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Phone getPhone() {
            return phone;
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Email getEmail() {
            return email;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Address getAddress() {
            return address;
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Set<Tag> getTags() {
            return tags;
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof FilterCommand.FilterPersonDescriptor)) {
                return false;
            }

            // state check
            FilterCommand.FilterPersonDescriptor e = (FilterCommand.FilterPersonDescriptor) other;

            return getName().equals(e.getName())
                    && getPhone().equals(e.getPhone())
                    && getEmail().equals(e.getEmail())
                    && getAddress().equals(e.getAddress())
                    && getTags().equals(e.getTags());
        }
    }

}
