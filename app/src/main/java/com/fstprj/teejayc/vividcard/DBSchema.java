package com.fstprj.teejayc.vividcard;

//TODO: find way to save numForget in infinite integer
public class DBSchema {
    public static final class CommonAtt {
        public static final String ID = "id";
        public static final String DATE = "date";
        public static final String NAME = "name";
    }

    public static final class CardTable {
        public static final String NAME = "cards";

        public static final class Attributes {
            public static final String PID = "pid";
            public static final String ID = "id";
            public static final String DATE = "date";
            public static final String NAME = "name";
            public static final String DETAIL = "detail";
            public static final String COLOR = "color";
            public static final String IMAGE = "image";
            public static final String LAST_VISIT_DATE = "last_visit_date";
            public static final String NUM_FORGET = "num_forget";
            public static final String NUM_FORGET_OVER_NUM_DATES = "num_forget_over_dates";
            public static final String CREATOR = "creator";
        }
    }

    public static final class DeckTable {
        public static final String NAME = "decks";

        public static final class Attributes {
            public static final String PID = "pid";
            public static final String ID = "id";
            public static final String DATE = "date";
            public static final String NAME = "name";
            public static final String CREATOR = "creator";
        }
    }

    public static final class DirectoryTable {
        public static final String NAME = "directories";

        public static final class Attributes {
            public static final String ID = "id";
            public static final String DATE = "date";
            public static final String NAME = "name";
        }
    }
}
