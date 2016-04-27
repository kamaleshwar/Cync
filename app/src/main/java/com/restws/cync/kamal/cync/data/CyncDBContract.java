package com.restws.cync.kamal.cync.data;

import android.provider.BaseColumns;

public class CyncDBContract
{
    public static final class ContactsEntry
            implements BaseColumns
    {
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_CONTACT_ID = "contact_id";
        public static final String COLUMN_NAME = "contact_name";
        public static final String COLUMN_NUMBER = "phone_number";
        public static final String TABLE_NAME = "tContacts";

    }

    public static final class RegistrationEntry
            implements BaseColumns
    {
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_REGISTER_STATUS = "status";
        public static final String TABLE_NAME = "tRegister";

    }

    public static final class ServerContactsEntry
            implements BaseColumns
    {
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_IP = "IP";
        public static final String COLUMN_NUMBER = "contact_number";
        public static final String TABLE_NAME = "tserverContacts";
    }

    public static final class CurrentContactEntry
            implements BaseColumns
    {
        public static final String COLUMN_ID = "_id";
        public static final String TABLE_NAME = "tCurrentContact";
        public static final String COLUMN_CURRENT_CONTACT= "current_contact";

    }
}
