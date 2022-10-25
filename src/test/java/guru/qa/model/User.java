package guru.qa.model;

import java.util.List;

public class User {
    public int id;
    public String name;
    public List<File> fileList;

    public static class File {
        public int id;
        public String name;
    }
}
