package Model;

public class User {
    private String name, shippingAddress, emailAddress, cardNumber, cvv, expiryDate, accType, student;

    public User() {

    }

    public String getName() {
        return name;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getAccType() {
        return accType;
    }

    public String getStudent() {
        return student;
    }

    private User(final Builder builder) {
        name = builder.name;
        shippingAddress = builder.shippingAddress;
        emailAddress = builder.emailAddress;
        cardNumber = builder.cardNumber;
        cvv = builder.cvv;
        expiryDate = builder.expiryDate;
        accType = builder.accType;
        student = builder.student;
    }

    public static class Builder {
        private String name, shippingAddress, emailAddress, cardNumber, cvv, expiryDate, accType, student;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setShippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }

        public Builder setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public Builder setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder setCvv(String cvv) {
            this.cvv = cvv;
            return this;
        }

        public Builder setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder setAccType(String accType) {
            this.accType = accType;
            return this;
        }

        public Builder setStudent(String student) {
            this.student = student;
            return this;
        }

        public User create() {
            return new User(this);
        }
    }
}
