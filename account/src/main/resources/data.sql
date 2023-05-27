CREATE TABLE account (
                         id UUID PRIMARY KEY,
                         account_number VARCHAR(255) NULL,
                         password VARCHAR(255) NOT NULL,
                         currency_code VARCHAR(255) NOT NULL,
                         alias VARCHAR(255) NULL,
                         user_number VARCHAR(255) NULL UNIQUE,
                         user_first_name VARCHAR(255) NOT NULL,
                         user_last_name VARCHAR(255) NOT NULL,
                         user_phone_country_code VARCHAR(255) NOT NULL,
                         user_phone_number VARCHAR(255) NOT NULL UNIQUE,
                         user_email VARCHAR(255) NULL UNIQUE,
                         address_line1 VARCHAR(255) NULL,
                         address_line2 VARCHAR(255) NULL,
                         zip_postal_code VARCHAR(255) NULL,
                         state_province_code VARCHAR(255) NULL
);

CREATE TABLE accessToken (
                         id UUID PRIMARY KEY,
                         access_token VARCHAR(255) NULL,
                         userPhoneNumber VARCHAR(255) NOT NULL
);