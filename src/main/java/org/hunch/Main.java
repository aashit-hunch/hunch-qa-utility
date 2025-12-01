package org.hunch;


import com.github.javafaker.Faker;
import org.hunch.constants.Config;
import org.hunch.enums.Gender;
import org.hunch.models.FirebaseServiceAccount;
import org.hunch.models.SetupUserV2;
import org.hunch.utils.Common;
import org.hunch.utils.CryptoUtility;

import java.util.Collections;
import java.util.UUID;

public class Main {
    static void main() {
        Faker k = new Faker();
/*
        IO.println("Project Id : "+CryptoUtility.encrypt( Config.FIREBASE_PROJECT_ID));
        IO.println("Private Key Id :"+CryptoUtility.encrypt(Config.FIREBASE_PRIVATE_KEY_ID));
        IO.println("Private Key : "+CryptoUtility.encrypt("-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDFy8YdcXwM4S3s\nlaQDh62+ue0EexLTcyZSesvZKsbzECG/U0lKiMj6SjgGIgmBQDpjPFunUcOiJABw\nhjAY021vYmKk6DimMJVcNNKhsl76OL1l0hir/THqeEJ+DhwXUJUCbqTxpVRSbqUk\n3xwFto+YE85OlIxE0ykBd3xeHrpRutBvL4aLgKFfSwCceRkE+g1yPknXhABRojza\n/7C9TeAaiX74rz7IYbxek0rQ/O/i1ArFnrGh9fJ1tE9cWVAEW788zupjLGIVl8Zr\nI2uC2u+X9VvgYNpCxvJ/pyPjygmztYXFKP4n8WmgFV1qNtVhJZrkVQLf+UT6VF2V\nNur7xV/TAgMBAAECggEAC+5u9nkeHwn5HIOp7G6WsmHol42AnrgcMGIcG6I2QXsQ\nVhUexci6uuJkPvZ2dVE1bgwGvIqbNWCPlQbhlHXLuaoJKK87aS1jRrftZsSdCkht\nU4/ykT/JbySu+iFwXCjhVc+GOQkC+gmWztxIhlqAKFyb5Gb9XFKsInY3NQqmEzNW\nMKHjc4USQXquEdUTihiT4kVx+bzxmxmiJJYp6EJfCq5zbn5eMyIc1Gwe65O0qqNe\nE66hUeX9TPhuhUHfcTSVSxF1Zw8ZQj/q+OX7FLp8PA02XXeCcMCkbIsZvxY/AQqY\na7MuPeBhlmByRFnfMbWTE70NKd2wJ38VtF5fwG4OpQKBgQDvtcW0O3P4wvra6yEn\n3aRuZ4X1JKffKolDeapXXtMuD/ta9Kt5WLjLyLxvgBrXr/StlEi6HTCkQ6kzG2HR\nIK3ocM4UWBtI+vDsPujVy42BBdCqs98tIl9LoK3YyQI85KYa3xuIog+fuMw5KVVe\nfnB80fx8d7WL1ENfKCks0aEMHQKBgQDTPNL8g38kdAaqWDx7FfWVOMDS5XPX8x2H\nucHfxnJL6uWAj+aWDDGhE1pFE1z/p0GeDJRv1weaTKL4Zim/EgrwWk7I6oHjktp5\nbRxnRBPpWjXj5GIHj5KbJQTQayzxbDXOscgLXV5/iKzKtX9RNjS4WHffj+/tePo7\nZz1vMBr4rwKBgQCvk8HbEM0SQabCoQTpEWR2Zadt9sprINnnrX8CffdKvvQKHDpZ\nKI463a6AMSH4J+6dEYbvo5/UQab2QzEzn68iGTYpGpvpqby8HhwnNPBRt/OuhQ+M\nHKgWWfvcVOcpuQSdnBuryaCOCiq0F71q+EvmcVxxwrEcWX57fYwjQuymVQKBgD0e\nMiWhnl3bvDn5QbaTSfUc8J2UigoHa5njozat4lAY7MJf4GW1rX5fbRTjX1sQ96lV\nFnWtj6OTXOvmE1oXTFbyqizzvRUtXk0XxsoSJni0Azem0r5BSjfdZCExgVVw67ic\nM7cmErtvvh8AmI/U0TgtULG8obzh7fTWEC5lisg9AoGAY73IkRW1fFx31VwsFNgB\n79RAIiK5dYA7ybz1w3GwQhpITEZcz4HrRGsasfqMgN0QPtiYf/HfBTF/nVcNLnUR\nZmJIazg62jIHTFbE6XSVV8V9OkqQ1nTCs//yqy14hPQ9tFJRyeg33d2SX6CCmfBq\n8ErilcmwU/7Uv8Qwd6hcYZs=\n-----END PRIVATE KEY-----\n"));
        IO.println("Client email : "+CryptoUtility.encrypt(Config.FIREBASE_CLIENT_EMAIL));
        IO.println("Client Id :"+CryptoUtility.encrypt(Config.FIREBASE_CLIENT_ID));
        IO.println("Cert URL : "+CryptoUtility.encrypt(Config.FIREBASE_CLIENT_X509_CERT_URL));
        IO.println("Firebase Key : "+CryptoUtility.encrypt(Config.FIREBASE_API_KEY));
*/
        IO.println("GEN : "+ Collections.singleton(Common.randomEnum(Gender.class).getString()));
        IO.println(new FirebaseServiceAccount().toString());

        SetupUserV2 se = new SetupUserV2();
        IO.println("Body : "+Common.mapper.writeValueAsString(se.setRandomData()));
        IO.println(k.number().numberBetween(6000000000L,9999999999L));

        IO.println(UUID.randomUUID());

        IO.println("User : "+CryptoUtility.encrypt( "dev_rw"));
        IO.println("Pass : "+CryptoUtility.encrypt( "x|.J8DzU3tz3"));
        IO.println("Dec USER"+CryptoUtility.decrypt(Config.DB_USER));
        IO.println("Dec PASS"+CryptoUtility.decrypt(Config.DB_PASS));
        IO.println("Dec NAME"+CryptoUtility.decrypt(Config.DB_NAME));


    }
}
