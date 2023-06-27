package org.indritbreti.restprocessordemo.exceptions.mailSender;

import org.indritbreti.restprocessordemo.util.Utilities;
import jakarta.mail.Address;
import jakarta.mail.SendFailedException;
import org.indritbreti.restprocessordemo.util.Utilities;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FailedToSendEmailException extends EmailSenderException {
    public List<String> invalid = new ArrayList<>();
    public List<String> validSent = new ArrayList<>();
    public List<String> validUnsent = new ArrayList<>();

    public FailedToSendEmailException(SendFailedException sendFailedException) {
        super(HttpStatus.INTERNAL_SERVER_ERROR);
        if (sendFailedException != null) {
            this.invalid = Utilities.arrayAsStream(sendFailedException.getInvalidAddresses()).map(Address::toString).collect(Collectors.toList());
            this.validSent = Utilities.arrayAsStream(sendFailedException.getValidSentAddresses()).map(Address::toString).collect(Collectors.toList());
            this.validUnsent = Utilities.arrayAsStream(sendFailedException.getValidUnsentAddresses()).map(Address::toString).collect(Collectors.toList());
            setRootException(sendFailedException);
        }
        setMessage("Failed to send email to addresses: [" + String.join(", ", invalid) + "]");

    }

}
