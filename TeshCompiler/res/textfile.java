public class textfile {
    public String name;
    public int error;
    public String directory;

    public textfile() {
        name = new String();
        error = 0;
        directory = new String();
    }

    public textfile clone() {
        textfile b = new textfile();
        b.name = this.name;
        b.error = this.error;
        b.directory = this.directory;
        return b;
    }
}