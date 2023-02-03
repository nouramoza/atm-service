package com.egs.atmservice.util;

public final class ConstantsUtil {

    ConstantsUtil() {
    }

    public static class SessionKey {
        public static final String CARD_NUMBER = "cardNumber";
        public static final String JWT = "jwt";
        public static final String PIN_ACCEPTED = "pinAccepted";
        public static final String ERROR = "error";

        private SessionKey() {
        }
    }

    public static class CommonMessage {
        public static final String YOUR_BALANCE = "Your Balance is: ";
        public static final String SUCCESS_DEPOSIT = "Deposit Done Successful. Your new Balance is: ";
        public static final String SUCCESS_WITHDRAW = "Withdraw Done Successful. Your new Balance is: ";

        private CommonMessage() {
        }
    }

    public static class ResponseMessage {
        public static final String CARD_ACCEPTED = "Card Number Is Valid.";

        private ResponseMessage() {
        }
    }

}